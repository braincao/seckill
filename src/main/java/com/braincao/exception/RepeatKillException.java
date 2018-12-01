package com.braincao.exception;

/**
 * @FileName: RepeatKillException
 * @Author: braincao
 * @Date: 2018/11/27 15:01
 * @Description: 用户秒杀操作时抛出的异常:重复秒杀异常
 * 这里要抛出运行期异常，所以需要继承RuntimeException而不是编译器异常Exception
 */

public class RepeatKillException extends SeckillException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}