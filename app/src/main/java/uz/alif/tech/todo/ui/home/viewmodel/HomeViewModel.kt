package uz.alif.tech.todo.ui.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uz.alif.tech.todo.database.AppDatabase
import uz.alif.tech.todo.database.TodoEntity
import uz.alif.tech.todo.repo.ITodoRepository
import uz.alif.tech.todo.repo.TodoRepositoryImpl

interface IHomeViewModel {
    val liveTodoList: LiveData<List<TodoEntity>>
    val liveAdded: LiveData<Boolean>
    val liveFinished: LiveData<Boolean>
    val liveProcess: LiveData<Boolean>
    val liveFeature: LiveData<Boolean>
    val liveDeleted: LiveData<Boolean>
    fun loadAll()
    fun add(title: String, time: Long, status: Int)
    fun edit(id: Long, title: String, time: Long, status: Int)
    fun toFinished(id: Long)
    fun toProcess(id: Long)
    fun toFeature(id: Long)
    fun delete(id: Long)
}

class HomeViewModel(
    application: Application
) : AndroidViewModel(application),
    IHomeViewModel {

    private val appDatabase = AppDatabase.invoke(application.applicationContext)
    private val todoRepository: ITodoRepository = TodoRepositoryImpl(appDatabase.getTodoDao())
    override val liveTodoList = todoRepository.liveEntities
    override val liveAdded = todoRepository.liveAdded
    override val liveFinished = todoRepository.liveFinished
    override val liveProcess = todoRepository.liveProcess
    override val liveFeature = todoRepository.liveFeature
    override val liveDeleted = todoRepository.liveDeleted

    init {
        loadAll()
    }

    override fun loadAll() = todoRepository.loadAll()

    override fun add(title: String, time: Long, status: Int) =
        todoRepository.add(title, time, status)

    override fun edit(id: Long, title: String, time: Long, status: Int) =
        todoRepository.edit(id, title, time, status)

    override fun toFinished(id: Long) = todoRepository.toFinished(id)

    override fun toProcess(id: Long) = todoRepository.toProcess(id)

    override fun toFeature(id: Long) = todoRepository.toFeature(id)

    override fun delete(id: Long) = todoRepository.delete(id)

    override fun onCleared() {
        super.onCleared()
        todoRepository.cancel()
    }
}