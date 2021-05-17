package uz.alif.tech.todo.ui.process

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_process.*
import uz.alif.tech.todo.R
import uz.alif.tech.todo.common.OnItemClickListener
import uz.alif.tech.todo.common.SwipeHelper
import uz.alif.tech.todo.common.lazyFast
import uz.alif.tech.todo.database.TodoEntity
import uz.alif.tech.todo.ui.add_edit.*
import uz.alif.tech.todo.ui.home.adapter.TodoListAdapter

class ProcessFragment : Fragment(R.layout.fragment_process),
    OnItemClickListener<TodoEntity>,
    Observer<List<TodoEntity>> {

    private val viewModel: IProcessViewModel by activityViewModels<ProcessViewModel>()
    private val adapter by lazyFast { TodoListAdapter(this) }
    private val dividerItemDecoration by lazyFast {
        DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
    }
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.liveProcessTodoList.observe(viewLifecycleOwner, this)
    }

    private fun setupViews() {
        recycler_process.adapter = adapter
        recycler_process.addItemDecoration(dividerItemDecoration)
        object : SwipeHelper(requireContext(), recycler_process) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>
            ) {
                underlayButtons.add(UnderlayButton(
                    requireContext(),
                    R.drawable.ic_baseline_delete,
                    Color.parseColor("#FFFF395F")
                ) {
                    val todo = adapter.getItem(it)
                    viewModel.delete(todo.id)
                })

                underlayButtons.add(UnderlayButton(
                    requireContext(),
                    R.drawable.ic_round_task_alt,
                    Color.parseColor("#FF2196F3")
                ) {
                    val todo = adapter.getItem(it)
                    viewModel.toFinished(todo.id)
                    Toast.makeText(
                        requireContext(),
                        R.string.changed_to_feature,
                        Toast.LENGTH_SHORT
                    ).show()
                    adapter.notifyItemChanged(it)
                })
            }
        }
    }

    override fun onItemClicked(position: Int, item: TodoEntity) {
        val args = bundleOf(
            Pair(KEY_ADD_MODE, false),
            Pair(KEY_TODO_ID, item.id),
            Pair(KEY_TITLE, item.title),
            Pair(KEY_STATUS, item.status),
            Pair(KEY_TIME, item.time)
        )
        navController.navigate(R.id.nav_add_edit_todo, args)
    }

    override fun onChanged(todos: List<TodoEntity>) {
        adapter.submitList(todos)
    }
}