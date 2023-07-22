package skeleton.app.domain.auth

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuthRepository : JpaRepository<Auth, UUID>, JpaSpecificationExecutor<Auth> {
    fun findAllByCustomerId(customerId: UUID, specification: Specification<Auth>, pageable: Pageable): Page<Auth>
}