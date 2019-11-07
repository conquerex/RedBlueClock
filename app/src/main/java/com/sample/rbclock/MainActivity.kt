package com.sample.rbclock

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val TAG: String = this.javaClass.simpleName
    val dateFormat = SimpleDateFormat("HH:mm:ss")

    var clockHandler = ClockHandler()
    var secFormat = SimpleDateFormat("ss")
    var time = ""
    var isRedColor = false
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

        // Observable
        val timeSetting = Observable
            .interval(1000L, TimeUnit.MILLISECONDS) // 1초에 숫자를 하나씩 발행 (0, 1, 2...)
            .map { it.toInt() } // Long to Int
            .take(6) // 10개만 발행
        val obs = timeSetting.publish()

        // Schedulers을 통해 동작할 스레드 지정
        obs.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        /**
         * ver2
         * Rx를 활용한 시계
         */
        obs.subscribe { getClock() }
        obs.connect()
        Thread.sleep(4000)

        // 1. onNext만 사용하는 경우
        obs.subscribe { t -> Log.d(TAG, "* * * " + t.toString()) }

        // 2. onNext, onError, onComplete 모두 사용하는 경우
        obs.subscribe(Consumer {
            Log.d(TAG, "* * * next :: " + it.toString())
        }, Consumer {
            Log.d(TAG, "* * * error :: " + it.toString())
        }, Action {
            Log.d(TAG, "* * * complete :: Done")
        })

    }

    /**
     * ver1
     * Thread를 활용한 시계
     */
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
                isRedColor = false
            } else {
                isRedColor = true
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
            if (isRedColor) {
                textClock.setTextColor(resources.getColor(R.color.clockred, null))
            } else {
                textClock.setTextColor(resources.getColor(R.color.clockblue, null))
            }

            textClock.text = time
        }
    }
}