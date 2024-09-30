package todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions")
data class ActionListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "titleAction")
    val titleAction: String,
    @ColumnInfo(name = "descriptionAction")
    val descriptionAction: String,
    @ColumnInfo(name = "dateCreate")
    val dateCreate: String,
    @ColumnInfo(name = "lastReview")
    val lastReview: String,
    @ColumnInfo(name = "isCompleted")
    var isCompleted: Boolean = false
)
