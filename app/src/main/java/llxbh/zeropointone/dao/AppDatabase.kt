package llxbh.zeropointone.dao

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room：数据库
 */
@Database(entities = [Task::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

}
