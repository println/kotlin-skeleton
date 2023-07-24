package skeleton.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories
@SpringBootApplication(exclude = [])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}