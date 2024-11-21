package com.example.to_do

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the database helper
        databaseHelper = DatabaseHelper(this)

        // Retrieve UI components
        val inputTask = findViewById<TextInputEditText>(R.id.inputTask)
        val addTaskButton = findViewById<FloatingActionButton>(R.id.addTaskButton)
        val taskRecyclerView = findViewById<RecyclerView>(R.id.taskRecyclerView)

        // Load tasks from SQLite database
        taskList.addAll(databaseHelper.getAllTasks())

        // Setup RecyclerView and Adapter
        // Correct initialization:
        taskAdapter = TaskAdapter(this, taskList, databaseHelper)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        // Handle adding new tasks
        addTaskButton.setOnClickListener {
            val taskName = inputTask.text.toString().trim()
            if (taskName.isNotEmpty()) {
                val task = Task(0, taskName)  // ID will be auto-assigned
                val taskId = databaseHelper.insertTask(task)

                if (taskId != -1L) {
                    task.id = taskId.toInt() // Update task with assigned ID
                    taskList.add(task)
                    taskAdapter.notifyItemInserted(taskList.size - 1)
                    inputTask.text?.clear()
                }
            }
        }
    }

    // Edit Task functionality
    private fun onEditTask(position: Int) {
        val task = taskList[position]

        // Create Edit Text for dialog
        val input = EditText(this)
        input.setText(task.name)

        // Create the Edit Task dialog
        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val updatedTaskName = input.text.toString().trim()
                if (updatedTaskName.isNotEmpty()) {
                    task.name = updatedTaskName
                    val updatedRows = databaseHelper.updateTask(task) // Update method with Task object

                    if (updatedRows > 0) {
                        taskAdapter.notifyItemChanged(position)
                        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Delete Task functionality
    private fun onDeleteTask(position: Int) {
        val task = taskList[position]

        // Create a confirmation dialog for delete
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                val rowsDeleted = databaseHelper.deleteTask(task.id) // Delete method using task ID

                if (rowsDeleted > 0) {
                    taskList.removeAt(position)
                    taskAdapter.notifyItemRemoved(position)
                    Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
