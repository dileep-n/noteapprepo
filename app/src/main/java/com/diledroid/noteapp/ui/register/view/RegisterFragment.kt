package com.diledroid.noteapp.ui.register.view

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.diledroid.noteapp.R
import com.diledroid.noteapp.databinding.FragmentRegisterBinding
import com.diledroid.noteapp.ui.register.viewmodel.RegistrationViewModel
import com.diledroid.noteapp.utils.ResultOf

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterFragment : Fragment() {

    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var binding:FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationViewModel =  (activity as RegisterActivity).fetchRegisterViewModel()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerBtn.setOnClickListener {
            if(TextUtils.isEmpty(binding.editTextTextEmailAddress.text.toString()) ||
                TextUtils.isEmpty(binding.editTextTextPassword.text.toString())
                || TextUtils.isEmpty(binding.editTextTextPassword2.text.toString())){
                Toast.makeText(requireContext(),"Input Fields cannot be Empty", Toast.LENGTH_LONG).show()
            }else if(binding.editTextTextPassword.text.toString() != binding.editTextTextPassword2.text.toString()){
                Toast.makeText(requireContext(),"Passwords don't match", Toast.LENGTH_LONG).show()
            }else{
                doRegistration()
            }


        }

        observeRegistration()

    }

    private fun doRegistration(){
        registrationViewModel.signUp(binding.editTextTextEmailAddress.text.toString(),
            binding.editTextTextPassword.text.toString())
    }

    private fun observeRegistration(){
        registrationViewModel.registrationStatus.observe(viewLifecycleOwner, Observer {result->
            result?.let {
                when(it){
                    is ResultOf.Success ->{
                        if(it.value.equals("UserCreated",ignoreCase = true)){
                            Toast.makeText(requireContext(),"Registration Successful User created",Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }else{
                            Toast.makeText(requireContext(),"Registration failed with ${it.value}",Toast.LENGTH_LONG).show()
                        }
                    }

                    is ResultOf.Failure -> {
                        val failedMessage =  it.message ?: "Unknown Error"
                        Toast.makeText(requireContext(),"Registration failed with $failedMessage",Toast.LENGTH_LONG).show()
                    }
                }
            }

        })
    }


}