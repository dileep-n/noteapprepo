package com.diledroid.noteapp.ui.register.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.diledroid.noteapp.MainActivity
import com.diledroid.noteapp.R
import com.diledroid.noteapp.databinding.ActivityRegisterBinding
import com.diledroid.noteapp.ui.register.viewmodel.RegistrationViewModel
import com.diledroid.noteapp.ui.register.viewmodel.RegistrationViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {

    lateinit var registrationViewModel: RegistrationViewModel
    //lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener {
            Log.i("firebase", "AuthState changed to ${it.currentUser?.uid}")
            if (it.currentUser != null) {
                this.let {
                    val intent = Intent(it, MainActivity::class.java)
                    startActivity(intent)
                    it?.finish()
                }
            }
                // No user is signed in
                // binding = DataBindingUtil.setContentView(this,R.layout.activity_register)
        }
        var fireBaseViewModelFactory = RegistrationViewModelFactory()
        registrationViewModel = ViewModelProvider(this,fireBaseViewModelFactory).get(RegistrationViewModel::class.java)
        val appSignatureHelper = AppSignatureHelper(this)
        Log.v("RegisterActivityKey", appSignatureHelper.appSignatures[0])



    }

    internal fun  fetchRegisterViewModel() = registrationViewModel
}