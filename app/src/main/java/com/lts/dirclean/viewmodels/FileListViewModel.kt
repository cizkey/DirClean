package com.lts.dirclean.viewmodels

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

import com.lts.dirclean.data.FileItem
import com.lts.dirclean.data.FileRepository


class FileListViewModel(val fileRepository: FileRepository) : ViewModel() {

    fun getFiles(parentName : String,startDate : Long,endDate : Long) : LiveData<List<FileItem>> {

        return fileRepository.getFiles(parentName,startDate,endDate)
    }


 fun deleteFile(fileItem: FileItem) {

        Thread(Runnable {
            fileRepository.deleteFiles(fileItem)
        }).start()

    }

}