package com.example.myapplication

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ListItemBinding
import java.util.*

interface ITaskClickListener {
    fun onTaskClick(pos:Int)
    fun onTaskLongClick(pos:Int)
}


class TaskViewHolder(val binding: ListItemBinding, val listener: ITaskClickListener) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {



    fun bind(task: Task) {
        binding.taskName.text = task.name
        binding.taskPriority.numStars=task.priority
        binding.taskPriority.rating= task.priority.toFloat()
        binding.taskPriority.stepSize= 0F
        binding.taskDate.text =  Task.dateToString(task.deadLine)
        binding.taskProgressShow.progress = task.completion
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)

    }


    override fun onClick(p0: View?) {
        listener.onTaskClick(adapterPosition)
    }

    override fun onLongClick(p0: View?): Boolean {
        listener.onTaskLongClick(adapterPosition)
        return true
    }
}

class TaskAdapter(private val taskListener: ITaskClickListener) : RecyclerView.Adapter<TaskViewHolder>() {
    private val data = mutableListOf<Task>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return TaskViewHolder(binding, taskListener)
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("NotifyDataSetChanged")
    fun replace(newData: List<Task>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }


}