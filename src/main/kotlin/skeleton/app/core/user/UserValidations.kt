package skeleton.app.core.user


object UserValidations {

    fun canUpdate(entity: User?) =
            (entity != null)
                    .and(entity!!.firstName.isNotBlank())
                    .and(entity.lastName.isNotBlank())

}