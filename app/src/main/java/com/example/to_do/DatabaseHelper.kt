package com.example.to_do

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(TABLE_TASKS, null, null, null, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME))
                taskList.add(Task(id, name))
            }
        }
        return taskList
    }

    // Update task method (Modified to match the MainActivity call)
    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name) // Update the task name column
        }
        return db.update(TABLE_TASKS, values, "$COLUMN_ID=?", arrayOf(task.id.toString()))
    }

    // Delete task method (Modified to match the MainActivity call)
    fun deleteTask(taskId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_ID=?", arrayOf(taskId.toString()))
    }

    companion object {
        const val DATABASE_NAME = "ToDoApp.db"
        const val DATABASE_VERSION = 1
        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
    }
}
