package uz.alif.tech.todo.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import uz.alif.tech.todo.database.TodoDao
import uz.alif.tech.todo.database.TodoEntity

interface IFinishRepository {
    val liveFinishEntities: LiveData<List<TodoEntity>>
    fun loadAllFinished()
    fun toFeature(id: Long)
    fun delete(id: Long)
    fun cancel()
}

private const val TAG = "FinishRepository"

class FinishRepositoryImpl(
    private val todoDao: TodoDao
) : IFinishRepository {

    private val cd = CompositeDisposable()
    override val liveFinishEntities = MutableLiveData<List<TodoEntity>>()

    override fun loadAllFinished() {
        val disposable = todoDao.loadAllFinished()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveFinishEntities.postValue(it) },
                {
                    liveFinishEntities.postValue(emptyList())
                    Log.e(TAG, "loadAllProcess: ${it.message}")
                }
            )

        cd.add(disposable)
    }

    override fun toFeature(id: Long) {
        val disposable = todoDao
            .feature(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { Log.i(TAG, "toFeature: ") },
                { Log.e(TAG, "toFeature: ", it) }
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