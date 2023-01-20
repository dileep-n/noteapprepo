package com.diledroid.noteapp.ui.register.view


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.diledroid.noteapp.MainActivity
import com.diledroid.noteapp.R
import com.diledroid.noteapp.databinding.FragmentLoginBinding
import com.diledroid.noteapp.ui.register.viewmodel.RegistrationViewModel
import com.diledroid.noteapp.utils.KeycodeUtils
import com.diledroid.noteapp.utils.ResultOf
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLoginBinding
    private lateinit var registerViewModel: RegistrationViewModel
    // create instance of firebase auth
    lateinit var auth: FirebaseAuth
    private lateinit var bundle: Bundle
    private lateinit var phone:String
    // we will use this to match the sent otp from firebase
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth=FirebaseAuth.getInstance()
        bundle = Bundle()
        registerViewModel = (activity as RegisterActivity).fetchRegisterViewModel()
        //   initViewModel()
        observerLoadingProgress()
        // observeSignIn()
        binding.registrationBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginBtn.setOnClickListener {
            if (LoginUtil.validateLoginInput(binding.loginEmail.text.toString(), binding.loginPwd.text.toString())) {
                signIn(binding.loginEmail.text.toString(), binding.loginPwd.text.toString())
            } else {
                Toast.makeText(requireContext(), "Login fields values are invalid", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.btnGo.setOnClickListener{
            KeycodeUtils.hideKeyboard(context as Activity)
            if (binding.etMobileNumber.text?.length != 10) {
                Toast.makeText(requireContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            sendVerificationCode("+91"+binding.etMobileNumber.text.toString())

        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                registerViewModel.loading.postValue(false)
                activity.let {
                    val intent = Intent(it, MainActivity::class.java)
                    startActivity(intent)
                    it?.finish()
                }
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                registerViewModel.loading.postValue(false)
                Log.d("GFG" , "onVerificationFailed  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                registerViewModel.loading.postValue(false)
                Log.d("GFG","onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token
                DetectingOtpDialogFragment
                    .newInstance(phone,storedVerificationId)
                    .show(requireActivity().supportFragmentManager, DetectingOtpDialogFragment.TAG)
                // Start a new activity using intent
                // also send the storedVerificationId using intent
                // we will use this id to send the otp back to firebase

            }
        }

    }


    private fun signIn(email: String, pwd: String) {
        observeSignIn()
        registerViewModel.signIn(email, pwd)

    }


    private fun observeSignIn() {
        registerViewModel.signInStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (it) {
                    is ResultOf.Success -> {
                        if (it.value.equals("Login Successful", ignoreCase = true)) {
                            // Toast.makeText(requireContext(),"Login Successful",Toast.LENGTH_LONG).show()
                            registerViewModel.resetSignInLiveData()
                            activity.let {
                                val intent = Intent(it, MainActivity::class.java)
                                startActivity(intent)
                                it?.finish()
                            }
                        }
                    }
                    is ResultOf.Failure -> {
                        val failedMessage = it.message ?: "Unknown Error"
                        Toast.makeText(
                            requireContext(), "Login failed with $failedMessage", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })


    }

    private fun observerLoadingProgress() {
        registerViewModel.fetchLoading().observe(viewLifecycleOwner, Observer {
            if (!it) {
                println(it)
                binding.loginProgress.visibility = View.GONE
            } else {
                binding.loginProgress.visibility = View.VISIBLE
            }

        })


    }

    // this method sends the verification code
    // and starts the callback of verification
    // which is implemented above in onCreate
    private fun sendVerificationCode(number: String) {
        phone = number
        registerViewModel.loading.postValue(true)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("GFG" , "Auth started")
    }


}