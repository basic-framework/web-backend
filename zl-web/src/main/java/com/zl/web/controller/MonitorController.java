package com.zl.web.controller;

import com.zl.common.result.Result;
import com.zl.common.utils.AddressUtil;
import com.zl.common.utils.IPUtil;
import com.zl.web.manager.ServerMonitor.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

 /**
 * 运维监测相关接口
 * @Author GuihaoLv
 */
@RestController
@RequestMapping("/web/monitor")
@Slf4j
@Tag(name = "运维监测", description = "运维监测")
public class MonitorController {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 实时IP地址监测接口
     * @param request HttpServletRequest
     * @return IP和归属地信息
     */
    @GetMapping("/ipMonitor")
    @Operation(summary = "获取用户IP及IP解析地址")
    public Result<Map<String, String>> monitorIp(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();

        String ip = IPUtil.getIpAddr(request); // 获取客户端IP
        String location = AddressUtil.getRealAddressByIP(ip); // 获取地址

        result.put("ip", ip);
        result.put("location", location);
        return Result.success(result);
    }


    /**
     * 获取缓存监控信息
     * @return
     * @throws Exception
     */
    @GetMapping("/getRedisInfo")
    @Operation(summary = "获取缓存监控信息")
    public Result getInfo() throws Exception {
        Map<String, Object> result = new HashMap<>(3);
        //获取 Redis 服务器信息（如版本、内存使用等）
        Properties info = (Properties) redisTemplate
                .execute((RedisCallback<Object>) connection -> connection.commands().info());
        //获取 Redis 命令统计信息（如每个命令的调用次数）
        Properties commandStats = (Properties) redisTemplate
                .execute((RedisCallback<Object>) connection -> connection.commands().info("commandstats"));
        //获取当前数据库的键总数
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.commands().dbSize());
        result.put("info", info);
        result.put("dbSize", dbSize);
        //将 commandstats 的原始数据转换为前端友好的格式。
        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return Result.success(result);
    }


    /**
     * 获取服务器监控信息
     * @return
     * @throws Exception
     */
    @GetMapping("/serverMonitor")
    @Operation(summary = "获取服务器监控信息")
    public Result getServerInfo() throws Exception {
        Server server = new Server();
        server.copyTo(); // 采集系统信息
        return Result.success(server);
    }



}
