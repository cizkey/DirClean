package com.lts.dirclean.data

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DirDao {

    @Insert
    fun save(dirItems : List<DirItem>)

    @Query("SELECT * FROM dirs")
    fun getDirItem() : LiveData<List<DirItem>>

    @Query("SELECT * FROM dirs")
    fun getAllDir() : List<DirItem>



}