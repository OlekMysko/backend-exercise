package co.ynd.BackendExercise.service

import co.ynd.BackendExercise.TorExitNodeApplication
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject

@SpringBootTest(classes = TorExitNodeApplication.class)
class TorExitNodeServiceIT extends Specification {
    @Autowired
    @Subject
    TorExitNodeService torExitNodeService
    @Autowired
    RestTemplate restTemplate

    String TOR_EXIT_NODES_URL = "http://example.com"
    String EXIT_ADDRESS_PATTERN = "ExitAddress %s\nExitAddress %s"
    String NETWORK_ERROR_MESSAGE = "Network error"

    def "should refresh Tor exit nodes and cache them"() {
        given:
        def (firstExitNode, secondExitNode) = getFirstTwoExitNodesWithIterator()
        String mockResponse = createMockResponse(firstExitNode, secondExitNode)
        setupRestTemplateMock(TOR_EXIT_NODES_URL, mockResponse)
        when:
        torExitNodeService.refreshTorExitNodes()
        def refreshedTorExitNodes = torExitNodeService.getTorExitNodes()
        then:
        refreshedTorExitNodes.contains(firstExitNode)
        refreshedTorExitNodes.contains(secondExitNode)
    }

    def "should fallback to last known set of Tor exit nodes on network error"() {
        given:
        def lastKnownTorExitNodes = torExitNodeService.getTorExitNodes()
        setTorExitNodes(lastKnownTorExitNodes as Set)
        setupRestTemplateMockWithException()
        when:
        torExitNodeService.refreshTorExitNodes()
        def refreshedTorExitNodes = torExitNodeService.getTorExitNodes()
        then:
        refreshedTorExitNodes == lastKnownTorExitNodes as Set
    }

    def setupRestTemplateMock(String url, def response) {
        restTemplate.getForObject(url, String) >> response
    }

    def setTorExitNodes(Set<String> nodes) {
        def field = TorExitNodeService.class.getDeclaredField("torExitNodes")
        field.setAccessible(true)
        field.set(torExitNodeService, nodes)
    }

    String createMockResponse(String firstExitNode, String secondExitNode) {
        return String.format(EXIT_ADDRESS_PATTERN, firstExitNode, secondExitNode)
    }

    def setupRestTemplateMockWithException() {
        restTemplate.getForObject(StringUtils.EMPTY, String) >> { throw new Exception(NETWORK_ERROR_MESSAGE) }
    }

    List<String> getFirstTwoExitNodesWithIterator() {
        def currentTorExitNodes = torExitNodeService.getTorExitNodes()
        Iterator<String> iterator = currentTorExitNodes.iterator()

        def firstExitNode = iterator.hasNext() ? iterator.next() : null
        def secondExitNode = iterator.hasNext() ? iterator.next() : null
        return [firstExitNode, secondExitNode]
    }
}