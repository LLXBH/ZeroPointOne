package llxbh.zeropointone.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import llxbh.zeropointone.api.TaskDao
import llxbh.zeropointone.data.model.Task

/**
 * Room：数据库
 */
@Database(entities = [Task::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        // 数据库的版本从 1 升级到 2 需要执行的方法
        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // 通过 alter 语句往表格最后一列添加
                database.execSQL("alter table Task " +
                        "add isDelete integer not null " +
                        "default 0")
            }

        }
    }

}
