package com.zl.framework.manager.asyncTask;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.TimerTask;


 /**
 * 异步工厂（产生任务用）
 * AsyncFactory 主要用于创建异步任务，它相当于一个 "任务工厂"，
 * 可以根据不同的需求创建不同的任务（如记录用户登录信息、记录操作日志）。
 * @Author GuihaoLv
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");



}