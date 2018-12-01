package com.braincao.exception;

/**
 * @FileName: RepeatKillException
 * @Author: braincao
 * @Date: 2018/11/27 15:01
 * @Description: 用户秒杀操作时抛出的异常:秒杀关闭异常
 * 这里要抛出运行期异常，所以需要继承RuntimeException而不是编译器异常Exception
 */

public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}