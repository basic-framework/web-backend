package com.zl.common.utils.ipUtils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;

import com.zl.common.constant.CommonConstant;
import com.zl.common.utils.httpUtils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 /**
 * IP解析工具类
 * @Auther:GuihaoLv
 */
public class AddressUtil {
    private static final Logger log = LoggerFactory.getLogger(AddressUtil.class);

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip) {
        // 内网不查询
        if (IPUtil.internalIp(ip)) {
            return "内网IP";
        }
        //ture：获取ip地址开关
        if (true) {
            try {
                String rspStr = HttpUtil.sendGet(IP_URL, "ip=" + ip + "&json=true", CommonConstant.GBK);
                if (ObjectUtil.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSONObject.parseObject(rspStr);
                String region = obj.getString("pro");
                String city = obj.getString("city");
                return String.format("%s %s", region, city);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return UNKNOWN;
    }
}

