import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException
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
