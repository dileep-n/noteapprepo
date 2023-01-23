package com.diledroid.noteapp.ui.register.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.diledroid.noteapp.MainActivity
import com.diledroid.noteapp.R
import com.diledroid.noteapp.databinding.FragmentDetectingOtpDialogBinding
import com.diledroid.noteapp.utils.KeycodeUtils
import com.diledroid.noteapp.utils.isNumeric
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


/**
 * A simple [Fragment] subclass.
 * Use the [DetectingOtpDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetectingOtpDialogFragment : DialogFragment(), OtpListener {

    private val storedVerificationId: String
        get() = requireArguments().getString("storedVerificationId")!!
    private val phone: String
        get() = requireArguments().getString("Phone")!!
    private lateinit var binding:FragmentDetectingOtpDialogBinding
    lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detecting_otp_dialog,
            container,
            false
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root   }

    companion object {
        @JvmField
        val TAG = DetectingOtpDialogFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(phoneNo: String,storedId:String) = DetectingOtpDialogFragment().apply {
            arguments = Bundle().apply {
                putString("storedVerificationId", storedId)
                putString("Phone",phoneNo)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        initControl()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth=FirebaseAuth.getInstance()

    }
    fun init() {
        isCancelable = false
        binding.tvPhoneNo.text = phone
        val client = SmsRetriever.getClient(requireContext())
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.i(TAG, "SMS RETRIEVER TASK SUCCESSFULLY ADDED")
        }
        task.addOnFailureListener {
            Log.i(TAG, "SMS RETRIEVER TASK FAILED")
        }
        SMSBroadcastReceiver.otpListener = this
    }

    fun initControl() {
        binding.ivCross.setOnClickListener {
            dialog?.cancel()
        }

        binding.tvChange.setOnClickListener {
            dialog?.cancel()
        }

        binding.tvResendOtp.setOnClickListener {
            Toast.makeText(requireContext(), "OTP resend.", Toast.LENGTH_LONG).show()
        }

        binding.etOtp1.doOnTextChanged { text, start, count, after ->
            if (after == 1) {
                binding.etOtp2.requestFocus()
            }
        }

        binding.etOtp1.setOnKeyListener { v, keyCode, event ->
            event?.let {
                if (keyCode == KeyEvent.KEYCODE_DEL || keyCode.isNumeric()) {
                    updateInvalidState(false)
                }
                if (it.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode.isNumeric() && !binding.etOtp1.text.isNullOrEmpty()) {
                        binding.etOtp1.text?.clear()
                    }
                }
            }
            return@setOnKeyListener false
        }

        binding.etOtp2.doOnTextChanged { text, start, count, after ->
            if (after == 1) {
                binding.etOtp3.requestFocus()
            }
        }

        binding.etOtp2.setOnKeyListener { v, keyCode, event ->
            event?.let {

                if (it.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL || keyCode.isNumeric()) {
                        updateInvalidState(false)
                    }
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (binding.etOtp2.text.isNullOrEmpty()) {
                            binding.etOtp1.requestFocus()
                            return@setOnKeyListener true
                        }
                    } else {
                        if (keyCode.isNumeric() && !binding.etOtp2.text.isNullOrEmpty()) {
                            binding.etOtp2.text?.clear()
                        }
                    }
                }
            }
            return@setOnKeyListener false
        }

        binding.etOtp3.doOnTextChanged { text, start, count, after ->
            if (after == 1) {
                binding.etOtp4.requestFocus()
            }
        }

        binding.etOtp3.setOnKeyListener { v, keyCode, event ->
            event?.let {
                if (it.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL || keyCode.isNumeric()) {
                        updateInvalidState(false)
                    }
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (binding.etOtp3.text.isNullOrEmpty()) {
                            binding.etOtp2.requestFocus()
                            return@setOnKeyListener true
                        }
                    } else {
                        if (keyCode.isNumeric() && !binding.etOtp3.text.isNullOrEmpty()) {
                            binding.etOtp3.text?.clear()
                        }
                    }
                }
            }
            return@setOnKeyListener false
        }

        binding.etOtp4.doOnTextChanged { text, start, count, after ->
            if (binding.etOtp1.text.isNullOrEmpty()
                || binding.etOtp2.text.isNullOrEmpty()
                || binding.etOtp3.text.isNullOrEmpty()
                || binding.etOtp4.text.isNullOrEmpty()
            ) {
                return@doOnTextChanged
            }

            KeycodeUtils.hideKeyboard(context as Activity)
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                storedVerificationId, binding.etOtp1.text.toString()
                        + binding.etOtp2.text.toString()
                        + binding.etOtp3.text.toString()
                        + binding.etOtp4.text.toString())

            signInWithPhoneAuthCredential(credential)

//            if (binding.etOtp1.text.toString()
//                + binding.etOtp2.text.toString()
//                + binding.etOtp3.text.toString()
//                + binding.etOtp4.text.toString()
//                == "1234"
//            ) {
//                onVerifiedSuccessfully()
//            } else {
//                updateInvalidState(true)
//            }
        }

        binding.etOtp4.setOnKeyListener { v, keyCode, event ->
            event?.let {
                if (it.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL || keyCode.isNumeric()) {
                        updateInvalidState(false)
                    }
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (binding.etOtp4.text.isNullOrEmpty()) {
                            binding.etOtp3.requestFocus()
                            return@setOnKeyListener true
                        }
                    } else {
                        if (keyCode.isNumeric() && !binding.etOtp4.text.isNullOrEmpty()) {
                            binding.etOtp4.text?.clear()
                        }
                    }
                }
            }
            return@setOnKeyListener false
        }

    }

    private fun updateInvalidState(isInvalid: Boolean) {
        val background =
            if (isInvalid) R.drawable.bg_item_otp_view_error else R.drawable.bg_item_otp_view
        binding.etOtp1.background = ContextCompat.getDrawable(requireContext(), background)
        binding.etOtp2.background = ContextCompat.getDrawable(requireContext(), background)
        binding.etOtp3.background = ContextCompat.getDrawable(requireContext(), background)
        binding.etOtp4.background = ContextCompat.getDrawable(requireContext(), background)
        binding.tvInvalidOtp.visibility = if (isInvalid) View.VISIBLE else View.GONE
    }

    fun onVerifiedSuccessfully() {
        dialog?.cancel()
        Toast.makeText(requireContext(), "OTP Verified Successfully.", Toast.LENGTH_LONG).show()
    }

    override fun onOtpReceived(otp: String) {
        val otpArr = otp.toCharArray()
        binding.etOtp1.setText(otpArr[0].toString())
        binding.etOtp2.setText(otpArr[1].toString())
        binding.etOtp3.setText(otpArr[2].toString())
        binding.etOtp4.setText(otpArr[3].toString())
    }
    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    dialog?.cancel()
                    activity.let {
                        val intent = Intent(it, MainActivity::class.java)
                        startActivity(intent)
                        it?.finish()
                    }
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        //Toast.makeText(requireContext(),"Invalid OTP", Toast.LENGTH_SHORT).show()
                        updateInvalidState(true)
                    }
                }
            }
    }

}