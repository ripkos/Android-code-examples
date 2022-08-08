package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.myapplication.databinding.FragmentTaskShowBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val taskIndex = "taskIndex"


/**
 * A simple [Fragment] subclass.
 * Use the [task_show.newInstance] factory method to
 * create an instance of this fragment.
 */
class task_show : Fragment(), SeekBar.OnSeekBarChangeListener {
    private var pos: Int? = null
    private lateinit var binding: FragmentTaskShowBinding
    //private lateinit var adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pos = it.getInt(taskIndex)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return FragmentTaskShowBinding.inflate(inflater, container, false).also{
            binding = it
            binding.btnBack.setOnClickListener {
                findNavController(this).navigateUp()
            }
            binding.btnEdit.setOnClickListener {
                findNavController(this).navigate(R.id.task_edit, bundleOf("isEdit" to true, "taskIndex" to pos))
            }
            replacePlaceholders()
            binding.taskProgresEdit2.setOnSeekBarChangeListener(this)
        }.root
    }
    private fun replacePlaceholders() {
        val task = DataSource.tasks[pos!!]
        binding.taskTime.text=task.approximateTime.toString()
        binding.taskDeadline.text=Task.dateToString(task.deadLine)
        binding.taskName.text=task.name
        binding.taskPriority.numStars=task.priority
        binding.taskPriority.rating= task.priority.toFloat()
        binding.taskPriority.stepSize= 0F
        binding.taskProgressShow.progress=task.completion
        binding.taskProgresEdit2.progress=task.completion/5
        binding.taskProgresLabel.setText("${task.completion}%")
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            task_show().apply {
                arguments = Bundle().apply {
                    putInt(taskIndex, param1)
                }
            }
    }

    override fun onProgressChanged(sb: SeekBar?, newValue: Int, p2: Boolean) {
        if (sb != null) {
            binding.taskProgressShow.progress=newValue*5
            binding.taskProgresLabel.setText("${newValue*5}%")
            DataSource.tasks[pos!!].completion=newValue*5
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }
}