package com.bunk.urbanmobility.view.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bunk.urbanmobility.R
import com.bunk.urbanmobility.api.GitHubDataSource
import com.bunk.urbanmobility.api.entity.RepositoryItem
import com.bunk.urbanmobility.scheduler.ObserveOnScheduler
import com.bunk.urbanmobility.scheduler.SubscribeOnScheduler
import com.bunk.urbanmobility.view.Info
import io.reactivex.disposables.Disposable

const val DEFAULT_STARS = 10000

class RepositoryListViewModel(
    private val gitHubDataSource: GitHubDataSource,
    private val subscribeOnScheduler: SubscribeOnScheduler,
    private val observeOnScheduler: ObserveOnScheduler
) : ViewModel() {

    private var disposable: Disposable? = null

    val liveData = MutableLiveData<List<RepositoryItem>>()
    val infoLiveData = MutableLiveData<Info>()

    fun fetchRepositories() {
        disposable = gitHubDataSource.getRepositories(DEFAULT_STARS)
            .subscribeOn(subscribeOnScheduler.io)
            .observeOn(observeOnScheduler.androidMainThreadScheduler)
            .subscribe(
                {
                    val list = liveData.value ?: emptyList()
                    val mutableList = mutableListOf<RepositoryItem>()

                    mutableList.addAll(list)
                    mutableList.addAll(it)

                    liveData.value = mutableList
                },
                { infoLiveData.value = Info(R.string.could_not_fetch_repositories) }
            )
    }

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
    }
}