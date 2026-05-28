package com.bblu.wallet.electrum

import com.squareup.okhttp3.OkHttpClient
import com.squareup.okhttp3.Request
import com.squareup.okhttp3.RequestBody
import com.squareup.okhttp3.MediaType
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ElectrumClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val serverUrl = "http://electrumx.bitcoin-blu.org:50001"
    private val mediaType = MediaType.parse("application/json; charset=utf-8")
    
    fun getBlockchainHeight(): Int? {
        try {
            val json = JSONObject()
            json.put("id", 1)
            json.put("method", "blockchain.headers.subscribe")
            json.put("params", emptyArray<Any>())
            
            val response = postRequest(json.toString())
            return response?.getInt("height")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun postRequest(jsonBody: String): JSONObject? {
        val requestBody = RequestBody.create(mediaType, jsonBody)
        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .build()
            
        val response = client.newCall(request).execute()
        val responseBody = response.body()?.string()
        
        return if (response.isSuccessful && responseBody != null) {
            JSONObject(responseBody).getJSONObject("result")
        } else null
    }
}
