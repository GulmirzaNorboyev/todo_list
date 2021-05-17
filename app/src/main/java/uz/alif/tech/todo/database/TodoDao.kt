package uz.alif.tech.todo.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_list")
    fun loadAll(): Flowable<List<TodoEntity>>

    @Query("SELECT * FROM todo_list WHERE status = :status")
    fun loadAllProcess(status: Int = 1): Flowable<List<TodoEntity>>

    @Query("SELECT * FROM todo_list WHERE status = :status")
    fun loadAllFinished(status: Int = 2): Flowable<List<TodoEntity>>

    @Query("SELECT COUNT() FROM todo_list")
    fun countTodoList(): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todoEntity: TodoEntity): Single<Long>

    @Update
    fun updateTodo(todoEntity: TodoEntity): Completable

    @Query("DELETE FROM todo_list WHERE id =:basketId")
    fun delete(basketId: Long): Completable

    @Query("DELETE FROM todo_list")
    fun clear(): Completable

    @Query("UPDATE todo_list SET status = :status WHERE id = :id")
    fun finish(id: Long, status: Int = 2): Completable

    @Query("UPDATE todo_list SET status = :status WHERE id = :id")
    fun process(id: Long, status: Int = 1): Completable

    @Query("UPDATE todo_list SET status = :status WHERE id = :id")
    fun feature(id: Long, status: Int = 0): Completable

}
