package ru.atomofiron.boomstream.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.adapters.NotesAdapter
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.fragments.FolderFragment
import ru.atomofiron.boomstream.fragments.MediaFragment


class MainActivity : AppCompatActivity(), NotesAdapter.OnMediaClickListener, NavigationView.OnNavigationItemSelectedListener {

    private val FRAGMENT_FOLDER_TAG = "FRAGMENT_FOLDER_TAG"
    private val FRAGMENT_MEDIA_TAG = "FRAGMENT_MEDIA_TAG"
    private val OPENED_MEDIA_TAG = "OPENED_MEDIA_TAG"
    var onBackPressedListener: OnBackPressedListener? = null
    var openedMedia: Media? = null

    lateinit var folderFragment: android.app.Fragment
    lateinit var mediaFragment: android.app.Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.setDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)


        if (savedInstanceState != null && savedInstanceState.containsKey(OPENED_MEDIA_TAG)) {
            I.Log("savedInstanceState: "+savedInstanceState.getString(OPENED_MEDIA_TAG))
            openedMedia = Gson().fromJson(savedInstanceState.getString(OPENED_MEDIA_TAG), Media::class.java)
        }

        setFragment(if (openedMedia == null) FRAGMENT_FOLDER_TAG else FRAGMENT_MEDIA_TAG)
        I.Log("onCreate(" + fragmentManager.backStackEntryCount + ") END")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (openedMedia != null)
            outState.putString(OPENED_MEDIA_TAG, Gson().toJson(openedMedia))
        super.onSaveInstanceState(outState)
    }

    private fun setFragment(tag: String) {
        val fragment: Fragment
        when (tag) {
            FRAGMENT_FOLDER_TAG -> fragment = FolderFragment() as Fragment
            FRAGMENT_MEDIA_TAG -> fragment = MediaFragment().setMedia(openedMedia!!) as Fragment
            else -> return
        }
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, FRAGMENT_FOLDER_TAG)
                .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (onBackPressedListener?.onBackPressed() ?: false)
                return

            if (openedMedia != null) {
                setFragment(FRAGMENT_FOLDER_TAG)
                openedMedia = null
                return
            }

            super.onBackPressed()
            startActivity(Intent(this, ApikeyActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_exit -> finish()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openMedia(media: Media) {
        openedMedia = media

        setFragment(FRAGMENT_MEDIA_TAG)
        I.Log("openMedia(" + fragmentManager.backStackEntryCount + ") END")
    }

    override fun onMediaClick(media: Media) {
        openMedia(media)
    }

    interface OnBackPressedListener {
        fun onBackPressed(): Boolean
    }
}
