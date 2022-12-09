package com.geekbrain.myapplication

object vip {

    private val vipTask =  Tasks(R.string.task1, false).copy(R.string.VIPTask)
    fun get() = vipTask
    fun print() {
        println("${vipTask.task} ${vipTask.done.toString()}")
    }
}