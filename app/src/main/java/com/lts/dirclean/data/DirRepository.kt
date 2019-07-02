package com.lts.dirclean.data

class DirRepository private constructor(val dirDao : DirDao){


    fun getDirs() = dirDao.getDirItem()

    companion object {
        @Volatile private var instance: DirRepository? = null

        fun getInstance(dirDao: DirDao) =
            instance ?: synchronized(this) {
                instance ?: DirRepository(dirDao).also { instance = it }
            }
    }
}