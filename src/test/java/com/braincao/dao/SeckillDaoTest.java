package com.braincao.dao;
import com.braincao.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

//配置spring和junit整合(@RunWith)，这样junit启动时会加载springIOC容器，拿到相应的bean
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件(这里测试dao，是spring-dao.xml)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //用resource注解来注入seckillDao依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillDao.queryAll(0,100);
        //这里注意：java没有保存形参的记录，多个参数时会将offset->arg0，
        // 因此在dao层接口中参数前需要加@Param("offset")，否则会报错
        for(Seckill seckill: seckills){
            System.out.println(seckill);
        }
    }

    @Test
    public void reduceNumber() {
        Date createTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L,createTime);
        System.out.println(updateCount);
    }
}