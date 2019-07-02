package com.lts.dirclean.utils

import android.content.Context
import com.lts.dirclean.data.AppDatabase
import com.lts.dirclean.data.DirRepository
import com.lts.dirclean.data.FileRepository
import com.lts.dirclean.viewmodels.DirViewModelFactory
import com.lts.dirclean.viewmodels.FileViewModelFactory

object InjectorUtils {

    private fun getDirRepository(context: Context) : DirRepository{
        return DirRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).dirDao()

        )
    }

    private fun getFileRepository(context: Context) : FileRepository {
        return FileRepository.getInstance(
            AppDatabase.getInstance(context).fileDao()
        )
    }

    fun provideMianViewModelFactory(context: Context) : DirViewModelFactory {
        val dirRepository = getDirRepository(context)

        return DirViewModelFactory(dirRepository)
    }

    fun provideFileListViewModelFactory(context: Context) : FileViewModelFactory {
        val repository = getFileRepository(context)

        return FileViewModelFactory(repository)
    }

}