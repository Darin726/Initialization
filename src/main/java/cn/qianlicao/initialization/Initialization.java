package cn.qianlicao.initialization;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dongyayun on 16/2/21.
 */
public class Initialization {

    private static Initialization instance;
    private LinkedList<Task> mTasks = new LinkedList<>();
    private int mThreadCount = 0;

    private ExecutorService mExecutorService;

    private Initialization() {

    }

    public static void init() {

        instance = new Initialization();

    }


    public static Initialization getInstance() {
        return instance;
    }


    public void start() {

        for (Task t : mTasks) {

        }

    }


    public void setThreadCount(int size) {
        if (mThreadCount == size)
            return;
        mThreadCount = size;

        createThreadPool();
    }


    public void createThreadPool() {
        if (mThreadCount > 0) {

            mExecutorService = Executors.newFixedThreadPool(mThreadCount);

        } else {

            mExecutorService = Executors.newCachedThreadPool();
        }
    }


    public void addTask(Task task) {
        mTasks.add(task);
    }

    public void cancelTasks() {
        for (Task task : mTasks) {
            task.cancel();
        }

        mTasks.clear();
    }


    public ExecutorService getThreadPool() {
        return mExecutorService;
    }
}
