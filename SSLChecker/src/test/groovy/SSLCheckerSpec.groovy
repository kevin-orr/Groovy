import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by kevin orr on 03/02/2017.
 */
class SSLCheckerSpec extends Specification {

    def "should fail when no parameters"() {
        when:
        SSLChecker.main()

        then:
        thrown(AssertionError)
    }

    @Unroll
    def "should fail when required parameter #missingParam value is missing"() {
        when:
        SSLChecker.main(params as String[])

        then:
        thrown(AssertionError)

        where:
        missingParam    | params
        'endpoint'      | ['-endpoint', '']
        'endpoint'      | ['-endpoint', ' ']
    }

    def "should get a cert chain or null when calling getCertChain"() {
        setup:
        def checker = new SSLChecker()

        when:
        checker.getCertChain()

        then:
        noExceptionThrown()
    }
}
