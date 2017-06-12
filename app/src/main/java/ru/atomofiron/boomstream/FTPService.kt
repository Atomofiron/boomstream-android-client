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
import android.graphics.drawable.Icon


class FTPService : IntentService("FTPService") {

    lateinit var notifer: NotificationManager

    companion object {
        val URI_TO_UPLOAD = "URI_TO_UPLOAD"
        val NAME_TO_UPLOAD = "NAME_TO_UPLOAD"
    }

    override fun onCreate() {
        super.onCreate()

        notifer = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onHandleIntent(intent: Intent) {
        I.Log("onHandleIntent()")


        upload(
                intent.getParcelableExtra(URI_TO_UPLOAD),
                intent.getStringExtra(NAME_TO_UPLOAD)
        )
    }

    fun upload(uri: Uri, remoteName: String) {
        val file = uri.getTempFile(baseContext) ?: return
        val length = file.length()
        val notifId = (Int.MAX_VALUE * Math.random()).toInt()
        val client = FTPClient()
        var success = true

        val progressTicket = getString(R.string.ftp_ticket_uploading)
        val progressTitle = getString(R.string.ftp_uploading, remoteName)
        showNotif(progressTicket, progressTitle, 0, notifId)

        try {
            client.connect(App.FTP_HOST, App.FTP_PORT)

            if (!client.login(App.ftpLogin, App.apikey))
                throw Exception(getString(R.string.ftp_error_login))

            client.enterRemotePassiveMode()
            client.enterLocalPassiveMode()
            client.setFileType(FTP.BINARY_FILE_TYPE)
            client.copyStreamListener = object : CopyStreamListener {
                val INTERVAL = 1000
                var timeStamp = 0L

                override fun bytesTransferred(p0: Long, p1: Int, p2: Long) {
                    val now = System.currentTimeMillis()
                    if (now - timeStamp > INTERVAL || p0 == length) {
                        val progress = Math.ceil(p0.toDouble() / length.toDouble() * 100)
                        showNotif(progressTicket, progressTitle, progress.toInt(), notifId)

                        timeStamp = now
                    }
                }
                override fun bytesTransferred(p0: CopyStreamEvent?) { /* не вызывается ни разу */ }
            }

            if (!client.appendFile(remoteName, FileInputStream(file)))
                throw Exception(getString(R.string.ftp_upload_error, remoteName))
        } catch (e: Exception) {
            I.Log("FTP: " + e.toString())

            baseContext.toast(e.message ?: e.toString())
            success = false
        } finally {
            file.delete()

            try {
                client.disconnect()
            } catch (e: Exception) {}
        }

        if (success)
            showNotif(getString(R.string.ftp_ticket_uploaded), getString(R.string.ftp_uploaded, remoteName), -1, notifId)
        else
            showNotif(getString(R.string.ftp_ticket_upload_error), getString(R.string.ftp_upload_error, remoteName), -1, notifId)
    }



    /**
     * Создаёт или обновляет уведомление в шторке.
     *
     * @param progress  прогресс от 0 до 100, -1 - без прогрессбара
     */
    private fun showNotif(ticker: String, title: String, progress: Int, notifId: Int) {
        val context = applicationContext
        val builder = Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(ticker)
                .setContentTitle(title)
                .setAutoCancel(true)

        val progress = Math.max(Math.min(progress, 100), -1) // защита типа
        if (progress != -1)
            builder.setProgress(100, Math.min(progress, 100), progress % 100 == 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            builder.setLargeIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))

        val notif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            builder.build() else builder.notification

        if (progress != -1)
            notif.flags = notif.flags or Notification.FLAG_NO_CLEAR

        notifer.notify(notifId, notif)
    }
}
