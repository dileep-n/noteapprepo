package com.diledroid.noteapp.ui.register.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.diledroid.noteapp.MainActivity


import com.diledroid.noteapp.R
import com.diledroid.noteapp.databinding.FragmentLoginBinding

import com.diledroid.noteapp.ui.register.viewmodel.RegistrationViewModel
import com.diledroid.noteapp.utils.ResultOf

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
    private lateinit var  registerViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerViewModel =  (activity as RegisterActivity).fetchRegisterViewModel()
        //   initViewModel()
        observerLoadingProgress()
        // observeSignIn()
        binding.registrationBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginBtn.setOnClickListener {
            if(TextUtils.isEmpty(binding.loginEmail.text.toString()) || TextUtils.isEmpty(binding.loginPwd.text.toString())){
                Toast.makeText(requireContext(),"Login fields can't be empty", Toast.LENGTH_LONG).show()
            }else{
                signIn(binding.loginEmail.text.toString(),binding.loginPwd.text.toString())
            }
        }
    }


    private fun signIn(email:String, pwd:String){
        registerViewModel.signIn(email,pwd)
        observeSignIn()
    }


    private fun observeSignIn(){
        registerViewModel.signInStatus.observe(viewLifecycleOwner, Observer {result->
            result?.let {
                when(it){
                    is ResultOf.Success ->{
                        if(it.value.equals("Login Successful",ignoreCase = true)){
                           // Toast.makeText(requireContext(),"Login Successful",Toast.LENGTH_LONG).show()
                            registerViewModel.resetSignInLiveData()
                            activity.let {
                                val intent = Intent(it, MainActivity::class.java)
                                startActivity(intent)
                                it?.finish()
                            }
                        }else if(it.value.equals("Reset",ignoreCase = true)){
                            Toast.makeText(requireContext(),"Login failed with ${it.value}",Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(requireContext(),"Login failed with ${it.value}",Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ResultOf.Failure -> {
                        val failedMessage =  it.message ?: "Unknown Error"
                        Toast.makeText(requireContext(),"Login failed with $failedMessage",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })


    }

    private fun observerLoadingProgress(){
        registerViewModel.fetchLoading().observe(viewLifecycleOwner, Observer {
            if (!it) {
                println(it)
                binding.loginProgress.visibility = View.GONE
            }else{
                binding.loginProgress.visibility = View.VISIBLE
            }

        })


    }


}