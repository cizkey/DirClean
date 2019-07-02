package com.lts.dirclean.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lts.dirclean.data.DirRepository

class DirViewModelFactory(val dirRepository: DirRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = MainViewmodel(dirRepository) as T
}