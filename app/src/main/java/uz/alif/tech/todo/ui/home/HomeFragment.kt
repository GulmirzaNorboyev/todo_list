package uz.alif.tech.todo.ui.home

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
import kotlinx.android.synthetic.main.fragment_home.*
import uz.alif.tech.todo.R
import uz.alif.tech.todo.common.OnItemClickListener
import uz.alif.tech.todo.common.SwipeHelper
import uz.alif.tech.todo.common.lazyFast
import uz.alif.tech.todo.database.TodoEntity
import uz.alif.tech.todo.ui.add_edit.*
import uz.alif.tech.todo.ui.home.adapter.TodoListAdapter
import uz.alif.tech.todo.ui.home.viewmodel.HomeViewModel
import uz.alif.tech.todo.ui.home.viewmodel.IHomeViewModel

class HomeFragment : Fragment(R.layout.fragment_home),
    OnItemClickListener<TodoEntity> {

    private val homeViewModel: IHomeViewModel by activityViewModels<HomeViewModel>()
    private val todoListAdapter by lazyFast { TodoListAdapter(this) }
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

    private val observerTodoList = Observer<List<TodoEntity>>() {
        todoListAdapter.submitList(it)
    }

    private val observerAdded = Observer<Boolean>() {

    }

    private fun setupObservers() {
        homeViewModel.liveTodoList.observe(viewLifecycleOwner, observerTodoList)
//        homeViewModel.liveAdded.observe(viewLifecycleOwner, observerAdded)
    }

    private fun setupViews() {
        recycler_home.adapter = todoListAdapter
        recycler_home.addItemDecoration(dividerItemDecoration)
        object : SwipeHelper(requireContext(), recycler_home) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>
            ) {
                underlayButtons.add(UnderlayButton(
                    requireContext(),
                    R.drawable.ic_baseline_delete,
                    Color.parseColor("#FFFF395F")
                ) {
                    val todo = todoListAdapter.getItem(it)
                    homeViewModel.delete(todo.id)
                    Toast.makeText(
                        requireContext(),
                        R.string.todo_deleted,
                        Toast.LENGTH_SHORT
                    ).show()
                })

                underlayButtons.add(UnderlayButton(
                    requireContext(),
                    R.drawable.ic_baseline_change_status,
                    Color.parseColor("#FF2196F3")
                ) {
                    val todo = todoListAdapter.getItem(it)
                    when (todo.status) {
                        0 -> {
                            homeViewModel.toProcess(todo.id)
                            Toast.makeText(
                                requireContext(),
                                R.string.changed_to_process,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        1 -> {
                            homeViewModel.toFinished(todo.id)
                            Toast.makeText(
                                requireContext(),
                                R.string.changed_to_finished,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            homeViewModel.toFeature(todo.id)
                            Toast.makeText(
                                requireContext(),
                                R.string.changed_to_feature,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    todoListAdapter.notifyItemChanged(it)
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
}