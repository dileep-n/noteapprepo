package com.diledroid.noteapp.ui.register.view

import java.util.regex.Pattern

object RegistrationUtil {

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun validateRegistrationInput(email: String, password: String, confirmPswd:String): Boolean {

        if(email.isEmpty() ||password.isEmpty()||confirmPswd.isEmpty()){
            return false
        }

        if (password.length<6){
            return false
        }
        if(password!=confirmPswd){
            return false
        }

        if(!isValidMail(email)){
            return false
        }
        return true
    }

    fun isValidMail(str: String): Boolean{
        return LoginUtil.EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }
}