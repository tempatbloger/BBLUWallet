package com.bblu.wallet.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bblu.wallet.databinding.ActivitySendBinding
import com.bblu.wallet.network.BBLUNetworkParameters
import com.bblu.wallet.wallet.BBLUWalletManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySendBinding
    private lateinit var walletManager: BBLUWalletManager
    private lateinit var networkParams: BBLUNetworkParameters
    
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            binding.etAddress.setText(result.contents)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        networkParams = BBLUNetworkParameters.get()
        walletManager = BBLUWalletManager(this, networkParams)
        
        setupUI()
        loadBalance()
    }
    
    private fun setupUI() {
        binding.apply {
            // Tombol Scan QR
            btnScanQr.setOnClickListener {
                scanQRCode()
            }
            
            // Tombol Send
            btnSend.setOnClickListener {
                sendTransaction()
            }
            
            // Tombol Back
            btnBack.setOnClickListener {
                finish()
            }
            
            // Tombol Max
            btnMax.setOnClickListener {
                etAmount.setText(currentBalance)
            }
        }
    }
    
    private var currentBalance = "0"
    
    private fun loadBalance() {
        lifecycleScope.launch(Dispatchers.IO) {
            val balance = walletManager.getBalance()
            currentBalance = balance
            withContext(Dispatchers.Main) {
                binding.tvCurrentBalance.text = "Balance: $balance BBLU"
            }
        }
    }
    
    private fun scanQRCode() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR Code address BBLU")
        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        scanLauncher.launch(options)
    }
    
    private fun sendTransaction() {
        val toAddress = binding.etAddress.text.toString().trim()
        val amount = binding.etAmount.text.toString().trim()
        
        // Validasi input
        if (toAddress.isEmpty()) {
            binding.etAddress.error = "Address tujuan tidak boleh kosong"
            return
        }
        
        if (!walletManager.isValidAddress(toAddress)) {
            binding.etAddress.error = "Address BBLU tidak valid"
            return
        }
        
        if (amount.isEmpty()) {
            binding.etAmount.error = "Jumlah tidak boleh kosong"
            return
        }
        
        val amountValue = amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            binding.etAmount.error = "Jumlah harus lebih dari 0"
            return
        }
        
        // Konfirmasi transaksi
        showConfirmDialog(toAddress, amountValue)
    }
    
    private fun showConfirmDialog(toAddress: String, amount: Double) {
        val message = """
            Konfirmasi pengiriman:
            
            Tujuan: ${toAddress.take(20)}...
            Jumlah: $amount BBLU
            
            Pastikan address tujuan sudah benar!
        """.trimIndent()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Konfirmasi Transaksi")
            .setMessage(message)
            .setPositiveButton("Kirim") { _, _ ->
                executeTransaction(toAddress, amount)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun executeTransaction(toAddress: String, amount: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                showProgress(true)
                
                val txId = walletManager.sendPayment(toAddress, amount.toString())
                
                withContext(Dispatchers.Main) {
                    showProgress(false)
                    Toast.makeText(
                        this@SendActivity,
                        "Transaksi berhasil dikirim!\nID: ${txId.take(20)}...",
                        Toast.LENGTH_LONG
                    ).show()
                    finish() // Kembali ke MainActivity
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showProgress(false)
                    Toast.makeText(
                        this@SendActivity,
                        "Gagal mengirim: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnSend.isEnabled = !show
        binding.btnScanQr.isEnabled = !show
    }
}
