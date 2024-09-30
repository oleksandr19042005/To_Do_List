package todolist.notations.appandroidtodo.todolistandroid.freeqrapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import todolist.notations.appandroidtodo.todolistandroid.freeqrapp.repository.ActionRepository

class ActionViewModelFactory(private val repository: ActionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}