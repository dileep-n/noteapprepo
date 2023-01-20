package com.diledroid.noteapp.ui.register.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.diledroid.noteapp.utils.KeycodeUtils.getOtp
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String
                    Log.e(TAG, "message: $message")
                    val otp = getOtp(message)
                    Log.e(TAG, "OTP: $otp")
                    otpListener?.onOtpReceived(otp)
                }
                CommonStatusCodes.TIMEOUT -> {
                }
            }
        }
    }

    companion object {
        private val TAG = SMSBroadcastReceiver::class.java.simpleName

        var otpListener: OtpListener? = null
    }
}

interface OtpListener {
    fun onOtpReceived(otp: String)
}