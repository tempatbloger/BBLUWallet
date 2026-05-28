package com.bblu.wallet.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bblu.wallet.R
import com.bblu.wallet.wallet.BBLUWalletManager

class ReceiveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)
        
        val walletManager = BBLUWalletManager(this)
        
        val tvAddress = findViewById<TextView>(R.id.tvAddress)
        val btnCopy = findViewById<Button>(R.id.btnCopyAddress)
        
        tvAddress.text = walletManager.getCurrentAddress()
        
        btnCopy.setOnClickListener {
            val address = tvAddress.text.toString()
            Toast.makeText(this, "Address copied: $address", Toast.LENGTH_SHORT).show()
        }
    }
}
