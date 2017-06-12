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

    lateinit var notifier: NotificationManager
    lateinit var ftpClient: FTPClient
    var loggedIn = false

    companion object {
        val URI_TO_UPLOAD = "URI_TO_UPLOAD"
        val NAME_TO_UPLOAD = "NAME_TO_UPLOAD"
    }

    override fun onCreate() {
        super.onCreate()

        notifier = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ftpClient = FTPClient()
    }

    override fun onDestroy() {
        super.onDestroy()

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
        return ftpClient.isConnected
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
        return loggedIn
    }

    private fun print(message: String) = baseContext.toast(message)

    override fun onHandleIntent(intent: Intent) {
        I.Log("onHandleIntent()")

        if (ftp())
            upload(
                    intent.getParcelableExtra(URI_TO_UPLOAD),
                    intent.getStringExtra(NAME_TO_UPLOAD)
            )
    }

    fun upload(uri: Uri, remoteName: String) {
        val file = uri.getTempFile(baseContext) ?: return
        val length = file.length()
        val notifId = (Int.MAX_VALUE * Math.random()).toInt()
        var success = true

        val progressTicket = getString(R.string.ftp_ticket_uploading)
        val progressTitle = getString(R.string.ftp_uploading_s, remoteName)
        showNotif(progressTicket, progressTitle, 0, notifId)

        try {
            ftpClient.copyStreamListener = CopyStreamWatcher(length, { pr ->
                showNotif(progressTicket, progressTitle, pr, notifId)
            })

            if (!ftpClient.appendFile(remoteName, FileInputStream(file)))
                throw Exception(getString(R.string.ftp_upload_error_s, remoteName))
        } catch (e: Exception) {
            I.Log("FTP: " + e.toString())

            print(e.message ?: e.toString())
            success = false
        } finally {
            file.delete()
        }

        if (success)
            showNotif(getString(R.string.ftp_ticket_uploaded), getString(R.string.ftp_uploaded_s, remoteName), -1, notifId)
        else
            showNotif(getString(R.string.ftp_ticket_upload_error), getString(R.string.ftp_upload_error_s, remoteName), -1, notifId)
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
