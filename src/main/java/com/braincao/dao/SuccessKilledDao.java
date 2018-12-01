package com.braincao.dao;

import com.braincao.entity.Seckill;
import com.braincao.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @FileName: SeckillDao
 * @Author: braincao
 * @Date: 2018/11/26 17:04
 * @Description: 对应实体SuccessKilledDao的dao层接口
 */

public interface SuccessKilledDao {

    /**
     * 插入秒杀购买明细，可过滤重复(防止一个用户多次秒杀)
     * @param seckillId, userPhone
     * @return int表示更新后的影响记录数，如果返回0则表示更新失败
     */
    int insertSuccessKilled(@Param("seckillId")long seckillId,@Param("userPhone") long userPhone);

    /**
     * 查询：根据seckillId查询SuccessKilled实体(也携带Seckill秒杀商品对象实体)
     * @param seckillId
     * @return SuccessKilled
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);
}