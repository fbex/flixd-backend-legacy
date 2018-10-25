package classification

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension::class)
annotation class IntegrationTest
