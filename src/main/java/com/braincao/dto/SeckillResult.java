package com.braincao.dto;

/**
 * @FileName: SeckillResult
 * @Author: braincao
 * @Date: 2018/11/28 21:13
 * @Description: dto层就是web层到service层之间的传递
 * 所有的ajax请求返回类型，封装json结果，使用泛型
 */

public class SeckillResult<T> {

    private boolean success;

    private T data;

    private String error;

    //有数据时
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    //没数据时
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }
}