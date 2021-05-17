package uz.alif.tech.todo.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import uz.alif.tech.todo.common.SingleLiveEvent
import uz.alif.tech.todo.database.TodoDao
import uz.alif.tech.todo.database.TodoEntity

interface ITodoRepository {
    val liveEntities: LiveData<List<TodoEntity>>
    val liveAdded: LiveData<Boolean>
    val liveFinished: LiveData<Boolean>
    val liveProcess: LiveData<Boolean>
    val liveFeature: LiveData<Boolean>
    val liveDeleted: LiveData<Boolean>
    fun loadAll()
    fun add(title: String, time: Long, status: Int)
    fun toFinished(id: Long)
    fun toProcess(id: Long)
    fun toFeature(id: Long)
    fun delete(id: Long)
    fun cancel()
    fun edit(id: Long, title: String, time: Long, status: Int)
}

class TodoRepositoryImpl(
    private val todoDao: TodoDao
) : ITodoRepository {

    private val cd = CompositeDisposable()
    override val liveEntities = MutableLiveData<List<TodoEntity>>()
    override val liveAdded = SingleLiveEvent<Boolean>()
    override val liveFinished = SingleLiveEvent<Boolean>()
    override val liveProcess = SingleLiveEvent<Boolean>()
    override val liveFeature = SingleLiveEvent<Boolean>()
    override val liveDeleted = SingleLiveEvent<Boolean>()

    override fun loadAll() {
        val disposable = todoDao.loadAll()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveEntities.postValue(it) },
                { liveEntities.postValue(emptyList()) }
            )

        cd.add(disposable)
    }

    override fun add(title: String, time: Long, status: Int) {
        val todoEntity = TodoEntity(
            id = 0L,
            title = title,
            time = time,
            status = status
        )
        val disposable = todoDao.insertTodo(todoEntity)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveAdded.postValue(true) },
                { liveAdded.postValue(false) }
            )

        cd.add(disposable)
    }

    override fun toFinished(id: Long) {
        val disposable = todoDao
            .finish(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveFinished.postValue(true) },
                { liveFinished.postValue(true) }
            )

        cd.add(disposable)
    }

    override fun toProcess(id: Long) {
        val disposable = todoDao
            .process(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveFinished.postValue(true) },
                { liveFinished.postValue(true) }
            )

        cd.add(disposable)
    }

    override fun toFeature(id: Long) {
        val disposable = todoDao
            .feature(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveFeature.postValue(true) },
                { liveFeature.postValue(true) }
            )

        cd.add(disposable)
    }

    override fun delete(id: Long) {
        val disposable = todoDao
            .delete(id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveDeleted.postValue(true) },
                { liveDeleted.postValue(true) }
            )

        cd.add(disposable)
    }

    override fun cancel() {
        cd.dispose()
    }

    override fun edit(id: Long, title: String, time: Long, status: Int) {
        val todoEntity = TodoEntity(
            id = id,
            title = title,
            time = time,
            status = status
        )
        val disposable = todoDao.updateTodo(todoEntity)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { liveAdded.postValue(true) },
                { liveAdded.postValue(false) }
            )

        cd.add(disposable)
    }
}