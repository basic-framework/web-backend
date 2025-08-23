package com.zl.framework.manager.asyncTask;



import com.zl.common.utils.springUtils.SpringUtil;
import com.zl.common.utils.jucUtils.Threads;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 异步任务管理器
 * AsyncManager 是 整个异步任务调度的核心，它提供了 任务执行、调度和管理。
 * @Author GuihaoLv
 */
public class AsyncManager
{
    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池
     * executor 采用 ScheduledExecutorService 线程池，可以 定时执行异步任务，提高并发能力
     */
    private ScheduledExecutorService executor = SpringUtil.getBean("scheduledExecutorService");

    /**
     * 单例模式
     * 采用 单例模式，确保全局只有一个 AsyncManager 实例，保证任务调度统一管理。
     创建单例对象
     */
    private AsyncManager(){}

    //创建异步任务管理器的静态对象
    private static AsyncManager me = new AsyncManager();

    public static AsyncManager me()
    {
        return me;
    }

    /**
     * 使用调度线程池执行任务
     * @param task 任务
     */
    //TimerTask是Java编程语言中的一个抽象类，通常用于安排将来某个时间执行的任务，
    // 或者以固定的速率重复执行的任务。
    // 它是与Timer类一起使用的，Timer负责管理和调度这些任务。
    public void execute(TimerTask task)
    {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown()
    {
        //优雅关闭线程池
        Threads.shutdownAndAwaitTermination(executor);
    }



}