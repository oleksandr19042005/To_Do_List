package todolist.notations.appandroidtodo.todolistandroid.freeqrapp.repository

import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.ActionListEntity
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.DataBaseDao
import kotlinx.coroutines.flow.Flow

class ActionRepository(private val actionDao: DataBaseDao) {
    fun getAllActions(): Flow<List<ActionListEntity>> {
        return actionDao.getAllActions()
    }
    fun insert(action: ActionListEntity) {
        actionDao.insert(action)
    }
    fun update(action: ActionListEntity) {
        actionDao.update(action)
    }
    fun delete(action: ActionListEntity) {
        actionDao.delete(action)
    }
}


