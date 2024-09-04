package co.ynd.BackendExercise

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TorExitNodeApplication)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationContextTest extends Specification {

    @Autowired
    ApplicationContext applicationContext

    def "should load all beans in application context"() {
        expect: "all beans are loaded"
        applicationContext.beanDefinitionNames.each { beanName ->
            assert applicationContext.getBean(beanName) != null
        }
    }
}