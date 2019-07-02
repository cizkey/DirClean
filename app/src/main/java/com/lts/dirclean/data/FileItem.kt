package com.lts.dirclean.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "fileItems")
data class FileItem (
    @PrimaryKey @ColumnInfo(name = "id")
    val id : String,


    val fileName: String,
    val filePath: String,
    val fileType: String,
    val duration: Int,
    val fileSize: Long,
    val videoCover : ByteArray,
    val lastModified : Long,

    @ColumnInfo(name = "parent_name")
    val parentName: String



){

    @Ignore
    var dateTitle: String = ""
    @Ignore
    var isGroup : Boolean = false



    override fun toString() = fileName


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileItem

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}