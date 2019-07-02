package com.lts.dirclean.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.provider.MediaStore
import android.text.TextUtils
import android.text.TextUtils.substring
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.data.AppDatabase
import com.lts.dirclean.data.FileByTimeSort
import com.lts.dirclean.data.FileItem
import com.lts.dirclean.utils.FileUtil
import com.lts.dirclean.utils.MediaUtil
import com.lts.dirclean.utils.Setting
import kotlinx.coroutines.coroutineScope
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileDatabaseWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val list = ArrayList<FileItem>()
    private var lastFileTime = 0L
    private val TAG: String by lazy { FileDatabaseWorker::class.java.simpleName }
    private var sum : Long = 0
    private var count : Int = 0

    override suspend fun doWork(): Result = coroutineScope {

        val dirDao = AppDatabase.getInstance(applicationContext).dirDao()
        val fileDao = AppDatabase.getInstance(applicationContext).fileDao()

        val allDir = dirDao.getAllDir()
        val setting = Setting.getInstances(applicationContext)
        val map = HashMap<String,Long>()


        if (allDir.isEmpty()) {
            Result.failure()
        } else {
            for (dirItem in allDir) {
                val file = File(dirItem.path)
                //获取上次查询时间
                lastFileTime = setting.getDirLastModified(dirItem.aliasName)

                map.put(dirItem.aliasName,lastFileTime)


                if (file.exists() && file.isDirectory && file.absoluteFile.listFiles().size > 0) {
                    addFileItem(file.listFiles(), dirItem.aliasName,map)
                }

            }

            if (!list.isEmpty()) {

                fileDao.save(list)
            }

            for (entry in map.iterator()) {
                //存入的时候多加200毫秒，防止重复
                setting.setDirLastModified(entry.key,entry.value + 200)
            }


            val data = Data.Builder().putLong(Constant.TOTAL_SIZE, sum)
                .putInt(Constant.TOTAL_COUNT, count)
                .build()
            //通知WorkManager任务完成，并可以携带数据
            Result.success(data)
        }


    }


    /**
     * 递归遍历[parentName]下的所有文件，并且封装成 [FileItem]
     *
     * @param fileList 目录下的子文件
     * @param parentName 要分组的文件名
     */
    private fun addFileItem(fileList: Array<File>, parentName: String , map : HashMap<String,Long>) {


        for (file in fileList) {

            if (file.isDirectory && file.listFiles() != null) {

                addFileItem(file.listFiles(), parentName,map)

            } else {

                if (file.lastModified() < lastFileTime) {
                    continue
                }

                val path = file.absolutePath
                val id = UUID.randomUUID().toString()

                if (TextUtils.isEmpty(path)) {
                    continue
                }

                val lastIndexOf = path.lastIndexOf('.')
                var fileType = ""

                if (lastIndexOf > 0) {
                    fileType = path.substring(lastIndexOf + 1)
                } else {
                    //如果没有后缀，根据流来识别类型
                    fileType = FileUtil.getFileType(path)
                }

                if (file.name.contains(".apk.1")) {
                    val reName = file.path.substring(0, file.path.lastIndexOf("."))
                    file.renameTo(File(reName))

                }

                val name = file.name



                val fileSize = FileUtil.getTotalSizeOfFilesInDir(file)

                sum += fileSize

                var videoCover = ByteArray(0)
                var duration = 0

                if (fileType.equals("mp4")) {
                    videoCover = getByteArray(path)

                    duration = MediaUtil.instances.getVideoDuration(path)

                }

                val fileItem = FileItem(
                    id = id,
                    fileName = name,
                    filePath = path,
                    duration = duration,
                    fileType = fileType,
                    parentName = parentName,
                    fileSize = fileSize / 1024,
                    videoCover = videoCover,
                    lastModified = file.lastModified()

                )

                Log.d(TAG, parentName + "------" + fileType)


                if (file.lastModified() > map.get(parentName)!!) {
                    map.put(parentName,file.lastModified())
                }


                if (!fileType.contains("nomedia")) {

                    count++

                    list.add(fileItem)
                }

            }
        }
    }

    private fun getByteArray(path: String): ByteArray {

        val bitmap = MediaUtil.instances.getVideoThumbail(path, MediaStore.Images.Thumbnails.MICRO_KIND)
        val outputStream = ByteArrayOutputStream()
        if (bitmap != null) {

            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
        }

        return outputStream.toByteArray()

    }


}