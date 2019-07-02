package com.lts.dirclean.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FileDao {

    @Insert
    fun save(fileItems : List<FileItem>)


    @Query("SELECT * FROM fileItems WHERE parent_name = :parentName and lastModified > :startDate and lastModified < :endDate " )
    fun getFileItemByParent(parentName : String,startDate : Long ,endDate : Long) : LiveData<List<FileItem>>

    @Query("DELETE FROM fileItems WHERE id = :id")
    fun deleteById(id : String)

    @Query("SELECT * FROM fileItems WHERE parent_name = :parentName")
    fun getCleanFileItems(parentName: String) : List<FileItem>

    @Delete
    fun deleteFileById(fileItem: FileItem)

}