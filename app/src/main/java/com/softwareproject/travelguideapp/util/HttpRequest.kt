package com.softwareproject.travelguideapp.util

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.softwareproject.travelguideapp.`interface`.RequestCallBackListener
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import com.softwareproject.travelguideapp.*
import java.net.URLEncoder
import java.nio.charset.Charset


class HttpRequest(path: String, method: String, body: JSONObject? = null, listenerRequest: RequestCallBackListener) :
    AsyncTask<String, Void, JSONObject?>() {
    var listenerRequest: RequestCallBackListener
    var path: String
    val method: String
    var body: JSONObject? = null
    // result is the api return if request is successful
    // error is return if bad request
    var result: JSONObject? = null
    var error : JSONObject? = null

    init {
        this.listenerRequest = listenerRequest
        this.path = path
        this.method = method
        this.body = body
    }

    override fun doInBackground(vararg params: String?): JSONObject? {
        val url = URL(Constants.BASE_URL + path)
        Log.d("url : http ", url.toString())
        Log.d("path : http ", path)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = this.method
        connection.readTimeout = Constants.READ_TIMEOUT
        connection.connectTimeout = Constants.CONNECTION_TIMEOUT
        connection.setChunkedStreamingMode(0)

        if (method == Constants.HTTP_POST) {
            connection.doOutput = true
            connection.doInput = true
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Content-Type", "application/json")
        }
        try {
            if (method == Constants.HTTP_POST) {
                connection.connect()
                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.write(body.toString().toByteArray(Charset.defaultCharset()))
                outputStream.flush()
                outputStream.close()
            }
            result = readConnectionStream(connection.inputStream)

        } catch (e: Exception) {
            error = readConnectionStream(connection.errorStream)
            Log.e("error", e.message)
        } finally {
            connection.disconnect()
            Log.d("result : http", result.toString())
            return result
        }
    }

    override fun onPostExecute(result: JSONObject?) {
        listenerRequest.onRequestCompleted(result,error)
    }

    private fun readConnectionStream(stream : InputStream) : JSONObject{
        val inputStream = InputStreamReader(stream)
        val bufferedReader = BufferedReader(inputStream)
        val data = bufferedReader.readText()
        bufferedReader.close()
        inputStream.close()
        return JSONObject(data)
    }
}