package com.example.myapplication

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.SeekBar
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.databinding.FragmentTaskEditBinding
import java.util.*
import com.example.myapplication.TimeOptions


private const val ARG_IS_EDIT = "isEdit"
private const val ARG_POS = "taskIndex"

class task_edit : Fragment(), SeekBar.OnSeekBarChangeListener{
    private var pos: Int? = null
    private var isEdit: Boolean? = null

    private lateinit var binding: FragmentTaskEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pos = it.getInt(ARG_POS)
            isEdit = it.getBoolean(ARG_IS_EDIT)
        }
    }
    private fun doSetup(){
        var task = DataSource.tasks[pos!!]
        binding.taskEditName.setText(task.name)
        binding.taskProgresLabelShow.setText("${task.completion}%")
        binding.taskEditDate.updateDate(task.deadLine.year+1900,task.deadLine.month,task.deadLine.day)
        binding.taskPriorityEdit.progress=task.priority
        binding.taskPriorityShow.numStars= task.priority
        binding.taskPriorityShow.rating= task.priority.toFloat()
        binding.taskEditEstimatedInt.setText(task.approximateTime.amount.toString())
        binding.taskProgressShow.progress=task.completion
        binding.taskProgresEdit.progress=task.completion/5
        if(task.approximateTime.timeType==TimeOptions.Days){
            binding.optionDays.isChecked=true
        }
        else {
            binding.optionHours.isChecked=true
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return FragmentTaskEditBinding.inflate(inflater, container, false).also {
            binding = it
            if(isEdit==true){
                doSetup()
            }
            binding.taskPriorityEdit.setOnSeekBarChangeListener(this)
            binding.taskProgresEdit.setOnSeekBarChangeListener(this)
            binding.btnBack2.setOnClickListener {
                NavHostFragment.findNavController(this).navigateUp()
            }
            binding.btnSave.setOnClickListener {
                if(isEdit == true) {
                    DataSource.tasks[pos!!].name= binding.taskEditName.text.toString()
                    DataSource.tasks[pos!!].completion= binding.taskProgresEdit.progress*5
                    DataSource.tasks[pos!!].deadLine = pickerToDate(binding.taskEditDate)
                    DataSource.tasks[pos!!].approximateTime = ApproximateTime(binding.taskEditEstimatedInt.text.toString().toInt(),
                        if (binding.optionDays.isChecked) TimeOptions.Days else TimeOptions.Hours)
                    DataSource.tasks[pos!!].priority = binding.taskPriorityEdit.progress
                }
                else {
                    DataSource.tasks.add(Task(
                        binding.taskPriorityEdit.progress,
                        binding.taskEditName.text.toString(),
                        binding.taskProgresEdit.progress*5,
                        pickerToDate(binding.taskEditDate),
                        ApproximateTime(binding.taskEditEstimatedInt.text.toString().toInt(),
                            if (binding.optionDays.isChecked) TimeOptions.Days else TimeOptions.Hours)
                    ))
                }
                DataSource.sort()
                var navcontroller = NavHostFragment.findNavController(this)
                navcontroller.popBackStack()
                navcontroller.navigate(R.id.task_list)
            }

        }.root
    }

    private fun pickerToDate(d: DatePicker): Date {
        return Date(d.year-1900,d.month,d.dayOfMonth)
    }


    companion object {
        @JvmStatic
        fun newInstance(pos: Int, isEdit: Boolean) =
            task_edit().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_EDIT, isEdit)
                    putInt(ARG_POS, pos)
                }
            }
    }

    override fun onProgressChanged(sb: SeekBar?, newValue: Int, fromUser: Boolean) {
        if (sb != null) {
            if(sb.id==binding.taskPriorityEdit.id){
                binding.taskPriorityShow.numStars=newValue
                binding.taskPriorityShow.rating= newValue.toFloat()
            }
            else {
                binding.taskProgressShow.progress=newValue*5
                binding.taskProgresLabelShow.setText("${newValue*5}%")
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}