package com.braincao.dao;

import com.braincao.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

//配置spring和junit整合(@RunWith)，这样junit启动时会加载springIOC容器，拿到相应的bean
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件(这里测试dao，是spring-dao.xml)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    //用resource注解来注入seckillDao依赖
    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() {
        System.out.println(successKilledDao.insertSuccessKilled(1003L, 15652965943L));
    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1004, 15652965944L);
        System.out.println(successKilled);
        System.out.println("======\n" + successKilled.getSeckill());
    }
}