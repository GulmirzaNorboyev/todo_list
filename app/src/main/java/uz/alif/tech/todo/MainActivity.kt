package uz.alif.tech.todo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import uz.alif.tech.todo.common.lazyFast
import uz.alif.tech.todo.common.setupWithNavController
import uz.alif.tech.todo.ui.add_edit.KEY_ADD_MODE

class MainActivity : AppCompatActivity(),
    NavController.OnDestinationChangedListener {

    private var currentNavController: LiveData<NavController>? = null
    private val navController by lazyFast {
        Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }
    private var isAddMenuVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
    }

    private fun setupNavigation() {
        setSupportActionBar(toolbar)

        nav_view.setOnNavigationItemReselectedListener(null)
        val navGraphIds = listOf(
            R.navigation.navigation_home,
            R.navigation.navigation_process,
            R.navigation.navigation_finished
        )

        val controller = nav_view.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        controller.observe(this, { navController ->
            setupActionBarWithNavController(navController)
            navController.addOnDestinationChangedListener(this)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_todo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.isVisible = isAddMenuVisible
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            val args = bundleOf(
                Pair(KEY_ADD_MODE, true)
            )
            navController.navigate(R.id.nav_add_edit_todo, args)
        }
        return true
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        isAddMenuVisible = destination.id == R.id.nav_home ||
                destination.id == R.id.nav_add_edit_todo
        invalidateOptionsMenu()
    }
}