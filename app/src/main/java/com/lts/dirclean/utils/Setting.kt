package com.lts.dirclean.utils

import android.content.Context
import android.content.SharedPreferences
import com.lts.dirclean.constants.Constant

class Setting private constructor(private val sharedPreferences: SharedPreferences){



    companion object {

        private var instance : Setting? = null

        fun getInstances(context: Context) : Setting {
            return instance ?: synchronized(this) {
                instance ?: Setting(
                    context.getSharedPreferences(Constant.SHARE_PREFERENCES, Context.MODE_PRIVATE)
                ).also { instance = it }
            }
        }
    }


    /**
     * 设置排序方式
     * @param type 1: 按时间排序 2: 按文件大小排序
     */
    fun setSoryBy(type : Int){
        sharedPreferences.edit().putInt(Constant.SORT_BY,type).apply()
    }

    /**
     * 获取设置的排序方式
     * @return 1 : 按时间排序 2 按文件大小排序
     */
    fun getBySort() : Int {
        return sharedPreferences.getInt(Constant.SORT_BY,0)
    }

    /**
     * 设置某哥文件的最后修改时间
     * @param dirName 文件名
     * @param lastModified 最后修改时间
     */
    fun setDirLastModified(dirName : String ,lastModified : Long) {
        sharedPreferences.edit().putLong(dirName,lastModified).apply()
    }

    /**
     * 根据文件名获取文件最后修改时间
     * @return lastModified
     */
    fun getDirLastModified(dirName: String) : Long {
        return sharedPreferences.getLong(dirName,0)
    }


    fun setFirstLoading(isFirst: Boolean) {
        sharedPreferences.edit().putBoolean(Constant.IS_FIRST_LOADING,isFirst).apply()
    }

    fun getFirstLoading() : Boolean {
        return sharedPreferences.getBoolean(Constant.IS_FIRST_LOADING,true)
    }
}