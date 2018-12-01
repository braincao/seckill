package com.braincao.service;

import com.braincao.dto.Exposer;
import com.braincao.dto.SeckillExecution;
import com.braincao.entity.Seckill;
import com.braincao.exception.RepeatKillException;
import com.braincao.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

//配置spring和junit整合(@RunWith)，这样junit启动时会加载springIOC容器，拿到相应的bean
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件(这里测试dao，是spring-dao.xml)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    //日志，因为测试service方法时存在秒杀失败抛出异常，所以把这些输出放在日志中
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //用@Autowired注解来注入seckillService依赖
    @Autowired
    private SeckillService seckillService;

    //查询所有秒杀商品
    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("list={}", seckillList);//{}是占位符，把后面的object放到占位符中
    }

    //根据id查询一个秒杀商品
    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }


    //集成测试代码完整逻辑，将接口地址暴露和执行秒杀操作两个一起集成测试，
    // 这样md5值就中间可用，可重复执行测试
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}", exposer);
            long userPhone = 15652965935L;
            String md5 = exposer.getMd5();
            try{
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, userPhone, md5);
                logger.info("seckillResult={}", seckillExecution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }
        else{
            //秒杀未开启
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    //测试存储过程的秒杀执行
    public void executeSeckillProcedure(){
        long seckillId = 1000;
        long phone = 15652965953L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
             String md5 = exposer.getMd5();
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info("seckillResult={}", seckillExecution.getStateInfo());
        }
    }
}