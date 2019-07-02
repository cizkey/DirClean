package com.lts.dirclean.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lts.dirclean.data.FileRepository

class FileViewModelFactory(val fileRepository: FileRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = FileListViewModel(fileRepository) as T
}