package com.example.midv2

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.midv2.ui.countdown.CountdownFragment

class MyService: Service() {

    lateinit var countDownTimer: CountDownTimer
    private var isRunning = false
    private var onScreen = true
    private var foreTimeVisible: Long = 0L
    private var nowTimeVisible: Long = 0L
    val intent1 = Intent(Constant.TIME_UPDATE)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // stop the service when we receive the action ACTION_STOP
        // ACTION_STOP is nothing but a constant that is assigned any value
        if (intent?.action != null && intent.action.equals(Constant.ACTION_STOP, ignoreCase = true)) {
            stopForeground(true)
            stopSelf()
        }

        createNotificationChannel()
        showNotification()

        // avoid duplicate countdownTimer
        if (!isRunning) {
            isRunning = true
            // bug: null pointer in the case of second round
            val inputt = intent!!.getLongExtra(Constant.TIME_EXTRA, 0L)
            startCounting(inputt)
            Log.d("MyService", "yes startCounting?")
        }

        return START_STICKY
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        // how ba os kill process QQ (TO-DO)
    }

    private fun startCounting(inputt: Long) {
        var user_input = inputt
        user_input *= 60000L
        Log.d("MyService", "input? = $inputt")
        startTimer(input_time = user_input)
    }

    private fun startTimer(input_time: Long) {
        countDownTimer = object : CountDownTimer(input_time, 1000) {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onTick(millisUntilFinished: Long) {
                //val intent1 = Intent(Constant.TIME_UPDATE)
                val minute = (millisUntilFinished / 1000) / 60
                val second = (millisUntilFinished / 1000) % 60
                intent1.putExtra(Constant.TIME_EXTRA, "$minute:$second")
                Log.d("MyService", "$minute:$second")
                //intent1.putExtra(Constant.UI_10SEC, true)
                checkUsage()
                send5Sec()
                sendBroadcast(intent1)
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            private fun checkUsage() {

                val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                // start time = 2 min bf now
                val myStat = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()-120000, System.currentTimeMillis()
                )
                
                // head of when power of (== screen locked?)
                if (myStat==null) {
                    Log.d("MyService", "screen CLOSED")
                    return
                }

                for (i in myStat) {
                    if (i.packageName == Constant.PACKAGE_NAME) {
                        Log.d("MyService", "state: ${onScreen}")
                        Log.d("MyService", "fore: ${foreTimeVisible}")
                        Log.d("MyService", "now: ${nowTimeVisible}")

                        // update time and state
                        nowTimeVisible = i.lastTimeVisible

                        // first time start up the service
                        if (foreTimeVisible==0L) {
                            foreTimeVisible = i.lastTimeVisible
                        }

                        // back to after 10 sec app
                        if (nowTimeVisible-foreTimeVisible >= 10000 && !onScreen) {
                            // go to failed fragment
                            Log.d("MyService", "you FAILED")
                            intent1.putExtra(Constant.UI_10SEC, false)
                            stopForeground(true)
                            stopSelf()
                        }

                        // have state change
                        if (nowTimeVisible-foreTimeVisible >= 1500) {
                            foreTimeVisible = nowTimeVisible
                            onScreen = !onScreen
                        }


                    }
                }

                // null will be returned when locked
            }

            override fun onFinish() {
                // implement success state and deep nav to fragment 3
                Log.d("MyService", "yoooo DONEEEE")
                intent1.putExtra(Constant.TIME_FINISH, true)
                sendBroadcast(intent1)
                //stopForeground(true)
                //stopSelf()
            }
        }.start()

    }

    private fun send5Sec() {
        // send notification when leave app for 5 sec
        if (System.currentTimeMillis()-nowTimeVisible >= 5100 && !onScreen) {
            Log.d("MyService", "5 SEC!!")
            createNotificationChannel5Sec()
            showNotification5Sec()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java) // activity or fragment?

        // here open your activity with PendingIntent then open your fragment via handling the Intent inside of your activity
        // try try
        // val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.countdownFragment)
            .createPendingIntent()

        val notification = Notification
            .Builder(this, Constant.CHANNEL_ID)
            .setContentText("Countdown")
            .setSmallIcon(R.drawable.ic_baseline_self_improvement_24)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(Constant.COUNTDOWN_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(Constant.CHANNEL_ID,
                "my service channel",
                NotificationManager.IMPORTANCE_DEFAULT)

            val manager = getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotificationChannel5Sec() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val channel = NotificationChannel(
                Constant.CHANNEL_ID_5SEC,
                "5 sec channel",
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "5 sec channel"
            }

            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification5Sec() {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.countdownFragment)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(this, Constant.CHANNEL_ID_5SEC)
            .setSmallIcon(R.drawable.ic_baseline_sentiment_dissatisfied_24)
            .setContentTitle("Leave for 5 sec...")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(Constant.NOTI_5SEC_ID, builder.build())
        }
    }

}