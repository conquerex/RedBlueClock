package com.sample.rbclock

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var clockHandler = ClockHandler()
    var dateFormat = SimpleDateFormat("HH:mm:ss")
    var secFormat = SimpleDateFormat("ss")
    var time = ""
    var redColor = false
    var blueNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runningClock()
        buttonChange.setOnClickListener {
            blueNumber = (blueNumber + 1) % 2
        }
    }

    fun runningClock() {
        var thread = Thread(Runnable {
            while (true) {
                try {
                    time = dateFormat.format(Date(System.currentTimeMillis()))
                    
                    // init : 짝수 파란색, 홀수 빨간색
                    if (secFormat.format(Date(System.currentTimeMillis())).toInt() % 2 == blueNumber) {
                        redColor = false
                    } else {
                        redColor = true
                    }

                    val message = clockHandler.obtainMessage()
                    clockHandler.sendMessage(message)

                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    //
                }
            }
        })
        thread.start()
    }

    inner class ClockHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (redColor) {
                textClock.setTextColor(resources.getColor(R.color.clockred, null))
            } else {
                textClock.setTextColor(resources.getColor(R.color.clockblue, null))
            }

            textClock.text = time
        }
    }
}