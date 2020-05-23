package ch.instantpastime.memory

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import ch.instantpastime.memory.ui.BackStackHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_memory.*


const val num_images = 32

lateinit var drawer_toolbar: ActionBarDrawerToggle


class MemoryActivity : AppCompatActivity() {

    private val backStackHelper = BackStackHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        //android.os.Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        nav_view.setOnNavigationItemSelectedListener { backStackHelper.onNavigationItemSelected(it) }
        backStackHelper.loadFragment(nav_view.selectedItemId)

        var drawerlayout = findViewById(R.id.activity_main) as DrawerLayout

        drawer_toolbar = ActionBarDrawerToggle(this, drawerlayout, 0, 0)
        drawer_toolbar.syncState()

        drawerlayout.addDrawerListener(drawer_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        var nav_view_drawer = findViewById(R.id.nav_view_drawer) as NavigationView
        nav_view_drawer.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id: Int = item.getItemId()
                when (id) {
                    ch.instantpastime.R.id.info -> Toast.makeText(
                        this@MemoryActivity,
                        "À propos de ..",
                        Toast.LENGTH_SHORT
                    ).show()
                    ch.instantpastime.R.id.install -> Toast.makeText(
                        this@MemoryActivity,
                        "Installation",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> return true
                }
                return true
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (drawer_toolbar.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        if (!backStackHelper.onBackPressed()) {
            super.onBackPressed()
        }
    }

}
