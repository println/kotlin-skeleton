package skeleton.app.api.support

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.Endpoints.SESSION
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.session.Session
import skeleton.app.support.access.session.SessionRepository
import skeleton.app.support.access.session.web.SessionController
import skeleton.app.support.access.session.web.SessionWebService

class AccountSessionIT : AbstractWebIT<Session>() {
    companion object {
        const val RESOURCE = SESSION
        var ACCOUNT: Account? = null
    }

    @Autowired
    private lateinit var repository: SessionRepository

    @Autowired
    private lateinit var service: SessionWebService

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun getRepository() = repository
    override fun getEntityType() = Session::class.java

    override fun getResource() = RESOURCE

    override fun createResource(): Any {
        return SessionController(service)
    }

    override fun preProcessing(data: Session): Session {
        if (accountRepository.count() <= 0 || ACCOUNT == null) {
            ACCOUNT = AccountIT.createAccount(repository = accountRepository, encoder = passwordEncoder)
        }
        val account = ACCOUNT!!
        return Session(
                token = data.token,
                tokenType = data.tokenType,
                revoked = data.revoked,
                expired = data.expired,
                account = account)
    }

    @AfterEach
    fun clear(){
        accountRepository.deleteAll()
        repository.deleteAll()
    }
}