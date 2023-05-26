package com.example.financeassistant.main_window

import com.example.financeassistant.R

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.dmsflatmanager.main_window.flatPage.FlatPageFragment
import com.example.financeassistant.base.ViewBindingActivity
import com.example.financeassistant.classes.SideMenuItem
import com.example.financeassistant.databinding.ActivityMainBinding
import com.example.financeassistant.main_window.creditPage.CreditPageFragment
import com.example.financeassistant.main_window.taskPage.TaskPageFragment
import com.example.financeassistant.sideMenu.SideMenuFragment
import com.example.financeassistant.utils.ToolbarUtils

import java.util.ArrayList

class FinanceManager : ViewBindingActivity<ActivityMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        = ActivityMainBinding::inflate

    lateinit var sideMenuFragment: SideMenuFragment

    lateinit var pager: ViewPager
    lateinit var pagerAdapter: PagerAdapter

    internal val DIALOG_DELETE_ALL = 1

    internal var myClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            // положительная кнопка
            Dialog.BUTTON_POSITIVE -> {
                val position = pager.currentItem
            }
        }//Fragment fragment = (Fragment) getFragmentManager().findFragmentById(R.id.PageCredit);
        //fragment.DeleteAll();
    }

    override fun setup() {

        val tool = findViewById<Toolbar>(R.id.toolbar_actionbar)
        setSupportActionBar(tool)

        ToolbarUtils.initNavigationBar(this, false)

        setupSideMenu()

        /*
        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < 3; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText("Tab " + (i + 1))
                            .setTabListener(tabListener));
        }
*/


        val inflater = LayoutInflater.from(this)
        val pages = ArrayList<View>()

        var page = inflater.inflate(R.layout.activity_maintask, null)
        pages.add(page)

        page = inflater.inflate(R.layout.credit_fragment, null)
        pages.add(page)

        page = inflater.inflate(R.layout.activity_payment, null)
        pages.add(page)

        pager = findViewById<View>(R.id.pager) as ViewPager
        pagerAdapter = MyFragmentPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter

        pager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                Log.d(TAG, "onPageSelected, position = $position")

                ToolbarUtils.setupActionBar(this@FinanceManager, pager.currentItem)

            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        pager.currentItem = 0

    }

    override fun onDestroy() {
        super.onDestroy()
        // закрываем подключение при выходе
    }

    override fun onCreateDialog(id: Int): Dialog {
        if (id == DIALOG_DELETE_ALL) {
            val adb = AlertDialog.Builder(this)
            // заголовок
            adb.setTitle(R.string.dialog_delete)
            // сообщение
            adb.setMessage(R.string.dialog_delete_all)
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info)
            // кнопка положительного ответа
            adb.setPositiveButton(R.string.dialog_button_yes, myClickListener)
            // кнопка нейтрального ответа
            adb.setNeutralButton(R.string.dialog_button_cancel, myClickListener)
            // создаем диалог
            return adb.create()
        }
        return super.onCreateDialog(id)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("DMS", "TOOLBAR CREATE #1")
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        ToolbarUtils.setupActionBar(this, pager.currentItem)

        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSideMenu(){
        sideMenuFragment = supportFragmentManager.findFragmentById(R.id.sideMenuFragment) as SideMenuFragment
        sideMenuFragment.syncWithDrawer(closeDrawerAction = {
            binding.drawerLayout.closeDrawers()
        }, selectMenuItemAction = { menuItem: SideMenuItem ->
            //viewModel.selectMenuItem(menuItem)
        })

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                //sideMenuFragment.startBgrVideo()
            }

            override fun onDrawerClosed(drawerView: View) {
                //sideMenuFragment.stopBgrVideo()
            }
        })
    }




    private inner class MyFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val resFragment: Fragment

            if (position == 1)
                resFragment = CreditPageFragment.newInstance(position)
            else if (position == 2)
                resFragment = FlatPageFragment.newInstance(position)
            else
                resFragment = TaskPageFragment.newInstance(position)

            return resFragment
        }

        override fun getCount(): Int {
            return PAGE_COUNT
        }

        override fun getPageTitle(position: Int): CharSequence {
            var title = "title"
            when (position) {
                0 -> title = "Задачи"
                1 -> title = "Кредиты"
                2 -> title = "Квартплата"
            }

            return title
        }

    }

    companion object {

        internal val TAG = "myLogs"
        internal val PAGE_COUNT = 3
    }

}
