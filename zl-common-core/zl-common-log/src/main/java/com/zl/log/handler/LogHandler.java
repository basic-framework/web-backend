package com.zl.log.handler;

import com.zl.common.utils.ipUtils.AddressUtil;
import com.zl.common.utils.springUtils.SpringUtil;
import com.zl.log.event.OperLogEvent;
import com.zl.log.service.SysOperLogService;

import java.util.TimerTask;

public class LogHandler {


    /**
     * 记录操作日志
     * @param operLog
     * @return
     */
    public static TimerTask recordOper(OperLogEvent operLog) {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtil.getRealAddressByIP(operLog.getOperIp()));
                SpringUtil.getBean(SysOperLogService.class).insertOperlog(operLog);
            }
        };
    }
}
