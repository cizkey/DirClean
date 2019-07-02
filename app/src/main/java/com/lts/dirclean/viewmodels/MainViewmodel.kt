package com.lts.dirclean.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.data.DirItem
import com.lts.dirclean.data.DirRepository
import com.lts.dirclean.workers.CleanWorker
import com.lts.dirclean.workers.FileDatabaseWorker

class MainViewmodel(val dirRepository: DirRepository) : ViewModel() {

    val dirs: LiveData<List<DirItem>> = dirRepository.getDirs()

    fun queryFileItems(context: Context): LiveData<WorkInfo> {

        val build = OneTimeWorkRequestBuilder<FileDatabaseWorker>().build()
        WorkManager.getInstance(context).enqueue(build)


        return WorkManager.getInstance(context).getWorkInfoByIdLiveData(build.id)
    }

    fun deleteFile(context: Context) : LiveData<WorkInfo> {
        val clean = OneTimeWorkRequestBuilder<CleanWorker>().build()

        WorkManager.getInstance(context).enqueue(clean)



        return WorkManager.getInstance(context).getWorkInfoByIdLiveData(clean.id)
    }

}