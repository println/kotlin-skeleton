package skeleton.app.support.jpa

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.util.*
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass

abstract class AuditableModel<T : AuditableModel<T>> : Auditable<T>() {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = [
                Parameter(
                        name = "uuid_gen_strategy_class",
                        value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
                )
            ]
    )
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null

}
