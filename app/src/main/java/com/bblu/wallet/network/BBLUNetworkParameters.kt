package com.bblu.wallet.network

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.MainNetParams

class BBLUNetworkParameters : MainNetParams() {
    
    override fun getBech32Hrp(): String = "bb"
    
    override fun getBip32Path(): String = "m/84'/4353123'/0'/0"
    
    init {
        // Override parameter untuk BBLU
        addressHeader = 25
        p2shHeader = 26
    }
    
    companion object {
        private var instance: BBLUNetworkParameters? = null
        
        fun get(): BBLUNetworkParameters {
            if (instance == null) {
                instance = BBLUNetworkParameters()
            }
            return instance!!
        }
    }
}
