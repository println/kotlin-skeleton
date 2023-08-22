package skeleton.app

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test", "integration-test")
@SpringBootTest(properties = ["spring.main.allow-bean-definition-overriding=true"])
open class IntegrationTest {
}