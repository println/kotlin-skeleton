package skeleton.app.api.support

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.Endpoints.ACCOUNT_ISSUE
import skeleton.app.support.access.issue.IssueRepository
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.issue.web.IssueController
import skeleton.app.support.access.issue.web.IssueWebService

class AccountIssueIT : AbstractWebIT<IssueToken>() {

    companion object {
        const val RESOURCE = ACCOUNT_ISSUE
    }

    @Autowired
    private lateinit var repository: IssueRepository

    @Autowired
    private lateinit var service: IssueWebService

    override fun getRepository() = repository
    override fun getEntityType() = IssueToken::class.java

    override fun getResource() = RESOURCE

    override fun createResource(): Any {
        return IssueController(service)
    }

    @AfterEach
    fun reset() {
        repository.deleteAll()
    }
}
