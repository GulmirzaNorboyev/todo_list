package uz.alif.tech.todo.ui.home.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_todo.view.*
import uz.alif.tech.todo.R
import uz.alif.tech.todo.common.*
import uz.alif.tech.todo.database.TodoEntity
import java.text.SimpleDateFormat
import java.util.*

class TodoListAdapter(
    private val clickListener: OnItemClickListener<TodoEntity>
) : RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, TodoDiffer)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = parent.inflater(R.layout.item_todo)
        return TodoViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) =
        holder.bind(asyncListDiffer.currentList[position])

    override fun onBindViewHolder(
        holder: TodoViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            payloads.isEmpty() -> super.onBindViewHolder(holder, position, payloads)
            payloads[0] is TodoPayload ->
                holder.changeStatus(asyncListDiffer.currentList[position])
        }
    }

    override fun getItemCount() = asyncListDiffer.currentList.size

    fun submitList(list: List<TodoEntity>) {
        asyncListDiffer.submitList(list)
    }

    fun getItem(position: Int): TodoEntity = asyncListDiffer.currentList[position]

    class TodoViewHolder(
        override val containerView: View,
        private val clickListener: OnItemClickListener<TodoEntity>
    ) : RecyclerView.ViewHolder(containerView),
        LayoutContainer, View.OnClickListener {

        private var todoEntity: TodoEntity? = null
        private val iconArray = intArrayOf(
            R.drawable.ic_twotone_panorama_fish_eye,
            R.drawable.ic_outline_timelapse,
            R.drawable.ic_baseline_check_circle
        )
        private val sdf = SimpleDateFormat("HH:mm\nd MMMM", Locale.getDefault())

        fun bind(todoEntity: TodoEntity) {
            this.todoEntity = todoEntity
            containerView.setOnClickListener(this)
            containerView.iv_status.setImageResource(iconArray[todoEntity.status])
            setTitleStatus(todoEntity)
            containerView.tv_time.text = sdf.format(todoEntity.time)
        }

        private fun setTitleStatus(todoEntity: TodoEntity) =
            when (todoEntity.status) {
                0 -> containerView.tv_title.normalFlagText(todoEntity.title)
                1 -> containerView.tv_title.underLineFlagText(todoEntity.title)
                else -> containerView.tv_title.strikeFlagText(todoEntity.title)
            }

        override fun onClick(v: View) {
            clickListener.onItemClicked(adapterPosition, todoEntity!!)
        }

        fun changeStatus(todo: TodoEntity) {
            containerView.iv_status.setImageResource(iconArray[todo.status])
            setTitleStatus(todo)
        }
    }

    object TodoDiffer : DiffUtil.ItemCallback<TodoEntity>() {
        override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity) =
            oldItem == newItem

        override fun getChangePayload(oldItem: TodoEntity, newItem: TodoEntity): Any? {
            return TodoPayload
        }
    }

    object TodoPayload
}