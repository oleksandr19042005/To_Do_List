package todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ActionListEntity::class], version = 1)
abstract class DataBaseInstance : RoomDatabase() {
    abstract fun ActionDao(): DataBaseDao

    companion object {
        @Volatile
        private var INSTANCE: DataBaseInstance? = null

        fun getDatabase(context: android.content.Context): DataBaseInstance {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBaseInstance::class.java,
                    "actions_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
