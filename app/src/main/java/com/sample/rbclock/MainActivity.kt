package com.sample.rbclock

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val TAG: String = this.javaClass.simpleName

    var clockHandler = ClockHandler()
    var dateFormat = SimpleDateFormat("HH:mm:ss")
    var secFormat = SimpleDateFormat("ss")
    var time = ""
    var redColor = false
    var blueNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        runningClock()
        buttonChange.setOnClickListener {
            blueNumber = (blueNumber + 1) % 2
        }

        // RX 학습용 코드
//        Observable.just(1,2,3,4,5)
//            .filter { it % 2 == 1 }
//            .subscribe { Log.d(TAG, "* * * " + it.toString()) }
//
//        Observable.empty<Int>()
//            .single(0)
//            .subscribe { t -> Log.d(TAG, "* * * " + t.toString()) }

        val timeSetting = Observable
            .interval(1000L, TimeUnit.MILLISECONDS) // 1초에 숫자를 하나씩 발행 (0, 1, 2...)
            .map { it.toInt() } // Long to Int
            .take(6) // 10개만 발행

        val obs = timeSetting.publish()
        obs.subscribe { getClock() }
        obs.connect()
        Thread.sleep(4000)
        obs.subscribe { t -> Log.d(TAG, "* * * " + t.toString()) }

    }

    fun runningClock() {
        var thread = Thread(Runnable {
            while (true) {
                getClock()
            }
        })
        thread.start()
    }

    fun getClock() {
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