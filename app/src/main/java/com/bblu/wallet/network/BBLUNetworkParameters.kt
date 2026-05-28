package com.bblu.wallet.network

import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.MainNetParams

class BBLUNetworkParameters : MainNetParams() {
    companion object {
        fun get(): BBLUNetworkParameters = BBLUNetworkParameters()
    }
}
