package com.geekbrain.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.geekbrain.myapplication.databinding.ActivityMainBinding

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

    private lateinit var  binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            index = 0
        } else {
            index = savedInstanceState.getInt(INDEX_LIST)
        }

        var resId: Int = taskList[index].task
        binding.taskText.setText(resId)
        binding.doneText.text = taskList[index].done.toString()

        binding.buttonNext.setOnClickListener {
            index = ++index % taskList.size
            resId = taskList[index].task
            binding.taskText.setText(resId)
            binding.doneText.text = convert (taskList[index].done)
            println("Задача ${resources.getString(taskList[index].task)} сделано ${taskList[index].done.toString()}")
        }

        binding.buttonVip.setOnClickListener {
            binding.taskText.setText(vip.get().task)
            vip.print()
        }

        binding.doneText.setOnClickListener {
            taskList[index].done = !taskList[index].done
            binding.doneText.text = convert(taskList[index].done)
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





