package com.lts.dirclean.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dirs")
data class DirItem(

    @PrimaryKey @ColumnInfo(name = "id")
    val id : String,

    @ColumnInfo(name = "file_name")
    val name : String,

    @ColumnInfo(name = "file_path")
    val path : String,

    @ColumnInfo(name = "file_aliasName")
    val aliasName : String
) {

    override fun toString(): String {
        return "DirItem(name='$name')"
    }
}