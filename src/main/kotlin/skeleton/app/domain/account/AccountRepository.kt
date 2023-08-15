package skeleton.app.domain.account

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
}