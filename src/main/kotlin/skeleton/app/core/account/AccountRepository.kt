package skeleton.app.core.account

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository: JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
    fun findByEmail(email: String?): Optional<Account?>
}