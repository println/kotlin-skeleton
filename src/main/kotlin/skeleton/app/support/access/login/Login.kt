package skeleton.app.support.access.login

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames.Core.LOGIN

@Entity
@Table(name = LOGIN)
data class Login(
        @Id
        @Column(unique = true,  nullable = false)
        var username: String,
        var password: String,
)