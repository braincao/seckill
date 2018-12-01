package com.braincao.dao.cache;

import com.braincao.entity.Seckill;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @FileName: RedisDao
 * @Author: braincao
 * @Date: 2018/11/30 10:27
 * @Description: 高并发优化:读取/存取redis操作。这个dao就不做接口了，直接实现即可
 */

public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    //java存取redis采用RuntimeSchema序列化工具类高效转化
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip, port);
    }

    //读取redis:根据id读取Seckill
    public Seckill getSeckill(long seckillId){
        //redis操作逻辑
        try{
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                //redis并没有实现内部序列化操作
                //get->byte[] ->反序列化 ->Object[Seckill]
                //采用自定义序列化工具protostuff进行java存取redis
                byte[] bytes = jedis.get(key.getBytes());

                if (bytes != null) {
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    //反序列化seckill
                    return seckill;
                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    //存取redis:将Seckill存储到redis中
    public String putSeckill(Seckill seckill){
        //set Onject[Seckill] -> 序列化 -> byte[]
        //redis操作逻辑
        try{
            Jedis jedis = jedisPool.getResource();
            try{
                String key = "seckill:" + seckill.getSeckillId();
                //redis并没有实现内部序列化操作
                //get->byte[] ->反序列化 ->Object[Seckill]
                //采用自定义序列化工具protostuff进行java存取redis
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //超时缓存1h
                int timeout = 60*60;

                //redis存储，result是存储成功或失败的结果
                String result = jedis.setex(key.getBytes(),timeout,bytes);

                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}