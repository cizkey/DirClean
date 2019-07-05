package com.lts.dirclean.viewmodels

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.lts.dirclean.data.FileItem
import com.lts.dirclean.data.FileRepository
import com.lts.dirclean.utils.Setting


class FileListViewModel(val fileRepository: FileRepository) : ViewModel() {

    fun getFiles(context: Context,parentName : String,startDate : Long,endDate : Long) : LiveData<List<FileItem>>? {

        val setting = Setting.getInstances(context)
        if (!setting.getCleaned()) {
            return fileRepository.getFiles(parentName,startDate,endDate)

        }

        return null

    }


 fun deleteFile(fileItem: FileItem) {

        Thread(Runnable {
            fileRepository.deleteFiles(fileItem)
        }).start()

    }

}