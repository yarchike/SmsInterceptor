package com.martynov.smsinterceptor

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import java.lang.Exception
import android.content.IntentFilter

import android.R.string.no
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    val PERMISSION_REQUEST_CODE = 55

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            addSmsInsp()
        } else {
            requestMultiplePermissions()
        }

    }

    private fun addSmsInsp() {
        Log.d("MyLogS","addSmsInsp")
        val smsView = findViewById<TextView>(R.id.sms)
        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        val incomingSms = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("MyLogS","onReceive")
                val bundle = intent!!.extras
                try {
                    if (bundle != null) {
                        val pdusObj = bundle["pdus"] as Array<Any>?
                        for (i in pdusObj!!.indices) {
                            val currentMessage: SmsMessage =
                                SmsMessage.createFromPdu(pdusObj!![i] as ByteArray)
                            val phoneNumber: String = currentMessage.getDisplayOriginatingAddress()
                            val message: String = currentMessage.getDisplayMessageBody()
                            Log.i("SmsReceiver", "senderNum: $phoneNumber; message: $message")


                            // Show Alert
                            val duration = Toast.LENGTH_LONG
                            val toast = Toast.makeText(
                                context,
                                "senderNum: $phoneNumber, message: $message", duration
                            )
                            smsView.text = message
                            toast.show()
                        } // end for loop
                    } // bundle is null
                } catch (e: Exception) {
                    Log.e("SmsReceiver", "Exception smsReceiver$e")
                }

            }

        }
        registerReceiver(incomingSms, filter);
    }

    fun requestMultiplePermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.RECEIVE_SMS
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addSmsInsp()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}