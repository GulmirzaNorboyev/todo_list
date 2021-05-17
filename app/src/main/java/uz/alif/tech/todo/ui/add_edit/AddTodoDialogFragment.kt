package uz.alif.tech.todo.ui.add_edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_add_todo.*
import uz.alif.tech.todo.R
import uz.alif.tech.todo.common.SimpleTextWatcher
import uz.alif.tech.todo.common.lazyFast
import uz.alif.tech.todo.ui.home.viewmodel.HomeViewModel
import uz.alif.tech.todo.ui.home.viewmodel.IHomeViewModel
import java.text.SimpleDateFormat
import java.util.*

const val KEY_ADD_MODE = "KEY_ADD_MODE"
const val KEY_TODO_ID = "KEY_TODO_ID"
const val KEY_TITLE = "KEY_TITLE"
const val KEY_TIME = "KEY_TIME"
const val KEY_STATUS = "KEY_STATUS"

class AddTodoDialogFragment : BottomSheetDialogFragment(),
    TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener,
    View.OnClickListener,
    Observer<Boolean> {

    private var isAddMode = true
    private var title = ""
    private var time = 0L
    private var status = 0
    private var todoId = 0L
    private val cd = CompositeDisposable()
    private val sdDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val sdTime = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val homeViewModel: IHomeViewModel by activityViewModels<HomeViewModel>()
    private val calendar = Calendar.getInstance()
    private val timePickerDialog by lazyFast {
        calendar.timeInMillis = time
        TimePickerDialog(
            requireContext(),
            R.style.ThemeOnverlay_timePicker,
            this,
            calendar.get(Calendar.HOUR),
            calendar[Calendar.MINUTE],
            true
        )
    }
    private val datePickerDialog by lazyFast {
        calendar.timeInMillis = time
        val picker = DatePickerDialog(
            requireContext(),
            R.style.ThemeOnverlay_timePicker,
            this,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
        picker.datePicker.minDate = Date().time
        return@lazyFast picker
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = requireArguments()
        isAddMode = args.getBoolean(KEY_ADD_MODE, true)
        title = args.getString(KEY_TITLE, "")
        time = args.getLong(KEY_TIME, System.currentTimeMillis())
        todoId = args.getLong(KEY_TODO_ID, 0L)
        status = args.getInt(KEY_STATUS, 0)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
            activity?.window?.setDecorFitsSystemWindows(true)
        else
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun getTheme() = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_add_todo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        homeViewModel.liveAdded.observe(viewLifecycleOwner, this)
    }

    private fun setupViews() {
        if (isAddMode) {
            tv_add.setText(R.string.title_add_todo)
            btn_save.setText(R.string.title_add)
        } else {
            tv_add.setText(R.string.title_edit_todo)
            btn_save.setText(R.string.title_edit)
        }
        tie_title.setText(title)
        tv_date.text = sdDate.format(time)
        tv_time.text = sdTime.format(time)
        btn_save.setOnClickListener(this)
        tv_date.setOnClickListener { datePickerDialog.show() }
        tv_time.setOnClickListener { timePickerDialog.show() }
        tie_title.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btn_save.isEnabled = s.isNotEmpty() && s.toString() != title
            }
        })
    }

    override fun onClick(v: View) {
        val title = tie_title.text.toString()
        val time = tv_time.text.toString()
        val date = tv_date.text.toString()
        val sdDateTime = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val selectedDate = sdDateTime.parse("$date $time")!!
        if (isAddMode)
            homeViewModel.add(title, selectedDate.time, status)
        else
            homeViewModel.edit(todoId, title, selectedDate.time, status)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        tv_time.text = sdTime.format(calendar.time)
        btn_save.isEnabled = true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        tv_date.text = sdDate.format(calendar.time)
        btn_save.isEnabled = true
    }

    override fun onChanged(t: Boolean) {
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cd.dispose()
    }
}