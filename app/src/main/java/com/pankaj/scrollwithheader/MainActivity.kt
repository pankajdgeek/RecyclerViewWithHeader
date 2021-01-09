package com.pankaj.scrollwithheader

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import java.util.*


class MainActivity : AppCompatActivity() {

    val headers = arrayListOf<CustomData>()
    lateinit var tabLayout: TabLayout
    lateinit var recyclerView: RecyclerView
    lateinit var scrollHelper: ScrollHelper
    val headerMap = TreeMap<Int, Int>()
    private fun getListWithHeader(): ArrayList<CustomData> {
        val customDataList = arrayListOf<CustomData>()
        for (x in 0..10) {
            val data = CustomData("head", "heading $x")
            customDataList.add(data)
            headers.add(data)
            headerMap.put(customDataList.size, x)
            for (y in 0..5) {
                customDataList.add(CustomData("item", "item $x $y"))
            }
        }
        return customDataList
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_main)
        val array = getListWithHeader()
        val adapter = Adapter(array)
        recyclerView = findViewById<RecyclerView>(R.id.rv_main)
        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        for (head in headers) {
            tabLayout.addTab(tabLayout.newTab().setText(head.value))
        }
        scrollHelper = ScrollHelper.getInstance(recyclerView, array.size, headerMap, true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        scrollHelper.headerUpdate.observe(this, {
            if (it != null)
                tabLayout.setScrollPosition(
                    it,
                    0f,
                    true
                )
        })
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollHelper.scrollStateUpdate(newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollHelper.onScrolled()
            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabSelected: ")
                scrollHelper.onHeaderSelection(tab)

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }


    companion object {
        const val TAG = "MainActivity"
    }


}