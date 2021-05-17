package uz.alif.tech.todo.ui.finished

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
import kotlinx.android.synthetic.main.fragment_finished.*
import uz.alif.tech.todo.R
import uz.alif.tech.todo.common.OnItemClickListener
import uz.alif.tech.todo.common.SwipeHelper
import uz.alif.tech.todo.common.lazyFast
import uz.alif.tech.todo.database.TodoEntity
import uz.alif.tech.todo.ui.add_edit.*
import uz.alif.tech.todo.ui.home.adapter.TodoListAdapter

class FinishedFragment : Fragment(R.layout.fragment_finished),
    OnItemClickListener<TodoEntity>,
    Observer<List<TodoEntity>> {

    private val finishedViewModel: IFinishedViewModel by activityViewModels<FinishedViewModel>()
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
        finishedViewModel.liveFinishEntities.observe(viewLifecycleOwner, this)
    }

    private fun setupViews() {
        recycler_finished.adapter = adapter
        recycler_finished.addItemDecoration(dividerItemDecoration)
        object : SwipeHelper(requireContext(), recycler_finished) {
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
                    finishedViewModel.delete(todo.id)
                    Toast.makeText(
                        requireContext(),
                        R.string.todo_deleted,
                        Toast.LENGTH_SHORT
                    ).show()
                })

                underlayButtons.add(UnderlayButton(
                    requireContext(),
                    R.drawable.ic_baseline_radio_button,
                    Color.parseColor("#FF2196F3")
                ) {
                    val todo = adapter.getItem(it)
                    finishedViewModel.toFeature(todo.id)
                    Toast.makeText(
                        requireContext(),
                        R.string.changed_to_finished,
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