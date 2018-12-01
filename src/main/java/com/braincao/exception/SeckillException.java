package com.braincao.exception;

/**
 * @FileName: RepeatKillException
 * @Author: braincao
 * @Date: 2018/11/27 15:01
 * @Description: 用户秒杀操作时抛出的异常:秒杀相关业务异常
 *
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}