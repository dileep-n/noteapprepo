package com.diledroid.noteapp.ui.register.view


import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginUtilTest {

    @Test
    fun emptyPasswordReturnFalse(){

        val result = LoginUtil.validateLoginInput("dileep@gmail.com","")
        assertThat(result).isFalse()
    }

    @Test
    fun emptyEmailReturnFalse(){

        val result = LoginUtil.validateLoginInput("","admin123")
        assertThat(result).isFalse()
    }

    @Test
    fun invalidEmailReturnFalse(){

        val result = LoginUtil.validateLoginInput("dilee","admin123")
        assertThat(result).isFalse()
    }

    @Test
    fun invalidPasswordReturnFalse(){

        val result = LoginUtil.validateLoginInput("dilee@gmail.com","admin")
        assertThat(result).isFalse()
    }

    @Test
    fun validEmailAndPasswordReturnTrue(){
        val result = LoginUtil.validateLoginInput("dileepka@gmail.com","admin123")
        assertThat(result).isTrue()
    }
}