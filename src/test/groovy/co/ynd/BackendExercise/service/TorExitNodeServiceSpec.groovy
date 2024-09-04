package co.ynd.BackendExercise.service

import org.apache.commons.lang3.StringUtils
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject

class TorExitNodeServiceSpec extends Specification {

    @Subject
    TorExitNodeService torExitNodeService

    RestTemplate restTemplate = Mock()

    String IP_1 = "1.2.3.4"
    String IP_2 = "5.6.7.8"
    String TOR_EXIT_NODES_URL = "http://example.com"
    String EXIT_ADDRESS_PATTERN = "ExitAddress %s\nExitAddress %s"
    String INVALID_ADDRESS_PATTERN = "InvalidLine\nExitAddress %s"

    def setup() {
        torExitNodeService = new TorExitNodeService(restTemplate, TOR_EXIT_NODES_URL)
    }

    def "should return cached Tor exit nodes"() {
        given:
        torExitNodeService.torExitNodes = [IP_1] as Set

        when:
        def result = torExitNodeService.getTorExitNodes()

        then:
        result == [IP_1] as Set
    }

    def "should refresh Tor exit nodes"() {
        given:
        String response = String.format(EXIT_ADDRESS_PATTERN, IP_1, IP_2)
        restTemplate.getForObject(TOR_EXIT_NODES_URL, String) >> response

        when:
        torExitNodeService.refreshTorExitNodes()

        then:
        torExitNodeService.torExitNodes == [IP_1, IP_2] as Set
    }

    def "should handle empty response during refresh"() {
        given:
        restTemplate.getForObject(StringUtils.EMPTY, String) >> StringUtils.EMPTY

        when:
        torExitNodeService.refreshTorExitNodes()

        then:
        torExitNodeService.torExitNodes.isEmpty()
    }

    def "should handle malformed lines during parsing"() {
        given:
        String response = String.format(INVALID_ADDRESS_PATTERN, IP_1)
        restTemplate.getForObject(TOR_EXIT_NODES_URL, String) >> response

        when:
        torExitNodeService.refreshTorExitNodes()

        then:
        torExitNodeService.torExitNodes == [IP_1] as Set
    }

    def "should validate IP addresses correctly"() {
        expect:
        torExitNodeService.ipAddressIsNotValid(ip) == isInvalid

        where:
        ip          || isInvalid
        null        || true
        ""          || true
        "invalid"   || true
        "256.1.1.1" || true
        "1.1.1.1"   || false
    }
}