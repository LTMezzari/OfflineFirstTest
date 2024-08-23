package mezzari.torres.lucas.android.notification.local

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.legacy.content.WakefulBroadcastReceiver
import mezzari.torres.lucas.android.logger.AppLogger
import mezzari.torres.lucas.android.notification.local.manager.LocalNotificationManager
import mezzari.torres.lucas.android.worker.WorkScheduler
import mezzari.torres.lucas.core.archive.elvis
import mezzari.torres.lucas.core.archive.launch
import mezzari.torres.lucas.core.interfaces.AppDispatcher
import org.joda.time.DateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * @author Lucas T. Mezzari
 * @since 23/08/24
 */
class LocalNotificationScheduler(
    private val application: Application,
    private val appLogger: AppLogger,
    private val dispatcher: AppDispatcher,
) : WorkScheduler, KoinComponent {

    val notifications: List<LocalNotificationManager> by lazy {
        getKoin().getAll()
    }

    val manager: AlarmManager? by lazy {
        application.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    override fun scheduleWork() {
        if (
            Build.VERSION.SDK_INT > Build.VERSION_CODES.S
            && manager?.canScheduleExactAlarms() == false
        ) {
            return
        }

        dispatcher.io.launch {
            cancelNotifications()
            scheduleRepeater()
            scheduleNotifications()
        }
    }

    override fun cancelWork() {
        dispatcher.io.launch {
            cancelNotifications()
            cancelRepeater()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleRepeater() {
        try {
            val intent = Intent(application, NotificationSchedulerReceiver::class.java)
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    application,
                    SCHEDULER_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    application,
                    SCHEDULER_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }
            val time = DateTime.now().plusDays(1).withTimeAtStartOfDay()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Work around doze mode
                manager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time.millis,
                    pendingIntent
                )
                appLogger.logMessage("Repeater scheduled to ${time.toString("HH:mm")}")
                return
            }

            manager?.setExact(
                AlarmManager.RTC_WAKEUP,
                time.millis,
                pendingIntent
            )
            appLogger.logMessage("Repeater scheduled to ${time.toString("HH:mm")}")
        } catch (e: Exception) {
            appLogger.logError(e)
            appLogger.recordError(e)
        }
    }

    private suspend fun scheduleNotifications() {
        val notificationSet = HashSet<Long>()
        for (notification in notifications) {
            try {
                notificationSet.addAll(notification.getNotificationsTimeInMillis())
            } catch (e: Exception) {
                appLogger.logError(e)
                appLogger.recordError(e)
                continue
            }
        }
        notificationSet.forEach { scheduleNotificationAtTime(it) }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleNotificationAtTime(time: Long) {
        try {
            if (DateTime.now().withMillis(time).isBeforeNow)
                return
            appLogger.logMessage(
                "Scheduling notification at ${
                    DateTime.now().withMillis(time).toString("HH:mm")
                }"
            )

            val intent = Intent(application, NotificationReceiver::class.java)
            intent.putExtra("notificationTime", time)
            val requestCode = time shl 4
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    application,
                    NOTIFICATIONS_REQUEST_CODE + requestCode.toInt(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    application,
                    NOTIFICATIONS_REQUEST_CODE + requestCode.toInt(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }
            manager?.setExact(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        } catch (e: Exception) {
            appLogger.logError(e)
            appLogger.recordError(e)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelRepeater() {
        val intent = Intent(application, NotificationSchedulerReceiver::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                application,
                SCHEDULER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                application,
                SCHEDULER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }
        manager?.cancel(pendingIntent)
        pendingIntent.cancel()
        appLogger.logMessage("Repeater cancelled")
    }

    private suspend fun cancelNotifications() {
        val notificationSet = HashSet<Long>()
        for (notification in notifications) {
            try {
                notificationSet.addAll(notification.getNotificationsTimeInMillis())
            } catch (e: Exception) {
                appLogger.logError(e)
                appLogger.recordError(e)
            }
        }
        notificationSet.forEach { cancelNotification(it) }
        (application.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.cancelAll()
        appLogger.logMessage("All notifications cancelled")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelNotification(time: Long) {
        try {
            if (DateTime.now().withMillis(time).isBeforeNow) {
                return
            }

            val intent = Intent(application, NotificationReceiver::class.java)
            intent.putExtra("notificationTime", time)
            val requestCode = time shl 4
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    application,
                    NOTIFICATIONS_REQUEST_CODE + requestCode.toInt(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    application,
                    NOTIFICATIONS_REQUEST_CODE + requestCode.toInt(),
                    intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }
            manager?.cancel(pendingIntent)
        } catch (e: Exception) {
            appLogger.logError(e)
            appLogger.recordError(e)
        }
    }

    class NotificationSchedulerReceiver : WakefulBroadcastReceiver(), KoinComponent {

//        private val preferences: AppPreferences by inject()
        private val scheduler: WorkScheduler by inject()
        private val appLogger: AppLogger by inject()
        private val dispatcher: AppDispatcher by inject()

        override fun onReceive(context: Context?, intent: Intent?) {
            val scheduler = scheduler as? LocalNotificationScheduler elvis {
                return
            }

//            val lastTimeScheduled = preferences.notificationRepeaterLastExecution
//            val lastDate = DateTime.now().withMillis(lastTimeScheduled).withTimeAtStartOfDay()
//            val now = DateTime.now().withTimeAtStartOfDay()
//            if (now.isEqual(lastDate) && lastTimeScheduled != -1L) {
//                appLogger.logMessage(
//                    "${now.toString("dd/MM - HH:mm")} -> Last Execution: ${
//                        lastDate.toString("dd/MM - HH:mm")
//                    }"
//                )
//                return
//            }

            dispatcher.io.launch {
                try {
                    scheduler.scheduleNotifications()
                    scheduler.scheduleRepeater()
//                    preferences.notificationRepeaterLastExecution = DateTime.now().millis
                    appLogger.logMessage("Repeater Rescheduled")
                } catch (e: Exception) {
                    appLogger.logError(e)
                    appLogger.recordError(e)
                }
            }
        }
    }

    class NotificationReceiver : WakefulBroadcastReceiver(), KoinComponent {

        private val scheduler: WorkScheduler by inject()
        private val dispatcher: AppDispatcher by inject()
        private val appLogger: AppLogger by inject()

        override fun onReceive(context: Context?, intent: Intent?) {
            appLogger.logMessage("Notifying User")
            val mContext = context ?: return
            val mIntent = intent ?: return
            val scheduler = scheduler as? LocalNotificationScheduler elvis {
                return
            }

            val notificationTime = mIntent.getLongExtra("notificationTime", -1)
            if (notificationTime == -1L) {
                return
            }
            appLogger.logMessage(
                "Notification for ${
                    DateTime.now().withMillis(notificationTime).toString("HH:mm")
                }"
            )

            dispatcher.io.launch {
                for (notification in scheduler.notifications) {
                    try {
                        if (!notification.isTimeToNotify(notificationTime)) {
                            continue
                        }
                        notification.showNotificationAtTime(notificationTime, mContext)
                    } catch (e: Exception) {
                        appLogger.logError(e)
                        appLogger.recordError(e)
                        continue
                    }
                }
            }
        }
    }

    companion object {
        private const val SCHEDULER_REQUEST_CODE = 2102
        private const val NOTIFICATIONS_REQUEST_CODE = 932321
    }
}