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


    private val _items = MutableLiveData<MutableList<Item>>()
    val items: LiveData<MutableList<Item>> = _items

    val itemBinding = ItemBinding.of<Item>(BR.itemBean, R.layout.item_text)

    val adapter = MyBindingRecyclerViewAdapter<Item>()

    val diff: DiffUtil.ItemCallback<Item> = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.name == newItem.name
        }
    }

    fun refreshData() {

        launchUI {
            launchFlow {
                MutableList(50) { Item(it, "item $it") }
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
                val maxIndex = _items.value?.size ?: 0
                MutableList(10) {
                    val id = maxIndex + it
                    Item(id, "item $id") }
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