package uz.alif.tech.todo.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import uz.alif.tech.todo.database.TodoDao
import uz.alif.tech.todo.database.TodoEntity

interface IProcessRepository {
    val liveProcessEntities: LiveData<List<TodoEntity>>
    fun loadAllProcess()
    fun toFinished(id: Long)
    fun delete(id: Long)
    fun cancel()
}

private const val TAG = "ProcessRepository"

class ProcessRepositoryImpl(
    private val todoDao: TodoDao
) : IProcessRepository {

    private val cd = CompositeDisposable()
    override val liveProcessEntities = MutableLiveData<List<TodoEntity>>()


    override fun loadAllProcess() {
        val disposable = todoDao.loadAllProcess()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveProcessEntities.postValue(it) },
                {
                    liveProcessEntities.postValue(emptyList())
                    Log.e(TAG, "loadAllProcess: ${it.message}")
                }
            )

        cd.add(disposable)
    }

    override fun toFinished(id: Long) {
        val disposable = todoDao
            .finish(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Log.i(TAG, "toFinished: ") },
                { Log.e(TAG, "toFinished: ", it) }
            )

        cd.add(disposable)
    }

    override fun delete(id: Long) {
        val disposable = todoDao
            .delete(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Log.i(TAG, "delete: ") },
                { Log.e(TAG, "delete: ", it) }
            )

        cd.add(disposable)
    }

    override fun cancel() {
        cd.dispose()
    }
}