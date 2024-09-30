package todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DataBaseDao {
    @Insert
    fun insert(task: ActionListEntity): Long

    @Update
    fun update(task: ActionListEntity): Int

    @Delete
    fun delete(task: ActionListEntity): Int

    @Query("SELECT * FROM actions")
    fun getAllActions(): Flow<List<ActionListEntity>>
}

