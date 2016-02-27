package cn.qianlicao.initialization

import android.util.Log
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by dongyayun on 16/2/18.
 */
abstract class Task(name: String) : Runnable {
    private val mIsCancelled = AtomicBoolean()
    var sons: HashMap<String, Task> = HashMap()
    var taskStatus = Status.STATUS_INIT

    var countdownLatch: CountDownLatch = CountDownLatch(1);

    var father: Task? = null
        set(value) {

            //father =value   千万不能这样用,否则会无限循环set
            field = value
            field!!.addSon(this)
        }

    var taskName: String = name


    override fun run() {
        if (isCancelled)
            return

        if (father != null) {

            Log.d("Task", taskName + "has father")

            Initialization.instance.threadPool.submit(father)
            taskStatus = Status.STATUS_WAITING
            countdownLatch.await()
        }


        if (isCancelled)
            return

        onDoSomething()

        onFinish()


        for (t in sons.values) {
            if (t.taskStatus == Status.STATUS_INIT) {
                Initialization.instance.threadPool.submit(t)
            } else if (t.taskStatus == Status.STATUS_WAITING) {
                t.countdownLatch.countDown();
            } else {
                //do nothing
            }
        }
    }

    protected open fun onDoSomething() {
        if (father != null) {
            Log.d("Task", taskName + "continue doing something")
        } else {
            Log.d("Task", taskName + "do something")
        }


        taskStatus = Status.STATUS_DOING
    }

    protected fun onFinish() {
        taskStatus = Status.STATUS_DONE
    }

    fun cancel() {
        if (father != null) {
            father!!.cancel()
            father = null
        }

        for (task in sons.values) {
            task.cancel()
        }

        sons.clear()

        mIsCancelled.set(true)
    }

    private val isCancelled: Boolean
        get() = mIsCancelled.get()

    fun addSon(task: Task) {
        sons.put(task.taskName, task)
    }


    fun removeSon(task: Task) {
        val name = task.taskName


        val task1 = sons[name] ?: return

        task1.cancel()
        sons.remove(name)
    }

}
