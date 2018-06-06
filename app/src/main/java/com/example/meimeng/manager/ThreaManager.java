package com.example.meimeng.manager;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by HIAPAD on 2018/4/8.
 */

public class ThreaManager {

    private ThreaManager() {}

    private static ThreaManager sThreaManager;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final BlockingDeque<Runnable> sPoolWorkQueue = new LinkedBlockingDeque<>(128);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "IVIThread" + mCount.getAndIncrement());
        }
    };

    public static final ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            sPoolWorkQueue, sThreadFactory);

    public static ThreaManager getInstance() {
        if (sThreaManager == null) {
            synchronized (ThreaManager.class) {
                if (sThreaManager == null) {
                    sThreaManager = new ThreaManager();
                }
            }
        }
        return sThreaManager;
    }

    public void test() {
        //关闭线程池，不影响已经提交的任务  或者 shutDown()不是关闭线程池，是停止任务队列中要执行的任务
//        executor.shutdown();
        //关闭线程池，并尝试去终止正在执行的线程
//        executor.shutdownNow();
        //允许核心线程闲置超时时被回收
//        executor.allowCoreThreadTimeOut(true);
        //一般情况下我们使用execute来提交任务，但是有时候可能也会用到submit，使用submit的好处是submit有返回值
//        executor.submit(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });


        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Future<String> future = executor.submit(new MyTask(i));
            futures.add(future);
        }

        for (Future<String> future : futures) {
            try {
                Log.d("lingtao", "test: " + future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


    }

    private static class MyTask implements Callable<String> {
        private int taskId;

        public MyTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public String call() throws Exception {
            SystemClock.sleep(1000);
            return "call方法被调用----" + Thread.currentThread().getName() + "---" + taskId;
        }
    }

    public static  <T> T callResults(Callable<T> callable) {
        Future future = executor.submit(callable);
        try {
            return (T) future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void pushRunnable(Runnable runnable) {
        executor.execute(runnable);
    }

}

