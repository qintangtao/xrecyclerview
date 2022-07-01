package me.tang.xrecyclerview

import android.os.Bundle
import me.tang.mvvm.base.BaseActivity
import me.tang.xrecyclerview.databinding.ActivityMainBinding

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>()  {

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.viewModel = viewModel

        mBinding.swipeRefreshLayout.run {
            setOnRefreshListener { viewModel.refreshData() }
        }

        mBinding.recyclerView.run {
            setPullRefreshEnabled(false)
            setLoadingMoreEnabled(true)
            setLoadingMoreProgressStyle(ProgressStyle.Pacman)
            setLoadingListener(object : XRecyclerView.LoadingListener {
                override fun onRefresh() {
                    viewModel.refreshData()
                }
                override fun onLoadMore() {
                    viewModel.loadMoreData()
                }
            })
        }
    }

    override fun initData() {
        viewModel.refreshData()
    }

    override fun onLoadCompleted() {
        super.onLoadCompleted()
        mBinding.swipeRefreshLayout.run {
            if (isRefreshing)
                isRefreshing = false
        }
        mBinding.recyclerView.refreshComplete()
        mBinding.recyclerView.loadMoreComplete()
    }
}