package cn.qianlicao.initialization

import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by dongyayun on 16/2/21.
 */
class Initialization private constructor() {
    private val mTasks = LinkedList<Task>()
    private var mThreadCount = 0

    var threadPool: ExecutorService = Executors.newCachedThreadPool();

    private object Holder {
        val INSTANCE = Initialization();
    }

    companion object {

        val instance: Initialization by lazy(LazyThreadSafetyMode.SYNCHRONIZED, { Holder.INSTANCE });

    }

    fun start() {
        for (t in mTasks) {
            threadPool.submit(t);
        }

    }


    fun setThreadCount(size: Int) {
        if (mThreadCount == size)
            return
        mThreadCount = size

        createThreadPool()
    }


    fun createThreadPool() {
        if (mThreadCount > 0) {

            threadPool = Executors.newFixedThreadPool(mThreadCount)

        } else {

            threadPool = Executors.newCachedThreadPool()
        }
    }


    fun addTask(task: Task) {
        mTasks.add(task)
    }

    fun cancelTasks() {
        for (task in mTasks) {
            task.cancel()
        }

        mTasks.clear()
    }

}
