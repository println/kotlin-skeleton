package skeleton.app.domain.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<Userr, UUID>, JpaSpecificationExecutor<Userr> {
    fun findAllByCustomerId(customerId: UUID, specification: Specification<Userr>, pageable: Pageable): Page<Userr>
}