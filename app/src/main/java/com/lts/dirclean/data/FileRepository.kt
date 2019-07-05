package com.lts.dirclean.data



class FileRepository private constructor(val fileDao: FileDao){


    fun getFiles(parentName : String,startDate : Long, endDate: Long) = fileDao.getFileItemByParent(parentName,startDate,endDate)


    fun deleteFiles(fileItem: FileItem) {
        fileDao.deleteFileById(fileItem)
    }


    companion object {
        @Volatile private var instance: FileRepository? = null

        fun getInstance(fileDao: FileDao) =
            instance ?: synchronized(this) {
                instance ?: FileRepository(fileDao).also { instance = it }
            }

    }
}