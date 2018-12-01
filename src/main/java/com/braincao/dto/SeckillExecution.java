package com.braincao.dto;

import com.braincao.entity.SuccessKilled;
import com.braincao.enums.SeckillStateEnum;

/**
 * @FileName: SeckillExecution
 * @Author: braincao
 * @Date: 2018/11/27 14:53
 * @Description: 执行秒杀操作后返回值，用dto传输层的实体来封装
 */

public class SeckillExecution {
    //秒杀操作的对象id
    private long seckillId;

    //秒杀操作后的状态标示
     private int state;

     //秒杀操作后的状态描述
     private String stateInfo;

     //秒杀成功的对象
     private SuccessKilled successKilled;

     //秒杀成功后的初始化
    public SeckillExecution(long seckillId, SeckillStateEnum seckillStateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    //秒杀失败后的初始化
    public SeckillExecution(long seckillId, SeckillStateEnum seckillStateEnum) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}