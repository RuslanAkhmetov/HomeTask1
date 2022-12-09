package com.geekbrain.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    val INDEX_LIST = "list_index"

    private val taskList = listOf(
        Tasks(R.string.task1, false),
        Tasks(R.string.task2, false),
        Tasks(R.string.task3, false),
        Tasks(R.string.task4, false),
        Tasks(R.string.task5, false),
        Tasks(R.string.taskA, false),
        Tasks(R.string.taskB, false),
        Tasks(R.string.taskC, false),
        Tasks(R.string.task6, false),
        Tasks(R.string.task7, false),
        Tasks(R.string.task8, false),
    )

    var index = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nextButton: Button = findViewById(R.id.button_next)
        val taskText: TextView = findViewById(R.id.task_text)
        val doneText: TextView = findViewById(R.id.done_text)
        val vipButton: Button = findViewById(R.id.button_vip)

        if (savedInstanceState == null) {
            index = 0
        } else {
            index = savedInstanceState.getInt(INDEX_LIST)
        }

        var resId: Int = taskList[index].task
        taskText.setText(resId)
        doneText.text = taskList[index].done.toString()

        nextButton.setOnClickListener {
            index = ++index % taskList.size
            resId = taskList[index].task
            taskText.setText(resId)
            doneText.text = convert (taskList[index].done)
            println("Задача ${resources.getString(taskList[index].task)} сделано ${taskList[index].done.toString()}")
        }

        vipButton.setOnClickListener {
            taskText.setText(vip.get().task)
            vip.print()
        }

        doneText.setOnClickListener {
            taskList[index].done = !taskList[index].done
            doneText.text = convert(taskList[index].done)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INDEX_LIST, index)
        super.onSaveInstanceState(outState)
    }

    fun convert(b: Boolean): String = if (b) {
        getString(R.string.done)
    } else {
        getString(R.string.in_process)
    }
}





