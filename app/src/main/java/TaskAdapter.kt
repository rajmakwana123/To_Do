package com.example.to_do

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private val taskList: MutableList<Task>,
    private val databaseHelper: DatabaseHelper
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder class to hold references to the views
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        var task = taskList[position]  // This is fine for reading, no modification yet.

        // Bind task name to TextView
        holder.taskName.text = task.name

        // Handle Edit Button Click
        holder.editButton.setOnClickListener {
            showEditTaskDialog(task, position)
        }

        // Handle Delete Button Click
        holder.deleteButton.setOnClickListener {
            deleteTask(task, position)
        }
    }

    override fun getItemCount(): Int = taskList.size

    // Function to show the Edit Task dialog
    private fun showEditTaskDialog(task: Task, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dailog_edit_task, null)
        val editTaskName = dialogView.findViewById<EditText>(R.id.editTaskName)

        editTaskName.setText(task.name) // Pre-fill the task name

        AlertDialog.Builder(context)
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedName = editTaskName.text.toString()
                if (updatedName.isNotEmpty()) {
                    // Update the name in the database and the task list
                    task.name = updatedName

                    // Update in the database
                    val updatedRows = databaseHelper.updateTask(task) // Update in database
                    if (updatedRows > 0) {
                        taskList[position] = task // Directly update the task in the list
                        notifyItemChanged(position) // Notify the adapter to reflect the change
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Function to delete a task
    private fun deleteTask(task: Task, position: Int) {
        val rowsDeleted = databaseHelper.deleteTask(task.id) // Delete from database
        if (rowsDeleted > 0) {
            taskList.removeAt(position) // Remove the task from the list
            notifyItemRemoved(position) // Notify the adapter about the item removal
        }
    }
}
