package skeleton.app.support.access.auth.basic.login

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames

@Entity
@Table(name = TableNames.Core.LOGIN)
data class Login(
        @Id
        @Column(unique = true,  nullable = false)
        var username: String,
        var password: String,
)