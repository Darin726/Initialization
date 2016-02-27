package cn.qianlicao.initialization;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dongyayun on 16/2/18.
 */
public abstract class Task implements Runnable {
    private final AtomicBoolean mIsCancelled = new AtomicBoolean();
    private HashMap<String, Task> mSons = new HashMap();
    private int mTaskStatus = Status.STATUS_INIT;

    private Task mFather;

    private String mTaskName;

    public Task(String name) {

    }


    @Override
    public void run() {
        if (isCancelled())
            return;


        if (mFather != null) {
            Initialization.getInstance().getThreadPool().submit(mFather);
            setTaskStatus(Status.STATUS_WAITING);
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        if (isCancelled())
            return;

        onDoSomething();

        onFinish();


        for (Task t : mSons.values()) {
            if (t.getTaskStatus() == Status.STATUS_INIT) {
                Initialization.getInstance().getThreadPool().submit(t);
            } else if (t.getTaskStatus() == Status.STATUS_WAITING) {
                t.notify();
            } else {
                //do nothing
            }
        }
    }

    protected void onDoSomething() {
        setTaskStatus(Status.STATUS_DOING);
    }

    protected void onFinish() {
        setTaskStatus(Status.STATUS_DONE);
    }

    public void cancel() {
        if (mFather != null) {
            mFather.cancel();
            mFather = null;
        }

        for (Task task : mSons.values()) {
            task.cancel();
        }

        mSons.clear();

        mIsCancelled.set(true);
    }

    private boolean isCancelled() {
        return mIsCancelled.get();
    }


    public int getTaskStatus() {
        return mTaskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        mTaskStatus = taskStatus;
    }

    public Task getFather() {
        return mFather;
    }

    public void setFather(Task father) {
        mFather = father;
        mFather.addSon(this);
    }

    public HashMap<String, Task> getSons() {
        return mSons;
    }

    public void setSons(HashMap<String, Task> sons) {
        mSons = sons;
    }

    public void addSon(Task task) {
        mSons.put(task.getTaskName(), task);
    }


    public void removeSon(Task task) {
        String name = task.getTaskName();


        Task task1 = mSons.get(name);
        if (task1 == null)
            return;

        task1.cancel();
        mSons.remove(name);
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }
}
