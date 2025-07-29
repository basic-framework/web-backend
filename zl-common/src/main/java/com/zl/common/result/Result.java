package com.zl.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

 /**
 * 统一返回结果
 * @param <T>
 * @Auther GuihaoLv
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result<T>  implements Serializable{
    private Integer code; //状态码
    private String message; //消息
    private T data; //响应数据


    /**
     * 无响应数据的成功响应
     * @return
     * @param <T>
     */
    public static<T> Result<T> success(){
        Result<T> result=new Result<>();
        result.setCode(200);
        return result;
    }

    /**
     * 带响应数据的成功响应
     * @param data
     * @return
     * @param <T>
     */
    public static <T>Result<T> success(T data){
        Result<T> result=new Result<>();
        result.setCode(200);
        result.setData(data);
        return result;
    }


    /**
     * 响应失败
     * @param message
     * @return
     * @param <T>
     */
    public static <T> Result<T> fail(String message){
        Result<T> result=new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }


}