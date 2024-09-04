package co.ynd.BackendExercise.monitoring

import co.ynd.BackendExercise.TorExitNodeApplication
import co.ynd.BackendExercise.service.TorExitNodeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.boot.actuate.health.Status
import spock.lang.Specification

import static org.mockito.Mockito.when

@SpringBootTest(classes = TorExitNodeApplication, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TorExitNodeHealthIndicatorIT extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @MockBean
    TorExitNodeService torExitNodeService

    @Autowired
    TorExitNodeHealthIndicator torExitNodeHealthIndicator

    def "health endpoint should report down if Tor exit nodes list is empty"() {
        given:
        when(torExitNodeService.getTorExitNodes()).thenReturn([] as Set)

        when:
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/health", Map)

        then:
        response.statusCode == HttpStatus.SERVICE_UNAVAILABLE
        response.body.status == 'DOWN'
    }

    def "health endpoint should report up if Tor exit nodes list is not empty"() {
        given:
        when(torExitNodeService.getTorExitNodes()).thenReturn(["1.2.3.4"] as Set)

        when:
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/health", Map)

        then:
        response.statusCode == HttpStatus.OK
        response.body.status == 'UP'
    }

    def "should return DOWN health status from the health indicator directly"() {
        given:
        when(torExitNodeService.getTorExitNodes()).thenReturn([] as Set)

        when:
        def healthDown = torExitNodeHealthIndicator.health()

        then:
        healthDown.status == Status.DOWN
    }

    def "should return UP health status from the health indicator directly"() {
        given:
        when(torExitNodeService.getTorExitNodes()).thenReturn(["1.2.3.4"] as Set)

        when:
        def healthUp = torExitNodeHealthIndicator.health()

        then:
        healthUp.status == Status.UP
    }
}