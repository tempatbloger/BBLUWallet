package com.bblu.wallet.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bblu.wallet.R
import com.bblu.wallet.wallet.BBLUWalletManager

class SendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        
        val walletManager = BBLUWalletManager(this)
        
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnSend = findViewById<Button>(R.id.btnSend)
        
        btnSend.setOnClickListener {
            val address = etAddress.text.toString()
            val amount = etAmount.text.toString()
            
            if (address.isNotEmpty() && amount.isNotEmpty()) {
                walletManager.sendPayment(address, amount)
                finish()
            } else {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
