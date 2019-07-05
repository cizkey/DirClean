package com.lts.dirclean.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.data.AppDatabase
import kotlinx.coroutines.coroutineScope
import java.io.File

class CleanWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private var sum: Long = 0

    override suspend fun doWork(): Result = coroutineScope {

        val dirDao = AppDatabase.getInstance(applicationContext).dirDao()
        val fileDao = AppDatabase.getInstance(applicationContext).fileDao()

        val allDir = dirDao.getAllDir()

        for (dirItem in allDir) {

            if (dirItem.aliasName.equals("微信相机")) {
                continue
            }

            if (dirItem.aliasName.equals("微信下载")) {
                continue
            }

            val fileItem = fileDao.getCleanFileItems(dirItem.aliasName)
            for (item in fileItem) {
                val file = File(item.filePath)
                file.delete()


                fileDao.deleteById(item.id)

                sum += item.fileSize
            }
        }


        val data = Data.Builder().putLong(Constant.TOTAL_SIZE, sum)
            .putLong(Constant.TOTAL_COUNT, sum)
            .build()


        Result.success(data)
    }
}