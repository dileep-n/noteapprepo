package com.diledroid.noteapp.ui.register.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.diledroid.noteapp.R
import com.diledroid.noteapp.databinding.ActivityRegisterBinding
import com.diledroid.noteapp.ui.register.viewmodel.RegistrationViewModel
import com.diledroid.noteapp.ui.register.viewmodel.RegistrationViewModelFactory


class RegisterActivity : AppCompatActivity() {

    lateinit var registrationViewModel: RegistrationViewModel
    //lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
       // binding = DataBindingUtil.setContentView(this,R.layout.activity_register)

        var fireBaseViewModelFactory = RegistrationViewModelFactory()
        registrationViewModel = ViewModelProvider(this,fireBaseViewModelFactory).get(RegistrationViewModel::class.java)

    }

    internal fun  fetchRegisterViewModel() = registrationViewModel
}