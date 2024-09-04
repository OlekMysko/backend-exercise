package co.ynd.BackendExercise.e2e

import co.ynd.BackendExercise.TorExitNodeApplication
import co.ynd.BackendExercise.config.Messages
import co.ynd.BackendExercise.service.TorExitNodeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import spock.lang.Specification

@SpringBootTest(classes = TorExitNodeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TorExitNodeE2ESpec extends Specification {

    @LocalServerPort
    private int port

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    TorExitNodeService torExitNodeService

    private String baseUrl

    def setup() {
        baseUrl = "http://localhost:${port}/ip/"
    }

    def "E2E test for retrieving Tor exit node info"() {

        given:
        String ip =  torExitNodeService.getTorExitNodes().iterator().next()

        when:
        ResponseEntity<Void> headResponse = restTemplate.exchange(RequestEntity.head(new URI("${baseUrl}${ip}")).build(), Void.class)

        then:
        headResponse.statusCode == HttpStatus.OK

        when:
        ResponseEntity<Map> getResponse = restTemplate.getForEntity("${baseUrl}${ip}", Map.class)

        then:
        getResponse.statusCode == HttpStatus.OK
        getResponse.body.message == Messages.IP_ADDRESS_IS_A_TOR_EXIT_NODE
    }

    def "E2E test for invalid IP address"() {

        given:
        String ip = "256.256.256.256"

        when:
        ResponseEntity<Void> headResponse = restTemplate.exchange(RequestEntity.head(new URI("${baseUrl}${ip}")).build(), Void.class)

        then:
        headResponse.statusCode == HttpStatus.BAD_REQUEST

        when:
        ResponseEntity<Map> getResponse = restTemplate.getForEntity("${baseUrl}${ip}", Map.class)

        then:
        getResponse.statusCode == HttpStatus.BAD_REQUEST
        getResponse.body.message == Messages.IP_ADDRESS_IS_NOT_VALID
    }

    def "E2E test for non-Tor exit node IP"() {

        given:
        String ip = "9.9.9.9"

        when:
        ResponseEntity<Void> headResponse = restTemplate.exchange(RequestEntity.head(new URI("${baseUrl}${ip}")).build(), Void.class)

        then:
        headResponse.statusCode == HttpStatus.NOT_FOUND

        when:
        ResponseEntity<Map> getResponse = restTemplate.getForEntity("${baseUrl}${ip}", Map.class)

        then:
        getResponse.statusCode == HttpStatus.NOT_FOUND
        getResponse.body.message == Messages.IP_ADDRESS_IS_NOT_A_TOR_EXIT_NODE
    }
}