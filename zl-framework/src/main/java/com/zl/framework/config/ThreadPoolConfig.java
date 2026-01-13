package com.zl.framework.config;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import com.zl.common.utils.jucUtils.Threads;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
* 自定义线程池配置
* @Author GuihaoLv
**/
@Configuration
public class ThreadPoolConfig
{
    // 核心线程池大小
    private int corePoolSize = 50;

    // 最大可创建的线程数
    private int maxPoolSize = 200;

    // 队列最大长度
    private int queueCapacity = 1000;

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds = 300;



    /**
     * 通用任务线程池
     * @return
     */
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 线程池对拒绝任务(无线程可用)的处理策略
        //当线程池满了，新任务无法加入时，CallerRunsPolicy 让提交任务的线程（即调用方线程）直接执行该任务，
        // 而不是丢弃或抛出异常，从而保证任务不会丢失。
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService()
    {
        //这里没有最大线程数的概念，所有线程都属于核心线程。
        return new ScheduledThreadPoolExecutor(corePoolSize,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d") //设置线程名称，方便排查日志。
                        .daemon(true).build(), //daemon(true) 使线程池中的线程成为 守护线程，即 JVM 退出时不会阻止进程终止。
                new ThreadPoolExecutor.CallerRunsPolicy()) //使用 CallerRunsPolicy，避免任务丢失。会让提交任务的线程（调用方线程）直接执行该任务，避免任务丢失。
        {
            //任务执行完毕后，调用 Threads.printException(r, t)，捕获并记录异常，确保线程池不会因为未捕获的异常而崩溃。
            @Override
            protected void afterExecute(Runnable r, Throwable t)
            {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }
}