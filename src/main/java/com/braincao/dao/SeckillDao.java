package com.braincao.dao;

import com.braincao.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @FileName: SeckillDao
 * @Author: braincao
 * @Date: 2018/11/26 17:04
 * @Description: 对应实体Seckill的dao层接口
 */

public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId, createTime
     * @return int表示更新后的影响记录数，如果返回0则表示更新失败
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("createTime") Date createTime);

    /**
     * 查询：根据id查询秒杀商品(一个Seckill实体类)
     * @param
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 查询：根据偏移量查询秒杀商品列表(多个Seckill实体类)
     * @param
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 存储过程执行秒杀:插入购买明细+减库存(mysql端进行)
     * @param
     * @return
     */
    void killByProcedure(Map<String, Object> paramMap);
}