package skeleton.app.support.access.issue

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import skeleton.app.support.access.issue.IssueStatus.*
import java.util.*

@Repository
interface IssueRepository : JpaRepository<IssueToken, UUID>, JpaSpecificationExecutor<IssueToken> {
    fun findByIdAndSecurityCodeAndStatus(tokenId: UUID,
                                         securityCode: String,
                                         status: IssueStatus = OPEN): Optional<IssueToken>

    fun findFirstByAccountIdAndStatus(accountId: UUID, status: IssueStatus = OPEN): Optional<IssueToken>

    fun existsByAccountId(accountId: UUID): Boolean
}