package com.braincao.dto;

/**
 * @FileName: Exposer
 * @Author: braincao
 * @Date: 2018/11/27 14:41
 * @Description: 秒杀开启时暴露的秒杀接口地址
 */

public class Exposer {

    //秒杀是否开启
    private boolean exposed;

    //一种加密措施
    private String md5;

    //秒杀商品id
    private long seckillId;

    //系统当前时间(毫秒)
    private long now;

    //秒杀开启时间
    private long start;

    //秒杀结束时间
    private long end;

    //以下3个构造方法：不同的构造方法方便对象初始化
    //1.如果秒杀开始，返回这个
    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    //2.如果秒杀未开始或已经结束，返回这个
    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    //3.用户想要秒杀的商品为空时返回这个
    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}