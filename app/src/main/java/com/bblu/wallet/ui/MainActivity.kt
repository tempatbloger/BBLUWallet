package com.bblu.wallet.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bblu.wallet.R
import com.bblu.wallet.databinding.ActivityMainBinding
import com.bblu.wallet.network.BBLUParameters
import com.bblu.wallet.wallet.BBLUWalletManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var walletManager: BBLUWalletManager
    private lateinit var networkParams: BBLUParameters
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        networkParams = BBLUParameters.get()
        walletManager = BBLUWalletManager(this, networkParams)
        
        setupUI()
        loadWalletData()
    }
    
    private fun setupUI() {
        binding.apply {
            // Tombol Send
            btnSend.setOnClickListener {
                startActivity(Intent(this@MainActivity, SendActivity::class.java))
            }
            
            // Tombol Receive
            btnReceive.setOnClickListener {
                startActivity(Intent(this@MainActivity, ReceiveActivity::class.java))
            }
            
            // Tombol Refresh
            btnRefresh.setOnClickListener {
                loadWalletData()
            }
            
            // Tombol Copy Address
            btnCopyAddress.setOnClickListener {
                copyAddressToClipboard()
            }
        }
    }
    
    private fun loadWalletData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Load atau create wallet
                var wallet = walletManager.loadWallet()
                if (wallet == null) {
                    // Jika belum ada wallet, buat baru
                    val seed = walletManager.generateSeed()
                    wallet = walletManager.createWalletFromSeed(seed)
                    showToast("Wallet baru berhasil dibuat! Backup seed phrase Anda.")
                }
                
                // Update UI dengan data wallet
                val balance = walletManager.getBalance()
                val address = walletManager.getCurrentAddress()
                val transactionCount = walletManager.getTransactionCount()
                
                withContext(Dispatchers.Main) {
                    updateUI(balance, address, transactionCount)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error loading wallet: ${e.message}")
                }
            }
        }
    }
    
    private fun updateUI(balance: String, address: String, txCount: Int) {
        binding.apply {
            tvBalance.text = "$balance BBLU"
            tvAddress.text = address
            tvTransactionCount.text = "$txCount transaksi"
            
            // Format address untuk display (shorten)
            val shortAddress = if (address.length > 20) {
                "${address.substring(0, 10)}...${address.substring(address.length - 8)}"
            } else {
                address
            }
            tvAddressShort.text = shortAddress
        }
    }
    
    private fun copyAddressToClipboard() {
        val address = binding.tvAddress.text.toString()
        walletManager.copyToClipboard(address)
        showToast("Address berhasil disalin!")
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onResume() {
        super.onResume()
        loadWalletData() // Refresh data saat kembali ke activity
    }
}
