package com.example.otpvarification

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit


class OTPverification : AppCompatActivity() {
    lateinit var next : Button
    lateinit var code : EditText
    lateinit var wrongCode : TextView
    lateinit var verificationId : String
    lateinit var message : TextView

    private lateinit var auth : FirebaseAuth

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        next = findViewById(R.id.verifyAndContinue)
        code = findViewById(R.id.verification_code)
        wrongCode = findViewById(R.id.invalidOTP)
        message = findViewById(R.id.messageOfCodeSend)

        auth = FirebaseAuth.getInstance()

        val phone = intent.getStringExtra("phone")
        val phoneNumber = "+91$phone"

        message.text = "Code sent to $phoneNumber"

        // phone number will always be there because of previous activity if/else condition
        sendVerificationCode(phoneNumber)


        next.setOnClickListener {
            if(code.text.isEmpty()){
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
            else{
                verifyCode(code.text.toString())
                val intent = Intent(this, selectProfile::class.java)
                startActivity(intent)
            }
        }
    }

    private fun sendVerificationCode(phone: String) {
        val option : PhoneAuthOptions =
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private val mCallBack: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
            // below method is used when OTP is sent from Firebase
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                // when we receive the OTP it contains a unique id which we are storing in our string which we have already created.
                verificationId = s
            }

            // this method is called when user receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // below line is used for getting OTP code which is sent in phone auth credentials.

                val codeSms = phoneAuthCredential.smsCode

                val codeN = code.text.toString()
//
//                // checking if the code is null or not.
                verifyCode(codeN)
            }

            // this method is called when firebase doesn't sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception
                Toast.makeText(this@OTPverification, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun verifyCode(codeVERIFY: String) {
        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, codeVERIFY)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    // if the code is correct and the task is successful we are sending our user to new activity.
                    val i = Intent(this@OTPverification, selectProfile::class.java)
                    startActivity(i)
                    finish()
                } else {
                    // if the code is not correct then we are displaying an error message to the user.
                    Toast.makeText(this@OTPverification, task.exception!!.message, Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

}


