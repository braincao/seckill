package com.braincao.dao.cache;

import com.braincao.dao.SeckillDao;
import com.braincao.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

//配置spring和junit整合(@RunWith)，这样junit启动时会加载springIOC容器，拿到相应的bean
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件(这里测试dao，是spring-dao.xml)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    private long id = 1001;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillDao seckillDao;

    @Test
    public void getSeckill() throws Exception{
        //get and put and get
        Seckill seckill = redisDao.getSeckill(id);
        if(seckill == null){
            seckill = seckillDao.queryById(id);
            if(seckill!=null){
                String result = redisDao.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }

    @Test
    public void getSeckill1() {
    }
}