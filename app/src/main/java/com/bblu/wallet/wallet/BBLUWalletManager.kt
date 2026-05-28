package com.bblu.wallet.wallet

import android.content.Context
import android.widget.Toast

class BBLUWalletManager(private val context: Context) {
    
    fun getBalance(): String = "0.00"
    
    fun getCurrentAddress(): String = "bb1qexampleaddressforshow"
    
    fun sendPayment(toAddress: String, amount: String): Boolean {
        Toast.makeText(context, "Send $amount to $toAddress", Toast.LENGTH_LONG).show()
        return true
    }
    
    fun getTransactionCount(): Int = 0
    
    fun copyToClipboard(text: String) {
        Toast.makeText(context, "Address copied", Toast.LENGTH_SHORT).show()
    }
}
