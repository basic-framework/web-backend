package com.zl.web.manager.asyncTask;

import com.zl.common.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;


/**
 * 确保应用退出时能关闭后台线程
 * @Author GuihaoLv
 */
@Component
public class ShutdownManager
{
    private static final Logger logger = LoggerFactory.getLogger("sys-user");

    @PreDestroy //它用于在 Spring Bean 被销毁前执行清理逻辑。
    public void destroy()
    {
        shutdownAsyncManager();
        HttpUtil.shutdown();
    }

    /**
     * 停止异步执行任务
     */
    private void shutdownAsyncManager()
    {
        try
        {
            logger.info("====关闭后台任务任务线程池====");
            AsyncManager.me().shutdown();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

}