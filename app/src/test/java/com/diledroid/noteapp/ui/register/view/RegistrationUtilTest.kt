package com.diledroid.noteapp.ui.register.view

import com.google.common.truth.Truth
import org.junit.Assert.*
import org.junit.Test

class RegistrationUtilTest{
    @Test
    fun emptyPasswordReturnFalse(){

        val result = RegistrationUtil.validateRegistrationInput("dileep@gmail.com","","admin123")
        Truth.assertThat(result).isFalse()
    }
    @Test
    fun emptyConfirmPasswordReturnFalse(){

        val result = RegistrationUtil.validateRegistrationInput("dileep@gmail.com","admin123","")
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun emptyEmailReturnFalse(){

        val result = RegistrationUtil.validateRegistrationInput("","admin123","admin123")
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun invalidEmailReturnFalse(){

        val result = RegistrationUtil.validateRegistrationInput("dilee","admin123","admin123")
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun invalidPasswordReturnFalse(){

        val result = RegistrationUtil.validateRegistrationInput("dilee@gmail.com","admin","admin")
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun validEmailAndPasswordReturnTrue(){
        val result = RegistrationUtil.validateRegistrationInput("dileepka@gmail.com","admin123","admin123")
        Truth.assertThat(result).isTrue()
    }
}