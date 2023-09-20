package skeleton.app.support.access.issue

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import skeleton.app.support.access.issue.IssueStatus.*
import java.util.*


@Repository
interface IssueRepository : JpaRepository<IssueToken, UUID>, JpaSpecificationExecutor<IssueToken> {
    fun findByIdAndStatus(tokenId: UUID,
                          status: IssueStatus = OPEN): Optional<IssueToken>

    fun findFirstByAccountIdAndStatus(accountId: UUID, status: IssueStatus = OPEN): Optional<IssueToken>

    fun existsByAccountId(accountId: UUID): Boolean

    fun findFirstBySecurityCodeAndType(securityCode: String, type: IssueType): Optional<IssueToken>

    @Modifying
    @Query("update IssueToken i set i.status = CLOSED where i.accountId = :accountId and i.type = :type")
    fun closeOlderIssues(accountId: UUID, type: IssueType): Int
}