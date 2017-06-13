package ru.atomofiron.boomstream.activities

import android.app.Activity
import android.app.FragmentTransaction
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.atomofiron.boomstream.*
import ru.atomofiron.boomstream.adapters.NotesAdapter
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import ru.atomofiron.boomstream.fragments.FolderFragment
import ru.atomofiron.boomstream.fragments.MediaFragment


class MainActivity : AppCompatActivity(), NotesAdapter.OnMediaClickListener, NavigationView.OnNavigationItemSelectedListener {

    var onBackPressedListener: OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.setDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)


        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, FolderFragment())
                    .commitAllowingStateLoss()
    }


    private fun setFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (onBackPressedListener?.onBackPressed() ?: false)
                return

            if (supportFragmentManager.backStackEntryCount > 0)
                super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_apikey -> startActivity(Intent(this, ApikeyActivity::class.java))
            R.id.nav_settings -> {}
            R.id.nav_exit -> finish()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode != Activity.RESULT_OK || intent == null)
            return

        when (requestCode) {
            I.ACTION_VIDEO_CAPTURE, I.ACTION_VIDEO_PICK -> checkFTPLoginAndSendVideo(intent.data!!)
        }
    }

    private fun checkFTPLoginAndSendVideo(uri: Uri) {
        if (App.ftpLogin.isEmpty())
            fragment_container.snack(R.string.no_ftp_login)
        else
            onSendVideo(uri)
    }

    private fun onSendVideo(uri: Uri) {
        I.showTextRequest(this, R.string.medias_name, "", { input ->
            startService(
                    Intent(this, FTPService::class.java)
                            .putExtra(FTPService.URI_TO_UPLOAD, uri)
                            .putExtra(FTPService.NAME_TO_UPLOAD, if (input.endsWith(".mp4")) input else input + ".mp4")
            )
        })

    }

    override fun onMediaClick(media: Media) {
        setFragment(MediaFragment.newInstance(media))
    }

    interface OnBackPressedListener {
        fun onBackPressed(): Boolean
    }
}
