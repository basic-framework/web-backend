package com.zl.web.controller;

import com.zl.common.utils.ipUtils.AddressUtil;
import com.zl.common.utils.ipUtils.IPUtil;
import com.zl.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * IP定位查询测试接口
 * @Author: GuihaoLv
 */
@RestController
@RequestMapping("/web/ip")
@Tag(name = "IP定位测试接口", description = "验证IP地址地理位置查询功能")
public class IpLocationTestController {

    /**
     * IP定位查询测试
     * @param request Http请求对象
     * @param ip 可选参数：手动传入要测试的IP（为空则自动获取客户端IP）
     * @return 测试结果
     */
    @GetMapping("/location")
    @Operation(summary = "IP定位查询", description = "支持自动获取客户端IP或手动传入IP，返回定位信息")
    public Result<IpLocationResult> testIpLocation(
            HttpServletRequest request,
            @Parameter(description = "要测试的IP地址（为空则自动获取客户端IP）")
            @RequestParam(required = false) String ip) {

        // 1. 处理IP参数（手动传入则用传入值，否则自动获取客户端IP）
        String targetIp = ip;
        if (ip == null || ip.trim().isEmpty()) {
            targetIp = IPUtil.getIpAddr(request);
        }

        // 2. 构建测试结果对象
        IpLocationResult result = new IpLocationResult();
        result.setRequestIp(targetIp);
        result.setInternalIp(IPUtil.internalIp(targetIp));

        // 3. 查询地理位置
        try {
            String location = AddressUtil.getRealAddressByIP(targetIp);
            result.setLocation(location);
            result.setSuccess(true);
            result.setMessage("IP定位查询成功");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setLocation(AddressUtil.UNKNOWN);
            result.setMessage("IP定位查询失败：" + e.getMessage());
        }

        return Result.success(result);
    }

    /**
     * IP定位测试结果封装类
     */
    @Data
    public static class IpLocationResult {
        /** 请求的IP地址 */
        private String requestIp;
        /** 是否为内网IP */
        private boolean internalIp;
        /** 定位结果（省份 城市） */
        private String location;
        /** 查询是否成功 */
        private boolean success;
        /** 提示信息 */
        private String message;
    }
}