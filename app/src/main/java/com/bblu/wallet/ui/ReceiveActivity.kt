package com.bblu.wallet.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bblu.wallet.databinding.ActivityReceiveBinding
import com.bblu.wallet.network.BBLUParameters
import com.bblu.wallet.wallet.BBLUWalletManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceiveActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityReceiveBinding
    private lateinit var walletManager: BBLUWalletManager
    private lateinit var networkParams: BBLUParameters
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        networkParams = BBLUParameters.get()
        walletManager = BBLUWalletManager(this, networkParams)
        
        setupUI()
        loadAddressAndQR()
    }
    
    private fun setupUI() {
        binding.apply {
            // Tombol Copy Address
            btnCopyAddress.setOnClickListener {
                copyAddressToClipboard()
            }
            
            // Tombol Share Address
            btnShareAddress.setOnClickListener {
                shareAddress()
            }
            
            // Tombol Back
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
    
    private fun loadAddressAndQR() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val address = walletManager.getCurrentAddress()
                val qrBitmap = generateQRCode(address)
                
                withContext(Dispatchers.Main) {
                    binding.tvAddress.text = address
                    binding.ivQrCode.setImageBitmap(qrBitmap)
                    
                    // Format address untuk display
                    val shortAddress = if (address.length > 30) {
                        "${address.substring(0, 15)}...${address.substring(address.length - 12)}"
                    } else {
                        address
                    }
                    binding.tvAddressShort.text = shortAddress
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ReceiveActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun generateQRCode(address: String): Bitmap? {
        return try {
            val multiFormatWriter = MultiFormatWriter()
            val matrix = multiFormatWriter.encode(address, BarcodeFormat.QR_CODE, 400, 400)
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.createBitmap(matrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun copyAddressToClipboard() {
        val address = binding.tvAddress.text.toString()
        walletManager.copyToClipboard(address)
        Toast.makeText(this, "Address BBLU berhasil disalin!", Toast.LENGTH_SHORT).show()
    }
    
    private fun shareAddress() {
        val address = binding.tvAddress.text.toString()
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, """
                Address BBLU Saya:
                $address
                
                Kirim BBLU ke address di atas.
            """.trimIndent())
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(shareIntent, "Bagikan Address BBLU"))
    }
}
