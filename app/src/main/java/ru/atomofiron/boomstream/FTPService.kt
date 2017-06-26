package ru.atomofiron.boomstream

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.io.CopyStreamEvent
import org.apache.commons.net.io.CopyStreamListener
import java.io.FileInputStream
import android.app.Notification
import android.app.NotificationManager
import android.os.Build
import android.content.Context
import android.app.PendingIntent
import android.os.Handler
import android.os.Looper
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import java.net.URLEncoder


class FTPService : IntentService("FTPService") {

    lateinit var notifier: NotificationManager
    lateinit var ftpClient: FTPClient
    var loggedIn = false

    companion object {
        val URI_TO_UPLOAD = "URI_TO_UPLOAD"
        val NAME_TO_UPLOAD = "NAME_TO_UPLOAD"
        val EXTRA_OLD_NOTIF_ID = "EXTRA_OLD_NOTIF_ID"
    }

    override fun onCreate() {
        super.onCreate()


        notifier = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ftpClient = FTPClient()
    }

    override fun onDestroy() {
        super.onDestroy()
        I.Log("ftps: onD")

        disconnect()
    }

    private fun ftp(): Boolean = (ftpClient.isConnected || connect()) && (loggedIn || login()) && ftpClient.isAvailable

    private fun connect(): Boolean {
        disconnect()

        try {
            ftpClient.connect(App.FTP_HOST, App.FTP_PORT)
            ftpClient.enterRemotePassiveMode()
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
        } catch (e: Exception) {
            print(getString(R.string.ftp_error_connect_s, e.message))
            I.Log("Exception: "+e.toString())
            return false
        }

        if (!ftpClient.isConnected) {
            print(getString(R.string.ftp_error_connect))
            return false
        } else
            return true
    }

    private fun disconnect() {
        loggedIn = false

        try {
            ftpClient.logout()
            ftpClient.disconnect()
        } catch (e: Exception) {}
    }

    private fun login(): Boolean {
        try {
            loggedIn = ftpClient.login(App.ftpLogin, App.apikey)
        } catch (e: Exception) {
            I.Log("Exception: "+e.toString())
            print(getString(R.string.ftp_error_login_s, e.message))
            return false
        }

        if (!loggedIn)
            print(getString(R.string.ftp_error_login))

        return loggedIn
    }

    private fun print(message: String) {
        Handler(Looper.getMainLooper()).post({ applicationContext.toast(message) })
    }

    override fun onHandleIntent(intent: Intent) {
        I.Log("onHandleIntent()")

        if (ftp())
            upload(intent)
    }

    fun upload(intent: Intent) {

        val uri: Uri = intent.getParcelableExtra(URI_TO_UPLOAD)
        val remoteName = intent.getStringExtra(NAME_TO_UPLOAD)

        val file = uri.getTempFile(baseContext) ?: return
        val length = file.length()
        var success = true

        var notifId = (Int.MAX_VALUE * Math.random()).toInt()
        if (intent.extras.containsKey(EXTRA_OLD_NOTIF_ID))
            notifId = intent.getIntExtra(EXTRA_OLD_NOTIF_ID, notifId)

        val progressTicket = getString(R.string.ftp_ticket_uploading)
        val progressTitle = getString(R.string.ftp_uploading_s, remoteName)
        showNotif(progressTicket, progressTitle, 0, notifId, null)

        try {
            ftpClient.copyStreamListener = CopyStreamWatcher(length, { pr ->
                showNotif(progressTicket, progressTitle, pr, notifId, null)
            })

            // URLEncoder нужен, потому что ftp клиент от apache некорректно передаёт кириллицу
            if (!ftpClient.appendFile(URLEncoder.encode(remoteName, "UTF-8"), FileInputStream(file)))
                throw Exception(getString(R.string.ftp_upload_error_s, remoteName))
        } catch (e: Exception) {
            I.Log("FTP: " + e.toString())

            print(e.message ?: e.toString())
            success = false
        } finally {
            file.delete()
        }

        if (success)
            showNotif(getString(R.string.ftp_ticket_uploaded), getString(R.string.ftp_uploaded_s, remoteName), -1, notifId, null)
        else
            showNotif(getString(R.string.ftp_ticket_upload_error), getString(R.string.ftp_upload_error_s, remoteName), -1, notifId, intent)
    }



    /**
     * Создаёт или обновляет уведомление в шторке.
     *
     * @param progress  прогресс от 0 до 100, -1 - без прогрессбара
     * @param actionIntent  намерение, с помощью которого была инициирована отправка файла (actionIntent != null только когда отправка не удалась)
     */
    private fun showNotif(ticker: String, title: String, progress: Int, notifId: Int, actionIntent: Intent?) {
        val context = applicationContext
        val builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_small)
                .setTicker(ticker)
                .setContentTitle(title)
                .setAutoCancel(true)

        // намерение для повторной отправки
        if (actionIntent != null) {
            val pendingIntent = PendingIntent.getService(context, notifId,
                    actionIntent.putExtra(EXTRA_OLD_NOTIF_ID, notifId),
                    PendingIntent.FLAG_UPDATE_CURRENT)

            builder.setContentIntent(pendingIntent)
            builder.addAction(
                    // при Build.VERSION.SDK_INT < 23 нет возможности отобразить векторное изображение
                    NotificationCompat.Action.Builder(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            R.drawable.ic_replay else R.drawable.ic_replay_img,
                            getString(R.string.try_upload_again),
                            pendingIntent
                    ).build()
            )
        }

        val progress = Math.max(Math.min(progress, 100), -1) // защита типа
        if (progress != -1)
            builder.setProgress(100, Math.min(progress, 100), progress % 100 == 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))

        val notif = builder.build()

        if (progress != -1)
            notif.flags = notif.flags or Notification.FLAG_NO_CLEAR

        notifier.notify(notifId, notif)
    }

    class CopyStreamWatcher(val length: Long, val listener: (progress: Int) -> Unit) : CopyStreamListener {
        val MIN_INTERVAL = 1000 // минимум - 1 секунда, максимум - задержка сети
        var timeStamp = 0L

        override fun bytesTransferred(p0: Long, p1: Int, p2: Long) {
            val now = System.currentTimeMillis()
            if (now - timeStamp > MIN_INTERVAL || p0 == length) {
                listener((Math.ceil(p0.toDouble() / length.toDouble() * 100)).toInt())

                timeStamp = now
            }
        }
        override fun bytesTransferred(p0: CopyStreamEvent?) { /* не вызывается ни разу */ }
    }
}
