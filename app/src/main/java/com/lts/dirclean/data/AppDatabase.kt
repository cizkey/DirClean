package com.lts.dirclean.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lts.dirclean.constants.Constant
import com.lts.dirclean.workers.DirDatabaseWorker
import com.lts.dirclean.workers.FileDatabaseWorker
import java.util.concurrent.TimeUnit


@Database(entities = [DirItem::class , FileItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dirDao(): DirDao
    abstract fun fileDao() : FileDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constant.DIR_DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        //查询分组目录的任务
                        val build = OneTimeWorkRequestBuilder<DirDatabaseWorker>()
                            .build()

                        val constraints = Constraints.Builder()
                            .setRequiresBatteryNotLow(true)
                            .build()

                        val request = PeriodicWorkRequestBuilder<FileDatabaseWorker>(1, TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .build()

                        WorkManager.getInstance(context).enqueue(build)


                    }

                }).build()

        }


    }


}
