package com.braincao.service;

import com.braincao.dto.Exposer;
import com.braincao.dto.SeckillExecution;
import com.braincao.entity.Seckill;
import com.braincao.exception.RepeatKillException;
import com.braincao.exception.SeckillCloseException;
import com.braincao.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：Service层接口应站在使用者角度设计接口
 *
 */
public interface SeckillService {

    /**
     * 查询所有秒杀商品
     * @param void
     * @return List<Seckill>
     */
    List<Seckill> getSeckillList();

    /**
     * 根据id查询一个秒杀商品
     * @param seckillId
     * @return Seckill
     */
    Seckill getById(long seckillId);

    /**
     * 用户根据id查询该秒杀商品的秒杀接口地址时:
     * 秒杀开启时输出秒杀接口地址，未开启时输出系统时间+秒杀开启时间
     * @param seckillId
     * @return  秒杀接口地址。用dto传输层的实体来封装秒杀接口地址,dto方便web层拿到秒杀暴露接口相关数据
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId,userPhone,md5
     *        md5用来验证是否是同一用户操作，如果md5变了，说明用户被篡改
     * @return dto传输层的实体来封装秒杀操作的返回实体，包含成功、失败
     * 当秒杀失败时输出自定义运行期异常RepeatKillException
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, SeckillCloseException, RepeatKillException;


    /**
     * 执行秒杀操作(存储过程)
     * 将事务放在mysql端的存储过程来操作，避免gc和网络延时，优化高并发
     * @param seckillId,userPhone,md5
     *        md5用来验证是否是同一用户操作，如果md5变了，说明用户被篡改
     * @return dto传输层的实体来封装秒杀操作的返回实体，包含成功、失败
     * 当秒杀失败时输出自定义运行期异常RepeatKillException
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
