package skeleton.app.core.token

import org.springframework.stereotype.Service
import skeleton.app.core.account.Account

@Service
class TokenService(
        private val repository: TokenRepository) {
    fun add(account: Account, jwtToken: String): Token {
        val token = Token(
                account = account,
                token = jwtToken,
                expired = false,
                revoked = false
        )
        return repository.save(token);
    }

    fun revokeAllUserTokens(account: Account) {
        val entities = repository.findAllValidTokenByUser(account.email)

        if (entities.isNotEmpty()) {
            entities.forEach {
                it.expired = true
                it.revoked = true
            }
            repository.saveAll(entities)
        }
    }
}