package com.ogzkesk.testproject.third

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.ogzkesk.testproject.R
import com.ogzkesk.testproject.showToast


/**
 * Set Manifest.xml
 *
 *     <uses-permission android:name="android.permission.NFC"></uses-permission>
 *     <uses-feature android:name="android.hardware.nfc" android:required="true"></uses-feature>
 *
 *      <intent-filter>
 *          <action android:name="android.nfc.action.NDEF_DISCOVERED"/>
 *          <data android:mimeType="text/plain"/>
 *          <category android:name="android.intent.category.DEFAULT"/>
 *      </intent-filter>
 */

class NfcActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var mTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        initUI()
        initAdapter()

    }

    private fun initUI() {
        mTextView = findViewById(R.id.text_view_explanation)
    }

    private fun initAdapter(){
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            nfcAdapter.enableReaderMode(this,this,NfcAdapter.FLAG_READER_NFC_A, bundleOf())
        }catch (e: Exception){
            showToast(e.localizedMessage ?: NOT_SUPPORTED)
            e.printStackTrace()
        }

    }

    override fun onTagDiscovered(tag: Tag?) {
        if(tag == null){
            return
        }




    }

    companion object {
        const val NOT_SUPPORTED = "Device not supporting Nfc"
    }
}