package com.example.colormaker

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

//    private var boxColor = intArrayOf(255, 0, 0)
    private lateinit var ResultColor: TextView
    private lateinit var seekbar:SeekBar
    private lateinit var seekbar1:SeekBar
    private lateinit var seekbar2:SeekBar
    private lateinit var switch:Switch
    private lateinit var switch1:Switch
    private lateinit var switch2:Switch
    private lateinit var textView:EditText
    private lateinit var textView1:EditText
    private lateinit var textView2:EditText
    private val seekBarIds = arrayOf(R.id.seekBar, R.id.seekBar2, R.id.seekBar3)
    private val textViewIds = arrayOf(R.id.textView, R.id.textView2, R.id.textView3)
    private val switchIds = arrayOf(R.id.switch1, R.id.switch2, R.id.switch3)
    val colormap= arrayOf("red","green","blue")
    private lateinit var Vm:MyViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ResultColor = findViewById<TextView>(R.id.Rclr)
        ConnectViews()
        MyPreferencesRepository.initialize(this)
        Vm=ViewModelProvider(this)[MyViewModel::class.java]
        lifecycleScope.launch{
            loadUIFlows()
        }
        Vm.flowUIValues()
        seekBarIds.forEachIndexed { index, sbId ->
            val mySeekBar = findViewById<SeekBar>(sbId)
            mySeekBar.setOnSeekBarChangeListener(createSeekBarListener(index))
        }

        switchIds.forEachIndexed { index, switchId ->
            val switchButton = findViewById<Switch>(switchId)
            switchButton.setOnCheckedChangeListener(createSwitchListener(index))
        }
        for (i in 0..2){
            var rsed=resources.getIdentifier(textViewIds[i].toString(), "id", packageName)
            var edt=findViewById<View>(rsed) as EditText
            edt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {

                    try {
                        var stt=s.toString().toFloat()
                        if (stt>1){
                            stt=1F
                            val message = "Max Value is 1!"
                            val duration = Toast.LENGTH_SHORT // or Toast.LENGTH_LONG
                            val toast = Toast.makeText(applicationContext, message, duration)
                            edt.setText("1.00")
                            toast.show()
                        }
                        var rbgvalue=(stt*255).toInt()
                        Vm.saveColor(rbgvalue,colormap[i])

                    }
                    catch (e:Exception){

                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {

                }
            })

        }
        val resetButton = findViewById<Button>(R.id.button)
        resetButton.setOnClickListener {
            Vm.saveColor(0,"red")
            Vm.saveColor(0,"green")
            Vm.saveColor(0,"blue")
            Vm.saveState(false,"red")
            Vm.saveState(false,"green")
            Vm.saveState(false,"blue")

        }



    }
    fun ConnectViews(){
        seekbar=findViewById(R.id.seekBar)
        seekbar1=findViewById(R.id.seekBar2)
        seekbar2=findViewById(R.id.seekBar3)
        switch=findViewById(R.id.switch1)
        switch1=findViewById(R.id.switch2)
        switch2=findViewById(R.id.switch3)
        textView=findViewById(R.id.textView)
        textView1=findViewById(R.id.textView2)
        textView2=findViewById(R.id.textView3)

    }
    private suspend fun loadUIFlows(){
        lifecycleScope.async {
            Vm._gcl.asStateFlow().collectLatest {
                seekbar1.progress = it
                textView1.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
            }
        }
        lifecycleScope.async {
            Vm._rcl.asStateFlow().collectLatest {
                seekbar.progress = it
                textView.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
            }
        }
        lifecycleScope.async {
            Vm._bcl.asStateFlow().collectLatest {
                seekbar2.progress = it
                textView2.setText(String.format("%.2f", (it.toDouble() / 255.toDouble())))
            }
        }

        lifecycleScope.async {
            combine(Vm._allcl.asStateFlow(),Vm._allst.asStateFlow()){
                    s,c->
                Pair(s,c)
            }.collectLatest {
                var st=it.second
                var colors=it.first
                var mem=IntArray(3)
                var rc=if (st[0]) colors[0] else 0
                var gc=if (st[1]) colors[1] else 0
                var bc=if (st[2]) colors[2] else 0
                findViewById<TextView>(R.id.Rclr).setBackgroundColor(Color.argb(255,rc,gc,bc))

            }
        }
        lifecycleScope.async {
            Vm._rsw.asStateFlow().collectLatest {
                switch.isChecked=it
                textView.isEnabled =it
                seekbar.isEnabled = it
            }

        }

        lifecycleScope.async {
            Vm._gsw.asStateFlow().collectLatest {
                switch1.isChecked=it
                textView1.isEnabled =it
                seekbar1.isEnabled = it
            }

        }

        lifecycleScope.async {
            Vm._bsw.asStateFlow().collectLatest {
                switch2.isChecked=it
                textView2.isEnabled =it
                seekbar2.isEnabled = it
            }

        }

    }
    
    private fun createSeekBarListener(index: Int): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Vm.saveColor(seekBar.progress,colormap[index])
            }
        }
    }





    private fun createSwitchListener(index: Int): CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { _, isChecked ->
            val sb = findViewById<SeekBar>(seekBarIds[index])
            Vm.saveState(isChecked,colormap[index])

            }
        }


    }


