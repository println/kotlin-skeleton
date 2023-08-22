package skeleton.app.support.access.account

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository: JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    fun findByEmail(email: String): Optional<Account?>

    fun existsByEmail(email: String): Boolean
}