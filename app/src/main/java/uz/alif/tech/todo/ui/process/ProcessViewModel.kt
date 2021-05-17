package uz.alif.tech.todo.ui.process

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uz.alif.tech.todo.database.AppDatabase
import uz.alif.tech.todo.database.TodoEntity
import uz.alif.tech.todo.repo.IProcessRepository
import uz.alif.tech.todo.repo.ProcessRepositoryImpl

interface IProcessViewModel {
    val liveProcessTodoList: LiveData<List<TodoEntity>>
    fun loadAllProcess()
    fun toFinished(id: Long)
    fun delete(id: Long)
}

class ProcessViewModel(
    application: Application
) : AndroidViewModel(application),
    IProcessViewModel {

    private val appDatabase = AppDatabase.invoke(application.applicationContext)
    private val todoRepository: IProcessRepository =
        ProcessRepositoryImpl(appDatabase.getTodoDao())
    override val liveProcessTodoList = todoRepository.liveProcessEntities

    init {
        loadAllProcess()
    }

    override fun loadAllProcess() {
        todoRepository.loadAllProcess()
    }

    override fun toFinished(id: Long) {
        todoRepository.toFinished(id)
    }

    override fun delete(id: Long) {
        todoRepository.delete(id)
    }

    override fun onCleared() {
        super.onCleared()
        todoRepository.cancel()
    }
}