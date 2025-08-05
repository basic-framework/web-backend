package com.zl.web.manager.ServerMonitor;

import com.zl.common.utils.calculateUtils.Arith;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * JVM相关信息
 * @Author GuihaoLv
 */
public class Jvm
{
    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    public double getTotal()
    {
        return Arith.div(total, (1024 * 1024), 2);
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public double getMax()
    {
        return Arith.div(max, (1024 * 1024), 2);
    }

    public void setMax(double max)
    {
        this.max = max;
    }

    public double getFree()
    {
        return Arith.div(free, (1024 * 1024), 2);
    }

    public void setFree(double free)
    {
        this.free = free;
    }

    public double getUsed()
    {
        return Arith.div(total - free, (1024 * 1024), 2);
    }

    public double getUsage()
    {
        return Arith.mul(Arith.div(total - free, total, 4), 100);
    }

    /**
     * 获取JDK名称
     */
    public String getName()
    {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getHome()
    {
        return home;
    }

    public void setHome(String home)
    {
        this.home = home;
    }

//    /**
//     * JDK启动时间
//     */
//    public String getStartTime()
//    {
//        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getServerStartDate());
//    }
//
//    /**
//     * JDK运行时间
//     */
//    public String getRunTime()
//    {
//        return DateUtils.timeDistance(DateUtils.getNowDate(), DateUtils.getServerStartDate());
//    }
private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * 获取JVM启动时间
     */
    public String getStartTime() {
        long startMillis = ManagementFactory.getRuntimeMXBean().getStartTime();
        LocalDateTime startTime = Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return formatter.format(startTime);
    }

    /**
     * 获取JVM运行时间（当前时间 - 启动时间）
     */
    public String getRunTime() {
        long startMillis = ManagementFactory.getRuntimeMXBean().getStartTime();
        long nowMillis = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(nowMillis - startMillis);

        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();
        long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
        long seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds();

        return String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, seconds);
    }

    /**
     * 运行参数
     */
    public String getInputArgs()
    {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
    }
}
