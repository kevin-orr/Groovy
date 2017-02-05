# SSL Checker

Chatting [HTTPS](SSLChecker) to a server can certainly throw up many issues and I'm not even talking about
mutual authentication (2 way SSL or MSSL), where you, as the client talking to the server, have to authenticate
to the server with your very own cert.  

I wanted a simple way to ping a server over HTTPS and see if the server could be trusted.


There's lots of reasons why you might not trust a server - the trust anchor for the server's cert may not be in your
trust store, or perhaps the wrong certs are being pulled down because taffic is going via a proxy and it's not configured
correctly, maybe one or more of the certs in the cert chain are invalid.
Anyway, suffice it to say, there are lots of reasons why you might not trust a server when talking HTTPS.
So I thought it would edifying to write a small command line tool that could point at a server and it would pull down the
certificate chain, do some reporting on, perhaps even look at different extensions within the certs chain to see if you
the trust anchor is somewhere else. Anyway, more an an exercise in playing with SSL layer.


Once built unzip/untar the distribution and invoke either the shell script or batch file depending on your OS.
Running the tool without any args should bring up the help...

```java

        Welcome to the SSL Checker...

        Aim it at an endpoint that uses HTTPS and it will give you some
         info on the site blahdy blah blah


 -debug,--debug                 turn on ssl:record:plaintext
 -endpoint,--endpoint <arg>     The url to test against
 -h,--help                      Show usage information
 -proxy,--proxy <arg>           The HTTPS proxy
 -proxyport,--proxyport <arg>   The HTTPS proxy port
```


