# SSL Checker

Chatting [HTTPS](SSLChecker) to a server can certainly throw up many issues and I'm not even talking about
mutual authentication (2 way SSL or MSSL), where you, as the client talking to the server, have to authenticate
to the server with your very own cert.  

I wanted to write a simple tool to ping a server over HTTPS and see if the server could be trusted.

There's lots of reasons why you might not trust a server - the trust anchor for the server's cert may not be in your
trust store, or perhaps the wrong certs are being pulled down because traffic is going via a proxy and it's not configured
correctly, maybe one or more of the certs in the cert chain are invalid.

Anyway, suffice it to say, there are lots of reasons why you might not trust a server when talking HTTPS.
So I thought it would edifying to write a small command line tool that could point at a server and it would pull down the
certificate chain, do some reporting on, perhaps even look at different extensions within the certs chain to see if you
the trust anchor is somewhere else. Anyway, more an an exercise in playing with SSL layer.

That said, the tool does a few things in the following order:
* perform SSL handshake to get the cert chain from server
* display some info on the cert chain
* perform a PKIX path validation on the cert chain reporting on it's validation
* if the ocsp flag was set then it will attempt to see if the certs in chain are valida against the OCSP service in cert


The first thing to note is that if the SSL handshake succeeds then we know we've got a valid cert chain and so we can
trust the server's certificate.

However, not every server sends down the full cert chain. In this scenario, the SSL handshake will most likely fail.
The tool will still try and do a PKIX validation - but it will most likely fail. However, even though the server may not have
sent down all the certs in a trusted chain, we can still take a look at certificate extensions to see if they point
us to an OCSP service which we can chat to and see if the cert is good.

Once built unzip/untar the distribution and invoke either the shell script or batch file depending on your OS.

Running the tool without any args should bring up the help...

```
error: Missing required option: host
usage:
Welcome to the SSL Checker...

Aim it at an host that uses HTTPS and it will give you some info
on the site blahdy blah blah

 -debug,--debug                 turn on ssl:record:plaintext
 -h,--help                      Show usage information
 -host,--host <arg>             The host name test against
 -ocsp,--ocsp                   Use OCSP to validate
 -proxy,--proxy <arg>           The HTTPS proxy
 -proxyport,--proxyport <arg>   The HTTPS proxy port
 -saveaia,--saveaia <arg>       Save the AIA certs to disk
 -savechain,--savechain <arg>   Save chain to disk

```

As an example, suppose we want to check out the site https://www.lightbend.com, just call the tool like this:
```
SSLChecker -host lightbend.com
```
and you should get something like this (at time of writing anyways):

```

Attempting to pull down certificate chain from https://www.lightbend.com


Looks like we have an issue with the SSLHandshake - perhaps we can't build path:javax.net.ssl.SSLException:
java.lang.RuntimeException: Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors
arameter must be non-empty

------------------------------------------------------------------------------------------------------------------------
Here's certificate info for chain[0]...

subject:       CN=www.lightbend.com, OU=PositiveSSL Multi-Domain, OU=Domain Control Validated
issuer:        CN=COMODO RSA Domain Validation Secure Server CA, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
Serial Number: f97c1f351acc80e4160046e974ae824a
Found 1 Authority Info Access Extension...
URIName: http://crt.comodoca.com/COMODORSADomainValidationSecureServerCA.crt
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	Here's some info on certificate pointed at in AIA extension...
	subject:       CN=COMODO RSA Domain Validation Secure Server CA, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
	issuer:        CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
	Serial Number: 2b2e6eead975366c148a6edba37c8c07
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


Here's certificate info for chain[1]...

subject:       CN=COMODO RSA Domain Validation Secure Server CA, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
issuer:        CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
Serial Number: 2b2e6eead975366c148a6edba37c8c07
Found 1 Authority Info Access Extension...
URIName: http://crt.comodoca.com/COMODORSAAddTrustCA.crt
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	Here's some info on certificate pointed at in AIA extension...
	subject:       CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
	issuer:        CN=AddTrust External CA Root, OU=AddTrust External TTP Network, O=AddTrust AB, C=SE
	Serial Number: 2766ee56eb49f38eabd770a2fc84de22
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


Here's certificate info for chain[2]...

subject:       CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
issuer:        CN=AddTrust External CA Root, OU=AddTrust External TTP Network, O=AddTrust AB, C=SE
Serial Number: 2766ee56eb49f38eabd770a2fc84de22
Found 0 Authority Info Access Extension...


------------------------------------------------------------------------------------------------------------------------

Next attempting to validate the chain separately...


Got a problem: Could not determine revocation status and the index of certificate that caused exception: 1


Failed! OK, so looks like the trust anchor didn't come down in chain from server. Lets see more info from cert chain - perhaps
there's an OCSP service inside Authority Info Access Extension...turn on the -ocsp flag to check against OCSP


Finished! Bye bye!
```

If we pass the '-ocsp' flag then output will look something like this:

'''
Attempting to pull down certificate chain from https://www.lightbend.com


Looks like we have an issue with the SSLHandshake - perhaps we can't build path:javax.net.ssl.SSLException: java.lang.RuntimeException: Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty

------------------------------------------------------------------------------------------------------------------------
Here's certificate info for chain[0]...

subject:       CN=www.lightbend.com, OU=PositiveSSL Multi-Domain, OU=Domain Control Validated
issuer:        CN=COMODO RSA Domain Validation Secure Server CA, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
Serial Number: f97c1f351acc80e4160046e974ae824a
Found 1 Authority Info Access Extension...
URIName: http://crt.comodoca.com/COMODORSADomainValidationSecureServerCA.crt
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	Here's some info on certificate pointed at in AIA extension...
	subject:       CN=COMODO RSA Domain Validation Secure Server CA, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
	issuer:        CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
	Serial Number: 2b2e6eead975366c148a6edba37c8c07
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


Here's certificate info for chain[1]...

subject:       CN=COMODO RSA Domain Validation Secure Server CA, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
issuer:        CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
Serial Number: 2b2e6eead975366c148a6edba37c8c07
Found 1 Authority Info Access Extension...
URIName: http://crt.comodoca.com/COMODORSAAddTrustCA.crt
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	Here's some info on certificate pointed at in AIA extension...
	subject:       CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
	issuer:        CN=AddTrust External CA Root, OU=AddTrust External TTP Network, O=AddTrust AB, C=SE
	Serial Number: 2766ee56eb49f38eabd770a2fc84de22
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


Here's certificate info for chain[2]...

subject:       CN=COMODO RSA Certification Authority, O=COMODO CA Limited, L=Salford, ST=Greater Manchester, C=GB
issuer:        CN=AddTrust External CA Root, OU=AddTrust External TTP Network, O=AddTrust AB, C=SE
Serial Number: 2766ee56eb49f38eabd770a2fc84de22
Found 0 Authority Info Access Extension...


------------------------------------------------------------------------------------------------------------------------

Next attempting to validate the chain separately...


Got a problem: Could not determine revocation status and the index of certificate that caused exception: 1


Failed! OK, so looks like the trust anchor didn't come down in chain from server. Lets see more info from cert chain - perhaps there's an OCSP service inside Authority Info Access Extension...turn on the -ocsp flag to check against OCSP


Ok, so looking for OCSP urls in chain extensions...


Found OCSP service url:http://ocsp.comodoca.com - trying each cert in chain against OCSP provider...


Trying certificate chain[0] with serial number: f97c1f351acc80e4160046e974ae824a


Contacting OCSP service using chain[0] and cert in AIA extension as parameters...


Service says this cert is GOOD!


Trying certificate chain[1] with serial number: 2b2e6eead975366c148a6edba37c8c07


Contacting OCSP service using chain[1] and cert in AIA extension as parameters...


Service says this cert is GOOD!


Trying certificate chain[2] with serial number: 2766ee56eb49f38eabd770a2fc84de22


No AIA extension for chain[2]


Finished! Bye bye!

'''

