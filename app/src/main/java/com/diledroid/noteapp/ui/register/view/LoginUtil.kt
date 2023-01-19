package com.diledroid.noteapp.ui.register.view

import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern


object LoginUtil {
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun validateLoginInput(email:String,password:String):Boolean{

        if(email.isEmpty() ||password.isEmpty()){
            return false
        }

        if (password.length<6){
            return false
        }
        if(!isValidMail(email)){
            return false
        }

        return true

    }
//    private fun emailValidator(emailToText: String):Boolean {
//
//        // Android offers the inbuilt patterns which the entered
//        // data from the EditText field needs to be compared with
//        // In this case the entered data needs to compared with
//        // the EMAIL_ADDRESS, which is implemented same below
//        return emailToText?.isEmpty() == true && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()
//    }

    fun isValidMail(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }
}