package com.ogzkesk.testproject.third

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.ogzkesk.testproject.R
import com.ogzkesk.testproject.showToast
import java.io.IOException

/**
 * Set Manifest.xml
 *     <uses-permission android:name="android.permission.NFC"/>
 *     <uses-feature android:name="android.hardware.nfc" android:required="true"/>
 *
 *      <intent-filter>
 *          <action android:name="android.nfc.action.NDEF_DISCOVERED"/>
 *          <data android:mimeType="text/plain"/>
 *          <category android:name="android.intent.category.DEFAULT"/>
 *      </intent-filter>
 */

class NfcActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    //TODO android.nfc.tech. check tech descriptions.

    private lateinit var pendingIntent: PendingIntent
    private lateinit var writingTagFilters: IntentFilter
    private var adapter: NfcAdapter? = null
    private var mTag: Tag? = null
    private var writeMode: Boolean = false

    private lateinit var mToggleButton: ToggleButton
    private lateinit var mSendButton: Button
    private lateinit var mMessageField: EditText
    private lateinit var mTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        initUI()
        initAdapter()
    }

    private fun initUI() {
        mToggleButton = findViewById(R.id.toggle_button)
        mTextView = findViewById(R.id.text_view_explanation)
        mSendButton = findViewById(R.id.btn_send)
        mMessageField = findViewById(R.id.et_message)

        mToggleButton.setOnCheckedChangeListener { _, b ->
            onToggleClick(b)
        }

        mSendButton.setOnClickListener {
            onSendClick(mMessageField.text.toString())
        }
    }

    private fun initAdapter() {
        adapter = NfcAdapter.getDefaultAdapter(this)
        if (adapter == null) {
            showToast(NOT_SUPPORTED)
        }
    }


    private fun onSendClick(message: String) {
        if (mTag == null) {
            showToast(ERROR_DETECTED)
            return
        }

        try {
            write("Plain text: $message Tag: $mTag")
            showToast(WRITE_SUCCESS)
        } catch (e: IOException) {
            showToast(WRITE_ERROR + e.message)
            e.printStackTrace()
        } catch (e: FormatException) {
            showToast(WRITE_ERROR + e.message)
            e.printStackTrace()

        }
    }

    private fun onToggleClick(isChecked: Boolean) {
        if (isChecked) {

        }
    }

    private fun write(data: String) {

    }

    private fun read() {
        readFromIntent(intent)

        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        val intentFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        writingTagFilters = intentFilter
    }

    private fun readFromIntent(intent: Intent?) {
        if (intent == null) {
            return
        }

        when (intent.action) {
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                val rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                val msg: MutableList<NdefMessage> = mutableListOf()
                if(rawMsg != null){
                    msg.clear()
                    msg.addAll(rawMsg.map { it as NdefMessage })
                }
            }
        }
    }

    class Serv : Service() {
        override fun onBind(p0: Intent?): IBinder? {
            return null
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        mTag = tag
    }

    companion object {
        const val ERROR_DETECTED = "No Nfc tag detected"
        const val WRITE_SUCCESS = "Text written successfully"
        const val WRITE_ERROR = "Error during writing. Please try again"
        const val NOT_SUPPORTED = "This device does not support Nfc"
    }
}