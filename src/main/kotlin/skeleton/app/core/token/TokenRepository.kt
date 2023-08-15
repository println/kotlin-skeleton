package skeleton.app.core.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TokenRepository : JpaRepository<Token, UUID> {
    @Query("""select t from Token t 
        inner join Account a on t.account.email = a.email 
        where a.email = :id and (t.expired = false or t.revoked = false)""")
    fun findAllValidTokenByUser(id: String): List<Token>

    fun findByToken(token: String): Optional<Token>
}
