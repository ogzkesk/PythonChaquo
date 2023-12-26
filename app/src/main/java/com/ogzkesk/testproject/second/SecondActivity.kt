package com.ogzkesk.testproject.second

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.ogzkesk.testproject.R
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {

    private val viewModel: SecondViewModel by viewModels()
    private lateinit var mAnswerTextView: TextView
    private lateinit var mSendButton: Button
    private lateinit var mEditText: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)




        mAnswerTextView = findViewById(R.id.tv_answer)
        mSendButton = findViewById(R.id.btn_send)
        mEditText = findViewById(R.id.et_message)

        val gemini = Gemini()

        mSendButton.setOnClickListener {
            lifecycleScope.launch {

                mAnswerTextView.text = buildString {
                    if(mAnswerTextView.text.isBlank()){
                        append(mEditText.text)
                    } else {
                        append("\n")
                        append(mEditText.text)
                    }
                }

                mEditText.setText("")


                val response = gemini.sendMessage(mEditText.text.toString())
                mAnswerTextView.text = buildString {
                    if(mAnswerTextView.text.isBlank()){
                        append(response)
                    } else {
                        append(mAnswerTextView.text)
                        append("\n")
                        append(response)
                    }
                }
            }
        }
    }
}