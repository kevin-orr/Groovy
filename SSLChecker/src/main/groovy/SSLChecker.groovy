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


}
