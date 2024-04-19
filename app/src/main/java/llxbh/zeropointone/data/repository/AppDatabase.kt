package llxbh.zeropointone.data.repository

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import llxbh.zeropointone.api.TaskApi
import llxbh.zeropointone.api.TaskCycleDao
import llxbh.zeropointone.api.TaskDao
import llxbh.zeropointone.app.appContext
import llxbh.zeropointone.data.model.Task
import llxbh.zeropointone.data.model.TaskCycle

/**
 * Room：数据库
 */
@Database(
    entities = arrayOf(Task::class, TaskCycle::class),
    version = 5
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun taskCycleDao(): TaskCycleDao

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
        val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table TaskCycle (" +
                        "id integer primary key autoincrement not null, " +
                        "state integer not null, " +
                        "title text not null, " +
                        "content text not null, " +
                        "checks text not null, " +
                        "updateTimes integer not null, " +
                        "startTimes integer not null, " +
                        "endTimes integer not null, " +
                        "addTimeDay integer not null, " +
                        "isDelete integer not null" +
                    ")"
                )
            }

        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table TaskCycle " +
                        "add remarks text not null " +
                        "default 0")
            }

        }
        val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("drop table if exists TaskCycle")
                database.execSQL("create table TaskCycle (" +
                        "id integer primary key autoincrement not null, " +
                        "state integer not null, " +
                        "title text not null, " +
                        "content text not null, " +
                        "checks text not null, " +
                        "updateTimes integer not null, " +
                        "startTimes integer not null, " +
                        "endTimes integer not null, " +
                        "finishedTimes text not null, " +
                        "needCompleteNum integer not null, " +
                        "addTimeDay integer not null, " +
                        "isDelete integer not null" +
                    ")"
                )
            }

        }

        val appDatabase = Room.databaseBuilder(
            appContext, AppDatabase::class.java,
            "database-ZeroPointOne"
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5
        ).build()
    }

}
