package com.zl.framework.manager.asyncTask;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * AsyncManager 定时任务使用示例
 * 功能：每隔5分钟执行一次自定义业务任务
 */
public class AsyncTaskDemo {

    // 定义任务执行间隔：5分钟（转换为毫秒）
    private static final long TASK_INTERVAL = 5 * 60 * 1000;

    /**
     * 自定义定时任务（继承TimerTask）
     * 这里编写需要每隔5分钟执行的业务逻辑
     */
    static class CustomScheduledTask extends TimerTask {
        @Override
        public void run() {
            try {
                // #######################
                // 核心业务逻辑（示例：打印当前时间+业务操作）
                // 你可以替换为自己的逻辑：比如数据同步、日志清理、状态检查等
                // #######################
                System.out.println("【定时任务执行】当前时间：" + System.currentTimeMillis());
                System.out.println("执行业务逻辑：比如清理过期缓存、同步数据库数据...");

            } catch (Exception e) {
                // 异常捕获：避免单个任务执行失败导致线程池异常
                System.err.println("定时任务执行异常：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动定时任务（每隔5分钟执行一次）
     * 核心思路：通过「固定延迟+循环提交」实现周期性执行
     */
    public static void startScheduledTask() {
        AsyncManager asyncManager = AsyncManager.me();

        // 定义「循环执行任务」的逻辑：执行完一次后，再次提交下一次任务
        Runnable loopTask = new Runnable() {
            @Override
            public void run() {
                // 1. 执行本次业务任务
                new CustomScheduledTask().run();

                // 2. 提交下一次任务（延迟5分钟后执行）
                asyncManager.getExecutor().schedule(this, TASK_INTERVAL, TimeUnit.MILLISECONDS);
            }
        };

        // 首次执行：延迟10毫秒（AsyncManager默认延迟）后启动，之后每隔5分钟执行
        asyncManager.execute(new TimerTask() {
            @Override
            public void run() {
                loopTask.run();
            }
        });

        System.out.println("定时任务已启动，每隔5分钟执行一次！");
    }

    // 测试入口
    public static void main(String[] args) {
        // 启动定时任务
        startScheduledTask();

        // 防止主线程退出（实际项目中无需此代码，因为Spring容器会保持运行）
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ####################### 移除了 Demo 类中重复的 getExecutor 方法 #######################
}