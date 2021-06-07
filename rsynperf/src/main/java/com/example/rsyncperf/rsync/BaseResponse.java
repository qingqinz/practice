package com.example.rsyncperf.rsync;

import java.io.Serializable;

/**
 * @ClassName BaseResponse
 * @Description TODO: 通用的响应封装类
 * @Author lidong Han
 * @Date 2019/11/2 11:06
 * @Version 1.0
 **/
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID=1L;

    private Integer code;
    private String msg;
    private T data;
    //重试标志位。false:即便重试，也不能成功;true:重试后，可能成功。常用于标识异常是否可以通过重试得以克服。
    private boolean retryable = true;

    public BaseResponse() {
    }

    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
    }

    public BaseResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(StatusCode statusCode, T data) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = data;
    }

    public BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public static BaseResponse success(Object object) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        response.setData(object);
        return response;
    }

    public static BaseResponse success() {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        return response;
    }


    public static BaseResponse fail(Object object) {
        BaseResponse response = new BaseResponse(StatusCode.Fail);
        response.setData(object);
        return response;
    }


    public static boolean isSuccess(BaseResponse response) {
        return response != null && StatusCode.Success.getCode().equals(response.getCode());
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
