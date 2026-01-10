package com.zl.common.utils.ipUtils;

import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址处理工具类
 * 核心能力：
 * 1. 适配Nginx/网关反向代理场景，正确解析客户端真实IP
 * 2. 准确判断内网IP（支持10.x.x.x/172.16.x.x-172.31.x.x/192.168.x.x网段）
 * 3. 处理IPv6本地回环、多级代理IP、IP空格等边界场景
 * @Author GuihaoLv
 */
public class IPUtil {

    /**
     * 获取客户端真实IP（适配反向代理场景）
     * 优先级：X-Forwarded-For > Proxy-Client-IP > WL-Proxy-Client-IP > X-Real-IP > RemoteAddr
     * @param request Http请求对象
     * @return 客户端真实IP（如114.114.114.114），异常时返回unknown
     */
    public static String getIpAddr(HttpServletRequest request) {
        // 空请求直接返回unknown
        if (request == null) {
            return "unknown";
        }

        String clientIp = null;
        // 1. 读取X-Forwarded-For（多级代理时格式：客户端IP, 代理1IP, 代理2IP）
        clientIp = request.getHeader("x-forwarded-for");
        // 2. 读取Proxy-Client-IP（Apache代理常用）
        if (isUnknown(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        // 3. 读取WL-Proxy-Client-IP（WebLogic代理常用）
        if (isUnknown(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        // 4. 读取X-Real-IP（Nginx代理常用）
        if (isUnknown(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }

        // 5. 所有代理头都未获取到，读取RemoteAddr（最后兜底）
        if (isUnknown(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        // 6. 处理IPv6本地回环地址（0:0:0:0:0:0:0:1），转为IPv4本地回环
        if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
            clientIp = "127.0.0.1";
        }

        // 7. 解析多级代理中的真实客户端IP（核心：过滤代理IP，提取第一个有效客户端IP）
        clientIp = getMultistageReverseProxyIp(clientIp);

        return clientIp;
    }

    /**
     * 判断是否为内网IP（严格遵循RFC1918私有地址规范）
     * 私有地址范围：
     * - 10.0.0.0/8        （10.x.x.x）
     * - 172.16.0.0/12     （172.16.x.x - 172.31.x.x）
     * - 192.168.0.0/16    （192.168.x.x）
     * - 127.0.0.1         （本地回环）
     * @param ip 待判断的IP地址
     * @return true=内网IP，false=公网IP
     */
    public static boolean internalIp(String ip) {
        // 空IP/unknown/本地回环，直接判定为内网
        if (isUnknown(ip) || "127.0.0.1".equals(ip)) {
            return true;
        }

        // 将IP转为字节数组（仅支持IPv4）
        byte[] ipBytes = textToNumericFormatV4(ip);
        // IP格式错误（非IPv4），判定为非内网
        if (ObjectUtil.isNull(ipBytes)) {
            return false;
        }

        return internalIp(ipBytes);
    }

    /**
     * 从字节数组层面判断是否为内网IP（核心逻辑）
     * @param ipBytes IPv4地址的字节数组（长度必须为4）
     * @return true=内网IP，false=公网IP
     */
    private static boolean internalIp(byte[] ipBytes) {
        // 非4字节数组（非IPv4），直接返回false
        if (ObjectUtil.isNull(ipBytes) || ipBytes.length != 4) {
            return false;
        }

        // 拆分IP的4个段（byte类型，范围-128~127，需转无符号整数）
        int b0 = Byte.toUnsignedInt(ipBytes[0]); // 第一段
        int b1 = Byte.toUnsignedInt(ipBytes[1]); // 第二段

        // 10.x.x.x 网段（10.0.0.0 - 10.255.255.255）
        if (b0 == 0x0A) { // 0x0A = 10
            return true;
        }

        // 172.16.x.x - 172.31.x.x 网段
        if (b0 == 0xAC) { // 0xAC = 172
            return (b1 >= 0x10 && b1 <= 0x1F); // 0x10=16，0x1F=31
        }

        // 192.168.x.x 网段（192.168.0.0 - 192.168.255.255）
        if (b0 == 0xC0 && b1 == 0xA8) { // 0xC0=192，0xA8=168
            return true;
        }

        // 其他均为外网IP
        return false;
    }

    /**
     * 将IPv4地址字符串转为字节数组（适配各种异常格式）
     * 支持格式：
     * - 标准格式：192.168.1.1
     * - 简化格式：192.168.1（等效192.168.0.1）、192.168（等效192.168.0.0）等
     * @param ip IPv4地址字符串
     * @return 字节数组（长度4），格式错误返回null
     */
    public static byte[] textToNumericFormatV4(String ip) {
        // 空IP直接返回null
        if (ObjectUtil.isEmpty(ip)) {
            return null;
        }

        byte[] result = new byte[4];
        String[] ipSegments = ip.split("\\.", -1); // 按.拆分，保留空段

        try {
            long segmentValue;
            int segmentCount = ipSegments.length;

            // 校验段数（仅支持1-4段）
            if (segmentCount < 1 || segmentCount > 4) {
                return null;
            }

            // 逐段解析（核心逻辑：补全缺失的段，确保最终4段）
            switch (segmentCount) {
                case 1: // 1段（如：3232235777 → 192.168.1.1）
                    segmentValue = Long.parseLong(ipSegments[0]);
                    if (segmentValue < 0 || segmentValue > 0xFFFFFFFFL) { // 0xFFFFFFFF=4294967295
                        return null;
                    }
                    result[0] = (byte) ((segmentValue >> 24) & 0xFF);
                    result[1] = (byte) ((segmentValue >> 16) & 0xFF);
                    result[2] = (byte) ((segmentValue >> 8) & 0xFF);
                    result[3] = (byte) (segmentValue & 0xFF);
                    break;

                case 2: // 2段（如：192.168 → 192.168.0.0；192.168.1 → 192.168.0.1）
                    segmentValue = Integer.parseInt(ipSegments[0]);
                    if (segmentValue < 0 || segmentValue > 255) {
                        return null;
                    }
                    result[0] = (byte) (segmentValue & 0xFF);

                    segmentValue = Integer.parseInt(ipSegments[1]);
                    if (segmentValue < 0 || segmentValue > 0xFFFFFF) { // 0xFFFFFF=16777215
                        return null;
                    }
                    result[1] = (byte) ((segmentValue >> 16) & 0xFF);
                    result[2] = (byte) ((segmentValue >> 8) & 0xFF);
                    result[3] = (byte) (segmentValue & 0xFF);
                    break;

                case 3: // 3段（如：192.168.1 → 192.168.1.0）
                    for (int i = 0; i < 2; i++) {
                        segmentValue = Integer.parseInt(ipSegments[i]);
                        if (segmentValue < 0 || segmentValue > 255) {
                            return null;
                        }
                        result[i] = (byte) (segmentValue & 0xFF);
                    }

                    segmentValue = Integer.parseInt(ipSegments[2]);
                    if (segmentValue < 0 || segmentValue > 0xFFFF) { // 0xFFFF=65535
                        return null;
                    }
                    result[2] = (byte) ((segmentValue >> 8) & 0xFF);
                    result[3] = (byte) (segmentValue & 0xFF);
                    break;

                case 4: // 4段（标准格式，核心场景）
                    for (int i = 0; i < 4; i++) {
                        segmentValue = Integer.parseInt(ipSegments[i]);
                        if (segmentValue < 0 || segmentValue > 255) {
                            return null;
                        }
                        result[i] = (byte) (segmentValue & 0xFF);
                    }
                    break;

                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            // 数字格式错误（如包含非数字字符），返回null
            return null;
        }

        return result;
    }

    /**
     * 解析多级反向代理中的真实客户端IP（解决Nginx代理IP拼接问题）
     * 场景：X-Forwarded-For = 客户端IP, 代理1IP, 代理2IP
     * 逻辑：提取第一个非unknown、非内网的IP作为真实客户端IP
     * @param ip 拼接的IP字符串（如：114.114.114.114, 192.168.1.100, 172.17.0.1）
     * @return 真实客户端IP
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 无多级代理，直接返回原IP
        if (ObjectUtil.isEmpty(ip) || ip.indexOf(",") == -1) {
            return ip;
        }

        // 拆分IP数组并去除首尾空格
        String[] ipArray = ip.trim().split(",");
        // 遍历所有IP段，提取第一个有效客户端IP
        for (String segmentIp : ipArray) {
            String cleanIp = segmentIp.trim(); // 去除IP前后空格（关键：解决Nginx拼接的空格问题）
            // 非unknown、非内网IP → 判定为客户端真实IP
            if (!isUnknown(cleanIp) && !internalIp(cleanIp)) {
                return cleanIp;
            }
        }

        // 所有IP段均为unknown/内网，返回最后一个（兜底）
        return ipArray[ipArray.length - 1].trim();
    }

    /**
     * 检测字符串是否为unknown（适配HTTP请求头的默认值）
     * @param checkString 待检测字符串（如X-Forwarded-For的值）
     * @return true=未知，false=有效
     */
    public static boolean isUnknown(String checkString) {
        return ObjectUtil.isNull(checkString)
                || checkString.length() == 0
                || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 获取服务器本机IP（避免UnknownHostException异常）
     * @return 本机IP，异常返回127.0.0.1
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * 获取服务器本机主机名
     * @return 主机名，异常返回"未知"
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "未知";
        }
    }
}