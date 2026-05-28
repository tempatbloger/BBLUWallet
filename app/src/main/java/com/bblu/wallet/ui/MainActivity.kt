package com.bblu.wallet.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bblu.wallet.R
import com.bblu.wallet.wallet.BBLUWalletManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val walletManager = BBLUWalletManager(this)
        
        val tvBalance = findViewById<TextView>(R.id.tvBalance)
        val tvAddress = findViewById<TextView>(R.id.tvAddress)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val btnReceive = findViewById<Button>(R.id.btnReceive)
        
        tvBalance.text = "${walletManager.getBalance()} BBLU"
        tvAddress.text = walletManager.getCurrentAddress()
        
        btnSend.setOnClickListener {
            startActivity(Intent(this, SendActivity::class.java))
        }
        
        btnReceive.setOnClickListener {
            startActivity(Intent(this, ReceiveActivity::class.java))
        }
    }
}
