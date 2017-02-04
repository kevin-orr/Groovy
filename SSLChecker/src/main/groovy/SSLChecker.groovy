import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.config.DefaultClientConfig

import javax.net.ssl.*
import javax.security.cert.CertificateException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate

/**
 * Created by kevin orr on 03/02/2017.
 *
 * Hey, well what's it do then...
 *
 * First up this isn't about MSSL or mutual authentication were the client has to also authenticate to the server.
 * All I'm concerned about here is trying to verify a server certificate for some HTTPS endpoint.
 *
 *
 */
class SSLChecker {

    static void main(String... commandLineArgs) {

        def usageBanner = """
        Welcome to the SSL Checker...
        
        Aim it at an endpoint that uses HTTPS and it will give you some info on the site blahdy blah blah
        
        """
        def cli = new CliBuilder(usage: usageBanner)

        // now slurp up the command line arguments...
        cli.with {
            // required params
            h longOpt: 'help', 'Show usage information', required: false
            endpoint longOpt: 'endpoint', 'The url to test against', required: true, args: 1
            // optional params below
            proxy longOpt: 'proxy', 'The HTTPS proxy', required: false, args: 1
            proxyport longOpt: 'proxyport', 'The HTTPS proxy port', required: false, args: 1
            debug longOpt: 'debug', 'turn on ssl:record:plaintext', required: false, args: 0
        }

        // next actually do the parsing of the command line arguments
        OptionAccessor opt = cli.parse(commandLineArgs)
        assert opt, "Some or all required arguments are missing - can't do nothing without them dude!"

        if (opt?.h || !commandLineArgs) {
            System.err << cli.usage()
            assert commandLineArgs, 'Some or all required parameters missing'
        }

        final endpoint = opt?.endpoint?.trim()
        assert endpoint, "Endpoint can't be null"

        if (opt?.debug) System.setProperty('javax.net.debug', 'ssl:record:plaintext')

        if (opt?.proxy) System.setProperty('https.proxyHost', opt.proxy)

        if (opt?.proxyport) System.setProperty('https.proxyPort', opt.proxyport)

        def checker = new SSLChecker()

        def chain = checker.getCertChain endpoint

        assert chain, 'Failed to get certificate chain'

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
    def getCertChain(endpoint, port = 443) {
        TrustManagerLite tm
        def errors = []
        try {
            // sorry, there's a lot of setting up here but it's kinda like a Russian dolls...
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType())

            SSLContext context = SSLContext.getInstance("TLS")

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

            tmf.init(ks)
            // we're going to use this guy to attempt the real PKIX stuff and capture any errors that plop out
            X509TrustManager defaultTrustManager = tmf.getTrustManagers().first()

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
            System.out << '=' * 120 << '\n'
            System.out << "Response ${response.status}:${response.clientResponseStatus}\n"
            System.out << '=' * 120 << '\n'

            // ok, if we got here then chain has passed PKIX path validation.

        } catch (Exception e) {
            // ok, so I reckon the SSL handshake has blown up - we've probably got a chain but validation failed.
            errors << e.message
        }
        // get the X509Certificate chain
        def chain = tm?.chain

        new Tuple2<>(chain, errors)
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
