package com.ogzkesk.testproject.python

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chaquo.python.Kwarg
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.android.PyApplication
import com.ogzkesk.testproject.databinding.ActivityPythonBinding

class PythonActivity : AppCompatActivity() {

    private lateinit var  binding: ActivityPythonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPythonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!Python.isStarted()){
            println("APP python started")
            Python.start(AndroidPlatform(this))
        }

        val py = Python.getInstance()
        val mainPyFile = py.getModule("main")

        val getExecDetails = mainPyFile["get_exec_details"]
        val details = getExecDetails?.call()
        binding.tv1.text = details.toString()
        println(details)

        val sumFun = mainPyFile["sumOp"]
        val sum = sumFun?.call(intArrayOf(10,15,20))
        binding.tv2.text = sum.toString()
        println(sum)

        val powFun = mainPyFile["powOp"]
        val pow = powFun?.call(2,4)
        binding.tv3.text = pow.toString()
        println(pow)

        val numsLength = mainPyFile["nums_len"]?.toInt()
        val numsLengthStr = mainPyFile["nums_len_str"]?.toString()
        binding.tv4.text = numsLength.toString()
        binding.tv5.text = numsLengthStr.toString()
        println("Nums Length is $numsLength")
        println("Nums Length is $numsLengthStr")

        val matrixFunc = mainPyFile["npMatrixSum"]
        val output = matrixFunc?.call(2,3)
        binding.tv6.text = output.toString()
        println(output)

        // Python Class

        val ops = mainPyFile["ops"]

        binding.tv7.text = ops.toString()
        println("Operations: $ops") // this is class Operations in main.py

        binding.tv8.text = ops?.get("num_ops").toString()
        println("num_ops : ${ops?.get("num_ops")}")

        binding.tv9.text = ops?.get("meanOp").toString()
        println("mean func : ${ops?.get("meanOp")}")

        binding.tv10.text = ops?.get("maxOp").toString()
        println("mean func : ${ops?.get("maxOp")}")

        Kwarg("key","value")

    }
}