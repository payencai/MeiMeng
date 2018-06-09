package com.example.meimeng.manager;

import android.app.Activity;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created by HIAPAD on 2018/4/8.
 */

public class ActivityManager {


    private static Stack<Activity> sActivityStack;


    private static ActivityManager mInstance;

    private ActivityManager() {
        if (sActivityStack == null) {
            sActivityStack = new Stack<>();
        }

    }

    public static ActivityManager getInstance() {
        if (mInstance == null) {
            mInstance = new ActivityManager();
        }
        return mInstance;
    }


    public void pushActivity(Activity activity) {
        sActivityStack.add(activity);
    }

    public Activity getTopActivity() {
        Activity activity = sActivityStack.lastElement();
        return activity;
    }

    public void finishTopActivity() {
        Activity activity = sActivityStack.lastElement();
        activity.finish();
    }

    public void finishActivity(Class<?> cls) {
        Iterator<Activity> iterator = sActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass().equals(cls)) {
                sActivityStack.remove(activity);
                activity.finish();
            }
        }
    }

    public void finishAllActivity() {
        for (Activity activity : sActivityStack) {
            if (activity != null) {
                activity.finish();
            }
        }
        sActivityStack.clear();
    }


    public void appExit() {
        finishAllActivity();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            sActivityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    public Activity getActivity(Class<?> cls) {
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }




}
