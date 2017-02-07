import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.config.DefaultClientConfig
import sun.security.provider.certpath.OCSP
import sun.security.provider.certpath.URICertStore
import sun.security.x509.AccessDescription
import sun.security.x509.X509CertImpl

import javax.net.ssl.*
import javax.security.cert.CertificateException
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.*

import static java.lang.System.err as err
import static java.lang.System.out as out

/**
 * Created by kevin orr on 03/02/2017.
 *
 * Hey, well what's it do then...
 *
 * First up this isn't about MSSL or mutual authentication were the client has to also authenticate to the server.
 * All I'm concerned about here is trying to verify a server certificate for some HTTPS endpoint.
 *

 */
class SSLChecker {

    final static endl = '\n'

    static void main(String... commandLineArgs) {

        def usageBanner = """
        Welcome to the SSL Checker...
        
        Aim it at an host that uses HTTPS and it will give you some info
        on the site blahdy blah blah
        
        """.stripIndent()

        def cli = new CliBuilder(usage: usageBanner)

        // now slurp up the command line arguments...
        cli.with {
            // these are required params
            h longOpt:         'help',      'Show usage information',       required: false
            host longOpt:      'host',     'The host name test against',   required: true,  args: 1
            // while theres are optional params below
            ocsp  longOpt:     'ocsp',      'Use OCSP to validate',         required: false, args: 0
            savechain longOpt: 'savechain', 'Save chain to disk',           required: false, args: 1
            saveaia longOpt:   'saveaia',   'Save the AIA certs to disk',   required: false, args: 1
            proxy longOpt:     'proxy',     'The HTTPS proxy',              required: false, args: 1
            proxyport longOpt: 'proxyport', 'The HTTPS proxy port',         required: false, args: 1
            debug longOpt:     'debug',     'turn on ssl:record:plaintext', required: false, args: 0
        }

        // next actually do the parsing of the command line arguments
        OptionAccessor opt = cli.parse(commandLineArgs)

        if (opt?.h || !commandLineArgs) {
            System.exit(0)
        }

        final hostname = opt?.host?.trim()

        assert hostname, "hostname can't be null"

        if (opt?.debug) System.setProperty('javax.net.debug', 'ssl:record:plaintext')

        if (opt?.proxy) System.setProperty('https.proxyHost', opt.proxy)

        if (opt?.proxyport) System.setProperty('https.proxyPort', opt.proxyport)

        def checker = new SSLChecker()

        showIt "Attempting to pull down certificate chain from https://www.${hostname}"

        def chain = checker.getCertChain(hostname)

        assert chain, 'Failed to get certificate chain - cannot really do anything else but quit - sorry!'

        showIt "We've got a chain of ${chain.length} certificates...here's more details on the chain:"

        // display some info on chain
        checker.chainInfo(chain)

        showIt "Next attempting to validate the chain separately..."
        Set<TrustAnchor> trustAnchor = new HashSet<>()
        trustAnchor.add(new TrustAnchor(chain.last(), null))
        def valid = checker.isCertChainValid(chain.init(), trustAnchor)

        // ok, so what do we know so far...
        if (!valid) {
            showIt "Failed! OK, so looks like the trust anchor didn't come down in chain from server. " +
                    "Lets see there's an OCSP service inside Authority " +
                    "Info Access Extension in the certs...turn on the -ocsp flag to check against OCSP",
                    err
        } else {
            showIt "Successfully validated server! It looks like we can trust host $hostname - but don't take my word for it..."
        }

        // have we got an OCSP service url?
        if (opt?.ocsp) {
            showIt 'Ok, so looking for OCSP urls in chain extensions...'

            checker.checkOCSP(chain)
        }

        showIt 'Finished! Bye bye!'
    }

    /**
     * Looks at the AIA extension in each of the certs in chain and tries to validate them against any OCSP provider
     * that appears in the cert extension
     * @param chain the chain of certificates from the server
     * @return nowt but good wishes
     */
    public checkOCSP(X509Certificate[] chain) {
        def ocsp = chain.collect { OCSP.getResponderURI(it as X509Certificate) }
        if (ocsp) {

            showIt "Found OCSP service url:${ocsp.first()} - trying each cert in chain against OCSP provider..."

            OCSP.RevocationStatus response
            chain.eachWithIndex { x509, index ->
                showIt "Trying certificate chain[$index] with serial number: ${x509.serialNumber.toString(16)}"

                def certFromExtension = getCertFromAIAExtension(x509)

                try {
                    if (certFromExtension) {
                        showIt "Contacting OCSP service using chain[$index] and cert in AIA extension as parameters..."

                        response = OCSP.check(x509, certFromExtension as X509Certificate)

                        if (response?.certStatus == OCSP.RevocationStatus.CertStatus.GOOD) showIt('Service says this cert is GOOD!')
                        else showIt("Service says this cert is ${response.certStatus}")

                    } else {
                        showIt "No AIA extension for chain[$index]"
                    }

                } catch (Exception hmmm) {
                    showIt "Ok, chain[$index] seems to be screwed:${hmmm}", err
                }

            }
        } else {
            showIt 'No OCSP urls found - sorry!', err
        }
    }

    /**
     * we need to do the validation (or not) of the cert chain during the SSL handshake with server.
     * Remember we don't actually care if the chain is valid or not, we just want the chain
     * we also assume no trust anchors available in a trust store
     * The underlying client framework (jersey) may blow up during pahh validation (remember, we
     * delegate the call on to a 'proper' trust manager) - this could be for many reasons which will probably pump
     * out to the user. One other reason could be that the the intermediate certs are using AuthorityInfoAccess
     * which will basically point us to where the trust anchor is (the server may have sent down a chain of certs
     * but the last one may not have been the trust anchor needed to validate the chain)
     */
    def getCertChain(hostname) {
        TrustManagerLite tm
        try {
            // sorry, there's a lot of setting up here but it's kinda like a Russian dolls...
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType())

            SSLContext context = SSLContext.getInstance("TLS")

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

            tmf.init(ks)
            // we're going to use this guy to attempt the real PKIX stuff and capture any errors that plop out
            X509TrustManager defaultTrustManager = tmf.trustManagers.first()

            // remember this guy from down below? She's gonna capture the chain of certs, hopefully...
            tm = new TrustManagerLite(defaultTrustManager)

            context.init(null, [tm] as TrustManager[], new SecureRandom())

            // now set our trust manager to be the one used for all SSL connections
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory())

            // lets use jersey for making calls out to endpoint
            def config = new DefaultClientConfig()
            def client = Client.create(config)
            def webSvc = client.resource("https://www.$hostname")
            def response = webSvc.head()

            showIt "Response ${response.status}:${response.clientResponseStatus}\n"

            // ok, if we got here then chain has passed PKIX path validation.

        } catch (Exception e) {
            // ok, so I reckon the SSL handshake has blown up - we've probably got a chain but validation failed.
            showIt "Looks like we have an issue with the SSLHandshake - perhaps we can't build path:${e.message}", err
        }
        // get the X509Certificate chain
        tm?.chain
    }

    /**
     * attempts to validate the certificate chain using PKIX
     * @param chain the chain to validate - should include the trust anchor
     * @param trustAnchorOrKeystore the trust anchor or a key store that holds a trust anchor, that we'll
     * attempt to validate up to
     * @param revocationEnabled if true then do not use the CRL distribution points for checking revocation
     * @return true if the chain is PKIX ok, false otherwise
     */
    def isCertChainValid(def chain, def trustAnchorOrKeystore, boolean revocationEnabled = true) {

        // next create CertPathValidator that implements the 'PKIX' algorithm...
        CertPathValidator cpv
        CertPath cp
        try {
            // instantiate a CertificateFactory for X.509 and
            // extract the certification path from the List of Certificates
            cp = CertificateFactory.getInstance('X.509')
                                   .generateCertPath(chain as List<Certificate>)


            cpv = CertPathValidator.getInstance('PKIX')

        } catch (Exception nsae) {
            showIt nsae.message
            return false
        }
        // next validate certification path ("cp") with specified parameters ("params")...
        try {
            PKIXParameters params = new PKIXParameters(trustAnchorOrKeystore)
            // have we been asked to turn off CRL checking?
            params.revocationEnabled = revocationEnabled

            CertPathValidatorResult cpvResult = cpv.validate(cp, params)

            // ok, looks good

        } catch (InvalidAlgorithmParameterException iape) {
            showIt iape.message
            return false

        } catch (CertPathValidatorException cpve) {
            showIt "Got a problem: ${cpve.message} and the index of certificate that caused exception: ${cpve.getIndex()}", err
            return false
        }
        true
    }

    /**
     * pulls out the cert pointed at by the AIA extension - if the cert has one obviously!
     * @param x509 the cert to work on
     * @return the cert pointed to or null if there's no cert
     */
    def getCertFromAIAExtension(X509Certificate x509) {

        def aia = (x509 as X509CertImpl)?.authorityInfoAccessExtension?.accessDescriptions?.grep { description ->
                                            description.accessMethod.equals(AccessDescription.Ad_CAISSUERS_Id)
                                        }

        if (aia) URICertStore.getInstance(aia.first()).getCertificates(new X509CertSelector())?.first()
        else null
    }

    /** displays some info on the cert chain */
    def chainInfo(final chain, final stream = out) {
        withDecoration(stream) { outputStream ->
            chain.eachWithIndex { X509CertImpl cert, index->
                outputStream << "Here's certificate info for chain[$index]..." << endl << endl
                outputStream << "subject:       ${cert.subjectDN}" << endl
                outputStream << "issuer:        ${cert.issuerDN}" << endl
                outputStream << "Serial Number: ${cert.serialNumber.toString(16)}" << endl

                def cas = cert?.authorityInfoAccessExtension?.accessDescriptions?.grep { description ->
                                  description.accessMethod.equals(AccessDescription.Ad_CAISSUERS_Id)
                              }

                outputStream << "Found ${cas?.size} Authority Info Access Extension..." << endl

                cas?.each { AccessDescription description ->
                    outputStream << description.accessLocation.name << endl
                    def x509 = getCertFromAIAExtension(cert as X509Certificate)
//                    chainInfo([x509] as X509Certificate[], out)
                    out << '+' * 120 << endl
                    out << "\tHere's some info on certificate pointed at in AIA extension..." << endl
                    out << "\tsubject:       ${x509.subjectDN}" << endl
                    out << "\tissuer:        ${x509.issuerDN}" << endl
                    out << "\tSerial Number: ${x509.serialNumber.toString(16)}" << endl
                    out << '+' * 120 << endl

                }
                outputStream << endl * 2
            }
        }
    }

    /** some helper dudes... */
    static def showIt(message, stream = out) {
        withDecoration(stream, ''){ outputStream ->
            outputStream << "$message$endl"
        }
    }
    static def withDecoration(final toDestinationStream = err, final deco = '-', final Closure what) {
        toDestinationStream << deco * 120 << endl
        what(toDestinationStream)
        toDestinationStream << deco * 120 << endl
    }


    /**
     * Ordinarily you'd refrain from supplying your own TrustManager preferring
     * instead to let the PKIX guy do all the heavy lifting required in path validation etc.
     *
     * But in our case we want to trap the call to checkServerTrusted() made during the
     * SSL handshake, from inside ClientHandshaker.java
     *
     * We'll also attempt to delegate the actual validation to a trust manager so we can
     * report on the validation, but essentially we want to capture the chain that's
     * sent down for validation.
     */
    private static class TrustManagerLite implements X509TrustManager {
        private final X509TrustManager tm
        private X509Certificate[] chain

        TrustManagerLite(X509TrustManager tm) {
            this.tm = tm
        }
        /** do not care about this for our use case */
        X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException()
        }
        /** do not care about this for our use case */
        void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            throw new UnsupportedOperationException()
        }
        /** we do care about this - get that chain and delegate the validation so we can report */
        void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain
            tm.checkServerTrusted(chain, authType)
        }
    }
}
