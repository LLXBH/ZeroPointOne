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
    entities = [Task::class, TaskCycle::class],
    version = 6
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun taskCycleDao(): TaskCycleDao

    companion object {
        // 数据库的版本从 1 升级到 2 需要执行的方法
        // 为 “Task” 添加一个 “isDelete” 字段，用于判断用户是否删除了；
        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // 通过 alter 语句往表格最后一列添加
                database.execSQL("alter table Task " +
                        "add isDelete integer not null " +
                        "default 0")
            }

        }

        // 添加新的表（TaskCycle）；
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

        // 为 “TaskCycle” 添加 “remarks” 字段；
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table TaskCycle " +
                        "add remarks text not null " +
                        "default 0")
            }

        }

        // 删除 “TaskCycle” ，重新创建一个；
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

        // 将 “Task” 的 “checks” 改为不可为 null ；
        val MIGRATION_5_6 = object : Migration(5, 6) {

            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建临时表，包括非空约束
                database.execSQL("""CREATE TABLE IF NOT EXISTS Task_temp (
                    id INTEGER PRIMARY KEY NOT NULL,
                    state INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    checks TEXT NOT NULL,
                    updateTimes INTEGER NOT NULL,
                    startTimes INTEGER NOT NULL,
                    endTimes INTEGER NOT NULL,
                    addTimeDay INTEGER NOT NULL,
                    isDelete INTEGER NOT NULL
                )""")

                // 将旧表数据迁移到临时表
                database.execSQL("""
                    INSERT INTO Task_temp(id, state, title, content, checks, updateTimes, startTimes, endTimes, addTimeDay, isDelete)
                    SELECT id, state, title, content, checks, updateTimes, startTimes, endTimes, addTimeDay, isDelete
                    FROM Task
                """)

                // 删除旧表
                database.execSQL("DROP TABLE Task")

                // 重命名临时表为原表名
                database.execSQL("ALTER TABLE Task_temp RENAME TO Task")
            }

        }

        val appDatabase = Room.databaseBuilder(
            appContext, AppDatabase::class.java,
            "database-ZeroPointOne"
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6
        ).build()
    }

}
