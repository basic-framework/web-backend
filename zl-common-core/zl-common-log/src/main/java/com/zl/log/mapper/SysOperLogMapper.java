package com.zl.log.mapper;

import com.zl.log.event.OperLogEvent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysOperLogMapper {

    @Insert("INSERT INTO sys_oper_log (" +
            "title, business_type, method, request_method, operator_type, " +
            "oper_name, dept_name, oper_url, oper_ip, oper_location, " +
            "oper_param, json_result, status, error_msg, cost_time" +
            ") VALUES (" +
            "#{title}, #{businessType}, #{method}, #{requestMethod}, #{operatorType}, " +
            "#{operName}, #{deptName}, #{operUrl}, #{operIp}, #{operLocation}, " +
            "#{operParam}, #{jsonResult}, #{status}, #{errorMsg}, #{costTime}" +
            ")")
    void insertSelective(OperLogEvent operLog);

}
