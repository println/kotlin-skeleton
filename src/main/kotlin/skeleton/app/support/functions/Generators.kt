package skeleton.app.support.functions

import org.passay.*

object Generators {
    fun generatePassword(): String {
        val gen = PasswordGenerator()
        val lowerCaseChars: CharacterData = EnglishCharacterData.LowerCase
        val lowerCaseRule = CharacterRule(lowerCaseChars)
        lowerCaseRule.numberOfCharacters = 2
        val upperCaseChars: CharacterData = EnglishCharacterData.UpperCase
        val upperCaseRule = CharacterRule(upperCaseChars)
        upperCaseRule.numberOfCharacters = 2
        val digitChars: CharacterData = EnglishCharacterData.Digit
        val digitRule = CharacterRule(digitChars)
        digitRule.numberOfCharacters = 2

        val specialChars: CharacterData = object : CharacterData {
            override fun getErrorCode(): String = AllowedCharacterRule.ERROR_CODE
            override fun getCharacters(): String = "!@#$%^&*()_+"
        }

        val splCharRule = CharacterRule(specialChars)
        splCharRule.numberOfCharacters = 2
        return gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule)
    }

    fun generateSecurityCode(length: Int = 4) = (0..9).shuffled().take(length).joinToString("")
}