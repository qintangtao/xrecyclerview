package me.tang.xrecyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import me.tang.mvvm.base.BaseViewModel
import me.tang.mvvm.event.Message
import me.tang.mvvm.network.ExceptionHandle
import me.tang.mvvm.network.RESULT
import me.tang.mvvm.BR
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion

import me.tatarka.bindingcollectionadapter2.ItemBinding

class MainViewModel  : BaseViewModel() {


    private val _items = MutableLiveData<MutableList<String>>()
    val items: LiveData<MutableList<String>> = _items

    val itemBinding = ItemBinding.of<String>(BR.itemBean, R.layout.item_text)

    val diff: DiffUtil.ItemCallback<String> = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    fun refreshData() {

        launchUI {
            launchFlow {
                MutableList(50) { "item $it" }
            }
            .flowOn(Dispatchers.IO)
            .catch {
                val e = ExceptionHandle.handleException(it)
                callError(Message(e.code, e.msg))
            }
            .onCompletion {
                callComplete()
            }
            .collect {
                _items.value = it
                callResult(RESULT.SUCCESS.code)
            }
        }
    }

    fun loadMoreData() {

        launchUI {
            launchFlow {
                delay(1000)
                MutableList(10) { "item $it" }
            }
                .flowOn(Dispatchers.IO)
                .catch {
                    val e = ExceptionHandle.handleException(it)
                    callError(Message(e.code, e.msg))
                }
                .onCompletion {
                    callComplete()
                }
                .collect {
                    val data = _items.value
                    data?.addAll(it)
                    _items.value = data
                    callResult(RESULT.SUCCESS.code)
                }
        }
    }
}