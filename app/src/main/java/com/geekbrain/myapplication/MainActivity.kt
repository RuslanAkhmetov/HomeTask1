package com.geekbrain.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val taskList = listOf(
        Questions(R.string.task1, false),
        Questions(R.string.task2, false),
        Questions(R.string.task3, false),
        Questions(R.string.task4, false),
        Questions(R.string.task5, false),
        Questions(R.string.taskA, false),
        Questions(R.string.taskB, false),
        Questions(R.string.taskC, false),
        Questions(R.string.task6, false),
        Questions(R.string.task7, false),
        Questions(R.string.task8, false),

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val nextButton:Button = findViewById(R.id.button_next)
        val taskText :TextView = findViewById(R.id.task_text)
        val doneText :TextView = findViewById(R.id.done_text)
        var index = 0
        var resId :Int = taskList[index].task
        taskText.setText(resId)
        doneText.text = taskList[index].done.toString()
        nextButton.setOnClickListener{
            resId = taskList[index].task
            taskText.setText(resId)
            doneText.text = taskList[index].done.toString()
            index = ++index % taskList.size
        }

    }
}