package uz.alif.tech.todo.ui.finished

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uz.alif.tech.todo.database.AppDatabase
import uz.alif.tech.todo.database.TodoEntity
import uz.alif.tech.todo.repo.FinishRepositoryImpl
import uz.alif.tech.todo.repo.IFinishRepository

interface IFinishedViewModel {
    val liveFinishEntities: LiveData<List<TodoEntity>>
    fun loadAllFinished()
    fun toFeature(id: Long)
    fun delete(id: Long)
}

class FinishedViewModel(
    application: Application
) : AndroidViewModel(application),
    IFinishedViewModel {

    private val appDatabase = AppDatabase.invoke(application.applicationContext)
    private val finishRepository: IFinishRepository =
        FinishRepositoryImpl(appDatabase.getTodoDao())
    override val liveFinishEntities = finishRepository.liveFinishEntities

    init {
        loadAllFinished()
    }

    override fun loadAllFinished() {
        finishRepository.loadAllFinished()
    }

    override fun toFeature(id: Long) {
        finishRepository.toFeature(id)
    }

    override fun delete(id: Long) {
        finishRepository.delete(id)
    }

    override fun onCleared() {
        super.onCleared()
        finishRepository.cancel()
    }
}