package com.example.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentTaskListBinding


class task_list : Fragment(), ITaskClickListener {
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return FragmentTaskListBinding.inflate(inflater, container, false).also{
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TaskAdapter(this).apply {
            replace(DataSource.tasks)
        }
        binding.tasks.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
            it.isLongClickable=true
        }
        binding.summary.setText("You have ${DataSource.calculateTaskToWeekEnd()} tasks in next 7 days!")


        binding.addNewTask.setOnClickListener {
            findNavController(this).navigate(R.id.task_edit)
        }


    }

    override fun onTaskClick(pos: Int) {

        findNavController(this).navigate(R.id.task_show, bundleOf("taskIndex" to pos))
    }

    override fun onTaskLongClick(pos: Int) {
        var builder = AlertDialog.Builder(this.context)
        builder.setTitle("Confirm delete")
        builder.setMessage("Are you sure you want to delete this task?")
        builder.setPositiveButton("Yes") { dialogInterface, i ->
            DataSource.tasks.removeAt(pos)
            adapter.replace(DataSource.tasks)
            binding.summary.setText("You have ${DataSource.calculateTaskToWeekEnd()} tasks in next 7 days!")
            dialogInterface.dismiss()


        }
        builder.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.cancel()
        }
        var alert = builder.create()
        alert.show()
    }
    override fun onStart() {
        super.onStart()
        adapter?.replace(DataSource.tasks)
    }
}

