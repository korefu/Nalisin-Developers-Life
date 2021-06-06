package com.korefu.developerslife

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception
import java.util.*


class EntryViewModel: ViewModel() {
    private val developersLifeService = RetrofitClient.service
    val entryList = LinkedList<Entry>()
    var pos = MutableLiveData(0)
    var lastPage = 0
    val entriesNetworkState = MutableLiveData<String>()
    val imageNetworkState = MutableLiveData<String>()

    suspend fun loadEntries(index: Int) {
        entriesNetworkState.value = "loading"
        try {
            val response = developersLifeService.getEntries(indexToString(index), lastPage)
            if (response.body() != null) {
                entryList.addAll(response.body()!!.result)
                lastPage += 1
                entriesNetworkState.value = "success"
            } else {
                entriesNetworkState.value = "error"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun indexToString(index: Int): String = when(index) {
        TOP_PAGE_INDEX -> "top"
        HOT_PAGE_INDEX -> "hot"
        LATEST_PAGE_INDEX -> "latest"
        else -> "top"
    }
}