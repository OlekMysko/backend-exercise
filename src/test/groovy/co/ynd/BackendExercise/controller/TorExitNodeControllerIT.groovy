package co.ynd.BackendExercise.controller

import co.ynd.BackendExercise.TorExitNodeApplication
import co.ynd.BackendExercise.config.Messages
import co.ynd.BackendExercise.service.TorExitNodeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import spock.lang.Specification

@SpringBootTest(classes = TorExitNodeApplication.class)
class TorExitNodeControllerIT extends Specification {

    private static final String INVALID_IP = "invalid"
    private static final String VALID_IP_NON_TOR = "5.6.7.8"
    private static final String INVALID_FORMAT_IP = "256.256.256.256"

    @Autowired
    private TorExitNodeController torExitNodeController

    @Autowired
    private TorExitNodeService torExitNodeService

    def "CheckTorExitNode should return bad request for invalid IP"() {
        when:
        def response = torExitNodeController.checkTorExitNode(INVALID_FORMAT_IP)
        then:
        response.statusCode == HttpStatus.BAD_REQUEST
    }

    def "CheckTorExitNode should return not found for non-Tor exit node"() {
        when:
        def response = torExitNodeController.checkTorExitNode(VALID_IP_NON_TOR)
        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

    def "CheckTorExitNode should return OK for Tor exit node"() {
        given:
        def firstExitNode = getFirstTorExitNodeIp()
        when:
        def response = torExitNodeController.checkTorExitNode(firstExitNode)
        then:
        response.statusCode == HttpStatus.OK
    }

    def "GetTorExitNodeInfo should return bad request for invalid IP"() {
        when:
        def response = torExitNodeController.getTorExitNodeInfo(INVALID_IP)
        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.message == Messages.IP_ADDRESS_IS_NOT_VALID
    }

    def "GetTorExitNodeInfo should return not found for non-Tor exit node"() {
        when:
        def response = torExitNodeController.getTorExitNodeInfo(VALID_IP_NON_TOR)
        then:
        response.statusCode == HttpStatus.NOT_FOUND
        response.body.message == Messages.IP_ADDRESS_IS_NOT_A_TOR_EXIT_NODE
    }

    def "GetTorExitNodeInfo should return OK for Tor exit node"() {
        given:
        def firstExitNode = getFirstTorExitNodeIp()
        when:
        def response = torExitNodeController.getTorExitNodeInfo(firstExitNode)
        then:
        response.statusCode == HttpStatus.OK
        response.body.message == Messages.IP_ADDRESS_IS_A_TOR_EXIT_NODE
    }

    String getFirstTorExitNodeIp() {
        return torExitNodeService.getTorExitNodes().iterator().next()
    }
}