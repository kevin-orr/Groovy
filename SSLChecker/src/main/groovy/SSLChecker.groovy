import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.config.DefaultClientConfig
import sun.security.x509.AccessDescription
import sun.security.x509.URIName
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
        
        Aim it at an endpoint that uses HTTPS and it will give you some info on the site blahdy blah blah
        
        """
        def cli = new CliBuilder(usage: usageBanner)

        // now slurp up the command line arguments...
        cli.with {
            // required params
            h longOpt:         'help',      'Show usage information',       required: false
            endpoint longOpt:  'endpoint',  'The url to test against',      required: true,  args: 1
            // optional params below
            proxy longOpt:     'proxy',     'The HTTPS proxy',              required: false, args: 1
            proxyport longOpt: 'proxyport', 'The HTTPS proxy port',         required: false, args: 1
            debug longOpt:     'debug',     'turn on ssl:record:plaintext', required: false, args: 0
        }

        // next actually do the parsing of the command line arguments
        OptionAccessor opt = cli.parse(commandLineArgs)
        assert opt, "Some or all required arguments are missing - can't do nothing without them dude!"

        if (opt?.h || !commandLineArgs) {
            err << cli.usage()
            assert commandLineArgs, 'Some or all required parameters missing'
        }

        final endpoint = opt?.endpoint?.trim()

        assert endpoint, "Endpoint can't be null"

        if (opt?.debug) System.setProperty('javax.net.debug', 'ssl:record:plaintext')

        if (opt?.proxy) System.setProperty('https.proxyHost', opt.proxy)

        if (opt?.proxyport) System.setProperty('https.proxyPort', opt.proxyport)

        def checker = new SSLChecker()

        // I prefer how Python/Scala handle tuples - come on Groovy
        def chain = checker.getCertChain endpoint

        assert chain, 'Failed to get certificate chain - cannot really do anything else but quit - sorry!'

        Set<TrustAnchor> trustAnchor = new HashSet<>()
        trustAnchor.add(new TrustAnchor(chain.last(), null))
        def valid = checker.isCertChainValid chain.init(), trustAnchor

        // ok, so what do we know so far...
        if (!valid) {
            showIt "OK, so perhaps a trust anchor didn't come down in chain - cert extension may point us to a trust anchor location...lets see what cert details tells us"
        }

        chainInfo chain

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
    def getCertChain(endpoint) {
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
            def webSvc = client.resource(endpoint)
            def response = webSvc.head()

            showIt "Response ${response.status}:${response.clientResponseStatus}\n"

            // ok, if we got here then chain has passed PKIX path validation.

        } catch (Exception e) {
            // ok, so I reckon the SSL handshake has blown up - we've probably got a chain but validation failed.
            showIt e.message
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
            showIt cpve.message
            showIt "index of certificate that caused exception: ${cpve.getIndex()}"
            return false
        }
        true
    }

    static def chainInfo(final chain) {
        withDecoration { outputStream ->
            chain.each { X509CertImpl cert ->
                outputStream << "Here's some certificate info..." << endl << endl
                outputStream << "subject:       ${cert.subjectDN}" << endl
                outputStream << "issuer:        ${cert.issuerDN}" << endl
                outputStream << "Serial Number: ${cert.serialNumber.toString(16)}" << endl

                def cas = cert.authorityInfoAccessExtension
                              .accessDescriptions
                              .grep { description ->
                                  description.accessMethod.equals(AccessDescription.Ad_CAISSUERS_Id)
                              }

                outputStream << "Found ${cas.size} Authority Info Access Extension(s)...so we have another location for trust anchor @" << endl

                cas.each { AccessDescription description ->
                    URIName uri = (URIName) description.accessLocation.name
                    outputStream << uri.URI << endl
                }
                outputStream << endl * 2
            }
        }
    }

    static def showIt(message) {
        withDecoration(out, ''){ outputStream ->
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
