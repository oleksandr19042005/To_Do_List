package todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.model.ActionListEntity
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.repository.ActionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActionViewModel(private val repository: ActionRepository) : ViewModel() {
    val allActions: StateFlow<List<ActionListEntity>> = repository.getAllActions().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun addTask(action: ActionListEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(action)
        }
    }
    fun update(action: ActionListEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(action)
        }
    }
    fun delete(action: ActionListEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(action)
        }
    }

}



