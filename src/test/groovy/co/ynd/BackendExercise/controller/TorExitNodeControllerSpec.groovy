package co.ynd.BackendExercise.controller

import co.ynd.BackendExercise.config.Messages
import co.ynd.BackendExercise.service.TorExitNodeService
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

class TorExitNodeControllerSpec extends Specification {

    @Subject
    TorExitNodeController torExitNodeController
    TorExitNodeService torExitNodeService = Mock()
    String IP_1 = "1.2.3.4"
    String IP_2 = "5.6.7.8"
    String INVALID_IP = "invalid"

    def setup() {
        torExitNodeController = new TorExitNodeController(torExitNodeService)
    }

    def "should return bad request for invalid IP in head check"() {
        given:
        torExitNodeService.ipAddressIsNotValid(INVALID_IP) >> true

        when:
        def response = torExitNodeController.checkTorExitNode(INVALID_IP)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should return not found for non-Tor exit node in head check"() {
        given:
        torExitNodeService.ipAddressIsNotValid(IP_2) >> false
        torExitNodeService.getTorExitNodes() >> new HashSet<>([IP_1])

        when:
        def response = torExitNodeController.checkTorExitNode(IP_2)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "should return OK for Tor exit node in head check"() {
        given:
        torExitNodeService.ipAddressIsNotValid(IP_1) >> false
        torExitNodeService.getTorExitNodes() >> new HashSet<>([IP_1])

        when:
        def response = torExitNodeController.checkTorExitNode(IP_1)

        then:
        response.statusCode == HttpStatus.OK
    }

    def "should return bad request for invalid IP in get info"() {
        given:
        torExitNodeService.ipAddressIsNotValid(INVALID_IP) >> true

        when:
        def response = torExitNodeController.getTorExitNodeInfo(INVALID_IP)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.message == Messages.IP_ADDRESS_IS_NOT_VALID
    }

    def "should return not found for non-Tor exit node in get info"() {
        given:
        torExitNodeService.ipAddressIsNotValid(IP_2) >> false
        torExitNodeService.getTorExitNodes() >> new HashSet<>([IP_1])

        when:
        def response = torExitNodeController.getTorExitNodeInfo(IP_2)

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body.message == Messages.IP_ADDRESS_IS_NOT_A_TOR_EXIT_NODE
    }

    def "should return OK for Tor exit node in get info"() {
        given:
        torExitNodeService.ipAddressIsNotValid(IP_1) >> false
        torExitNodeService.getTorExitNodes() >> new HashSet<>([IP_1])

        when:
        def response = torExitNodeController.getTorExitNodeInfo(IP_1)

        then:
        response.statusCode == HttpStatus.OK
        response.body.message == Messages.IP_ADDRESS_IS_A_TOR_EXIT_NODE
    }
}