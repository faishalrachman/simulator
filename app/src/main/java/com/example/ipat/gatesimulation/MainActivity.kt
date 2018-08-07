package com.example.ipat.gatesimulation

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import com.google.zxing.integration.android.IntentIntegrator
import android.widget.Toast
import android.R.attr.data
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.google.zxing.integration.android.IntentResult
import com.androidnetworking.error.ANError
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        opengate.setOnClickListener(View.OnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()
        })
        closegate.setOnClickListener(View.OnClickListener {
            closeGate()
        })
    }

    fun openGate(qr_hash: String) {
        val objek = JSONObject()
        objek.put("action", "open")
        objek.put("qr_hash", qr_hash)

        AndroidNetworking.initialize(applicationContext);
        AndroidNetworking.post(applicationContext.getString(R.string.base_url) + "/device")
                .addJSONObjectBody(objek)
                .addHeaders("Authorization",applicationContext.getString(R.string.apikey))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.getString("status").equals("success"))
                            Toast.makeText(applicationContext, "Sukses", Toast.LENGTH_LONG).show()
                        else
                            Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_LONG).show()

                    }

                    override fun onError(error: ANError) {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_LONG).show()
                        // handle error
                    }
                })
    }

    fun closeGate() {
        val objek = JSONObject()
        objek.put("action", "close")

        AndroidNetworking.initialize(applicationContext);
        AndroidNetworking.post(applicationContext.getString(R.string.base_url) + "/device")
                .addJSONObjectBody(objek)
                .addHeaders("Authorization",applicationContext.getString(R.string.apikey))
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        if (response.getString("status").equals("success"))
                            Toast.makeText(applicationContext, "Sukses", Toast.LENGTH_LONG).show()
                        else
                            Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_LONG).show()

                    }

                    override fun onError(error: ANError) {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_LONG).show()
                        // handle error
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Log.d("MainActivity", "Cancelled scan")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Log.d("MainActivity", "Scanned")
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                openGate(result.contents)
            }
        }
    }
}
