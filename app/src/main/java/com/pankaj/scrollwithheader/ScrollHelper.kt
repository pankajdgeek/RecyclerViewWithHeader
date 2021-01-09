package com.pankaj.scrollwithheader

import android.util.Log
import android.widget.AbsListView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ScrollHelper {
    private val TAG = this.javaClass.name
    private lateinit var headerMap: TreeMap<Int, Int>
    private var headerFindingJob: Job? = null
    private val isTabClicked = AtomicBoolean(false)
    private val _headerUpdate = MutableLiveData<Int>()
    private var recyclerView: WeakReference<RecyclerView>? = null
    private var enableLog: Boolean = false
    val headerUpdate: LiveData<Int> = _headerUpdate
    private var dataSize: Int? = null
        get() {
            if (field == null)
                throw Throwable("Size of list is not provide")
            else return field!!
        }

    companion object {
        fun getInstance(
            recyclerView: RecyclerView,
            dataSize: Int,
            headerMap: TreeMap<Int, Int>,
            enableLog: Boolean = false
        ): ScrollHelper {
            return ScrollHelper().apply {
                this.recyclerView = WeakReference(recyclerView)
                this.dataSize = dataSize
                this.enableLog = enableLog
                this.headerMap = headerMap
            }

        }
    }


    fun updateDataSize(size: Int) {
        dataSize = size
    }

    fun getHeaderMap(): TreeMap<Int, Int> {
        return headerMap
    }


    private fun updateCategoryHeader(position: Int) {
        headerFindingJob?.takeIf { it.isActive }?.apply {
            cancel()
        }
        headerFindingJob = CoroutineScope(Dispatchers.IO).launch {
            val headPosition = findHeader(position)
            headerFindingJob?.takeIf { headerFindingJob!!.isActive }?.apply {
                if (headerUpdate.hasActiveObservers())
                    _headerUpdate.postValue(headPosition?.value)
                else
                    throw  Throwable("No Active obsever for headerUpdate. Observe it for updates in header")
            }
        }
    }

    fun scrollStateUpdate(newState: Int) {
        when (newState) {
            AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                if (isTabClicked.get())
                    isTabClicked.set(false)
            }
        }
    }

    fun onScrolled() {
        val head =
            (recyclerView?.get()?.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (!isTabClicked.get())
            updateCategoryHeader(head)
    }

    fun onHeaderSelection(tab: TabLayout.Tab?) {
        var position = 0
        for ((key, value) in headerMap) {
            if (tab != null && value == tab.position) {
                position = key
                break
            }
        }
        isTabClicked.set(true)
        val top =
            (recyclerView?.get()?.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        val bottom =
            (recyclerView?.get()?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        val count = bottom - top
        if (bottom < position || (bottom > position && top < position))
            position = (position + count)
        else
            position = position - 1
        if (position > dataSize!! - 1) {
            position = dataSize!! - 1
        }
        if (bottom >= position)
            isTabClicked.set(false)

        printLog(
            "scrollToPosition: count $count top $top bottom $bottom positon $position final $position"
        )
        (recyclerView?.get()?.layoutManager as LinearLayoutManager).scrollToPosition(position)
    }

    private fun findHeader(index: Int): Map.Entry<Int, Int>? {
        val result = headerMap.floorEntry(index)
        printLog("findHeader: $index  ${result?.key}  ${result?.value}")
        return result
    }

    private fun printLog(log: String) {
        if (enableLog)
            Log.d(TAG, log)
    }
}