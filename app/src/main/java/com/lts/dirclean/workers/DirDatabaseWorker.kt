package com.lts.dirclean.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.data.AppDatabase
import com.lts.dirclean.data.DirItem
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class DirDatabaseWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams){
    private val list : ArrayList<DirItem>

    private val TAG by lazy { DirDatabaseWorker::class.java.simpleName }

    init {
        list = ArrayList<DirItem>()
    }

    companion object {
        private val map = ArrayList<String>()
        init {
            map.add("emoji")
            map.add("favorite")
            map.add("image")
            map.add("image2")
            map.add("openapi")
            map.add("sns")
            map.add("video")
            map.add("voice2")
            map.add("crash")
            map.add("Download")
            map.add("sns_ad_landingpages")
            map.add("WeiXin")
            map.add("bizmsg")
            map.add("xlog")
        }

    }

    override suspend fun doWork(): Result = coroutineScope {


        val file = File(Constant.PATH).absoluteFile


        if (file.exists() && file.isDirectory) {

            for (listFile in file.listFiles()) {


                if (listFile.isDirectory && listFile.name.trim().length == 32) {
                    for (userFile in listFile.listFiles()) {
                        val name  = userFile.name
                        val path = userFile.absolutePath
                        val id : String = UUID.randomUUID().toString()
                        val aliasName = getUserAliasName(name)

                        if (map.contains(name)) {
                            val dirItem = DirItem(id = id, name = name, path = path, aliasName = aliasName)
                            list.add(dirItem)
                        }
                    }

                } else {
                    val name = listFile.name
                    val path = listFile.absolutePath
                    val id : String = UUID.randomUUID().toString()
                    val aliasName = getUserAliasName(name)

                    if (map.contains(name)) {
                        val dirItem = DirItem(id = id, name = name, path = path, aliasName = aliasName)
                        list.add(dirItem)
                    }
                }
            }

            val dirDao = AppDatabase.getInstance(applicationContext).dirDao()
            dirDao.save(list)


            Result.success()

        } else {
            Result.failure()
        }


    }


    private fun getUserAliasName(name: String): String {
        if (name.equals("image2")) {
            return "聊天图片"
        }

        if (name.equals("sns")) {
            return "朋友圈图片"
        }

        if (name.equals("video")) {
            return "小视频"
        }

        if (name.equals("voice2")) {
            return "语音文件"
        }

        if (name.equals("favorite")) {
            return "收藏"
        }

        if (name.equals("image")) {
            return "公众号"
        }

        if (name.equals("openapi")) {
            return "第三方库"
        }

        if (name.equals("crash")) {
            return "奔溃日志"
        }

        if (name.equals("Download")) {
            return "微信下载"
        }

        if (name.equals("sns_ad_landingpages")) {
            return "朋友圈广告"
        }

        if (name.equals("WeiXin")) {
            return "微信相机"
        }

        return name

    }
}