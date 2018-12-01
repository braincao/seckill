package com.braincao.enums;

/**
 * @FileName: SeckillStateEnum
 * @Author: braincao
 * @Date: 2018/11/27 16:41
 * @Description: 使用枚举表示seckill秒杀状态的常量数据。
 */

public enum SeckillStateEnum {
    SUCCESS(1,"秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3,"数据篡改");

    //状态
    private int state;

    //状态说明
    private String stateInfo;

    //构造方法赋值
    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStateEnum stateOf(int index){
        for(SeckillStateEnum state: values()){
            if(state.getState() == index){
                return state;
            }
        }
        return null;
    }
}