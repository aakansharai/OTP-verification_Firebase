package com.example.otpvarification

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class PhoneNumber : AppCompatActivity() {

    lateinit var continueBTN : Button
    lateinit var phone : EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        continueBTN = findViewById(R.id.mobileNumberNextBtn)
        phone = findViewById(R.id.phoneNumber)

        continueBTN.setOnClickListener {
            if(phone.toString().length>=6){
                val phoneNum = phone.text.toString()
                Log.d("Aakansha", phoneNum)
                Toast.makeText(this, phoneNum, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, OTPverification::class.java)
                intent.putExtra("phone", phoneNum)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Phone number not valid!", Toast.LENGTH_SHORT).show()
            }

        }

    }
}