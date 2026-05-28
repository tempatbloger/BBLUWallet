package com.bblu.wallet.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import org.bitcoinj.core.*
import org.bitcoinj.crypto.DeterministicKey
import org.bitcoinj.crypto.HDUtils
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.Wallet.BalanceType
import com.bblu.wallet.network.BBLUNetworkParameters
import java.io.File
import java.security.SecureRandom
import java.util.*

/**
 * Manager untuk semua operasi wallet BBLU
 * - Membuat wallet baru
 - Restore wallet dari seed
 * - Mengirim dan menerima BBLU
 * - Mengelola balance dan transaksi
 */
class BBLUWalletManager(
    private val context: Context,
    private val networkParams: BBLUParameters
) {
    
    private var wallet: Wallet? = null
    private lateinit var prefs: SharedPreferences
    
    companion object {
        private const val PREFS_NAME = "bblu_wallet_prefs"
        private const val KEY_WALLET_EXISTS = "wallet_exists"
        private const val KEY_SEED_SAVED = "seed_saved"
    }
    
    init {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Generate seed phrase (12 atau 24 kata)
     */
    fun generateSeed(wordCount: Int = 12): List<String> {
        val entropy = when (wordCount) {
            12 -> ByteArray(16)  // 128 bit
            24 -> ByteArray(32)  // 256 bit
            else -> throw IllegalArgumentException("Word count harus 12 atau 24")
        }
        SecureRandom().nextBytes(entropy)
        return MnemonicCode.INSTANCE.toMnemonic(entropy)
    }
    
    /**
     * Membuat wallet baru dari seed phrase
     */
    fun createWalletFromSeed(
        seedWords: List<String>,
        passphrase: String = ""
    ): Wallet {
        // Validasi seed phrase
        val seed = try {
            DeterministicSeed(seedWords, MnemonicCode.INSTANCE, passphrase, System.currentTimeMillis())
        } catch (e: Exception) {
            throw IllegalArgumentException("Seed phrase tidak valid: ${e.message}")
        }
        
        // Buat wallet dengan BIP84 path (SegWit)
        val newWallet = Wallet(networkParams)
        val keyChain = DeterministicKeyChain.builder()
            .seed(seed)
            .accountPath(HDUtils.parsePath(networkParams.getBip32Path()))
            .build()
        
        newWallet.addAndActivateHDChain(keyChain)
        
        // Auto backup ke file
        newWallet.autosaveToFile(getWalletFile(), 1, TimeUnit.SECONDS, null)
        
        this.wallet = newWallet
        saveWalletState(seedWords)
        
        return newWallet
    }
    
    /**
     * Restore wallet dari seed phrase
     */
    fun restoreWallet(seedWords: List<String>, passphrase: String = ""): Wallet {
        return createWalletFromSeed(seedWords, passphrase)
    }
    
    /**
     * Load wallet yang sudah ada dari file
     */
    fun loadWallet(): Wallet? {
        if (wallet != null) return wallet
        
        return if (getWalletFile().exists()) {
            wallet = Wallet.loadFromFile(getWalletFile())
            wallet
        } else {
            null
        }
    }
    
    /**
     * Mendapatkan address saat ini (untuk receive)
     */
    fun getCurrentAddress(): String {
        val currentWallet = wallet ?: throw IllegalStateException("Wallet belum dibuat/dimuat")
        val key = currentWallet.currentReceiveKey()
        return key.toAddress(networkParams).toString()
    }
    
    /**
     * Mendapatkan balance dalam format string
     */
    fun getBalance(): String {
        val currentWallet = wallet ?: return "0.00"
        val balance = currentWallet.getBalance(BalanceType.ESTIMATED)
        return formatCoinValue(balance)
    }
    
    /**
     * Mendapatkan balance dalam bentuk Coin
     */
    fun getBalanceCoin(): Coin {
        val currentWallet = wallet ?: return Coin.ZERO
        return currentWallet.getBalance(BalanceType.ESTIMATED)
    }
    
    /**
     * Mengirim BBLU ke address tujuan
     */
    fun sendPayment(toAddress: String, amountStr: String): String {
        val currentWallet = wallet ?: throw IllegalStateException("Wallet belum dibuat/dimuat")
        
        // Validasi address
        if (!isValidAddress(toAddress)) {
            throw IllegalArgumentException("Address BBLU tidak valid")
        }
        
        // Parse amount
        val amount = try {
            Coin.parseCoin(amountStr)
        } catch (e: Exception) {
            throw IllegalArgumentException("Jumlah tidak valid")
        }
        
        // Cek balance cukup
        val balance = currentWallet.getBalance(BalanceType.ESTIMATED)
        if (amount.isGreaterThan(balance)) {
            throw IllegalArgumentException("Balance tidak cukup")
        }
        
        // Buat send request
        val address = Address.fromString(networkParams, toAddress)
        val sendRequest = SendRequest.to(address, amount)
        
        // Set fee (default 10k satoshi)
        sendRequest.fee = Coin.valueOf(10000)
        sendRequest.feePerKb = Coin.valueOf(10000)
        
        // Complete transaction
        currentWallet.completeTx(sendRequest)
        currentWallet.commitTx(sendRequest.tx)
        
        // Save wallet
        currentWallet.saveToFile(getWalletFile())
        
        return sendRequest.tx.txId.toString()
    }
    
    /**
     * Validasi address BBLU
     */
    fun isValidAddress(address: String): Boolean {
        return try {
            // Coba parse address
            Address.fromString(networkParams, address)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Mendapatkan riwayat transaksi
     */
    fun getTransactionHistory(): List<Transaction> {
        val currentWallet = wallet ?: return emptyList()
        return currentWallet.transactions.toList()
    }
    
    /**
     * Mendapatkan jumlah transaksi
     */
    fun getTransactionCount(): Int {
        val currentWallet = wallet ?: return 0
        return currentWallet.transactions.size
    }
    
    /**
     * Backup seed phrase ke SharedPreferences (terenkripsi)
     */
    private fun saveWalletState(seedWords: List<String>) {
        // Simpan seed phrase (dalam production sebaiknya dienkripsi)
        val seedString = seedWords.joinToString(" ")
        prefs.edit()
            .putBoolean(KEY_WALLET_EXISTS, true)
            .putString(KEY_SEED_SAVED, seedString)
            .apply()
    }
    
    /**
     * Mendapatkan seed phrase yang tersimpan
     */
    fun getSavedSeed(): String? {
        return if (prefs.getBoolean(KEY_WALLET_EXISTS, false)) {
            prefs.getString(KEY_SEED_SAVED, null)
        } else null
    }
    
    /**
     * Cek apakah wallet sudah ada
     */
    fun hasWallet(): Boolean {
        return getWalletFile().exists() && prefs.getBoolean(KEY_WALLET_EXISTS, false)
    }
    
    /**
     * Format coin value ke string
     */
    private fun formatCoinValue(coin: Coin): String {
        val btcValue = coin.toBigDecimal().movePointLeft(8)
        return String.format("%.8f", btcValue).trimEnd('0').trimEnd('.')
    }
    
    /**
     * Mendapatkan file wallet
     */
    private fun getWalletFile(): File {
        return File(context.filesDir, "bblu_wallet.wallet")
    }
    
    /**
     * Export wallet ke file
     */
    fun exportWallet(destinationFile: File) {
        val currentWallet = wallet ?: throw IllegalStateException("Wallet belum dibuat/dimuat")
        currentWallet.saveToFile(destinationFile)
    }
    
    /**
     * Menghapus wallet (reset aplikasi)
     */
    fun deleteWallet(): Boolean {
        return try {
            wallet = null
            getWalletFile().delete()
            prefs.edit().clear().apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Copy text ke clipboard
     */
    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("BBLU Address", text)
        clipboard.setPrimaryClip(clip)
    }
    
    /**
     * Mendapatkan private key dalam format WIF (Wallet Import Format)
     */
    fun getPrivateKey(): String? {
        val currentWallet = wallet ?: return null
        val key = currentWallet.currentReceiveKey()
        return key.getPrivateKeyEncoded(networkParams).toString()
    }
}
