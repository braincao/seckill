package com.braincao.service.impl;

import com.braincao.dao.SeckillDao;
import com.braincao.dao.SuccessKilledDao;
import com.braincao.dao.cache.RedisDao;
import com.braincao.dto.Exposer;
import com.braincao.dto.SeckillExecution;
import com.braincao.entity.Seckill;
import com.braincao.entity.SuccessKilled;
import com.braincao.enums.SeckillStateEnum;
import com.braincao.exception.RepeatKillException;
import com.braincao.exception.SeckillCloseException;
import com.braincao.exception.SeckillException;
import com.braincao.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @FileName: SeckillServiceImpl
 * @Author: braincao
 * @Date: 2018/11/27 15:40
 * @Description: Service层接口的实现
 * md5:对任意长度字符串返回一个特定长度的加密编码，不可逆
 * 所有编译期异常转化为运行期异常，这样spring声明式事务会帮我们做roll back
 */
//@Component / @Service / @Dao / @Controller
@Service
public class SeckillServiceImpl implements SeckillService {

    //slf4j规范接口的日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //spring注入Service依赖：spring注入的DAO层对象 @Autowired / @Resource / @Inject
    @Autowired
    private SeckillDao seckillDao;

    //spring注入的DAO层对象
    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //盐值字符串，用于混淆md5。越复杂越好，用户猜不到
    private final String slat = "asdc#$!EFSD$#%$GWVDSQ#!$#%$#T~~@#$^GV";

    //对seckillId生成md5的过程
    private String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());//用base二进制生成md5
        return md5;
    }

    //查询所有秒杀商品
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,100);
    }

    //根据id查询一个秒杀商品
    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    //用户根据id查询该秒杀商品的秒杀接口地址时:
    //秒杀开启时输出秒杀接口地址，未开启时输出系统时间+秒杀开启时间
    //高并发优化：将秒杀接口地址放到redis缓存服务器中
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //拿到目标的秒杀商品
        //将这句话进行优化:1.先访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            //2.访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(seckill==null) {
                return new Exposer(false, seckillId);
            }else{
                //3.放入redis
                redisDao.putSeckill(seckill);
            }
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date date = new Date(); //系统的当前时间

        //2.如果秒杀未开始或已经结束
        if(date.getTime()<startTime.getTime() || date.getTime()>endTime.getTime()){
            return new Exposer(false,seckillId,date.getTime(),startTime.getTime(),endTime.getTime());
        }
        //3.如果秒杀开始，暴露接口地址
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    //执行秒杀操作，使用注解来声明式事务
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, SeckillCloseException, RepeatKillException {
        //md5验证，如果不对则抛出异常，秒杀失败，防止用户私自篡改
        if(md5==null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑:减库存 + 记录购买行为，对里面抛出的异常进行try/catch并记录到日志，汇总后向外只抛一个总异常就好
        Date date = new Date();
        try{
            //优化高并发：记录购买行为在前
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if(insertCount<=0){//重复秒杀
                throw new RepeatKillException("Repeat seckill");
            }else{
                //优化高并发：减库存在后，热点商品竞争。因为减库存需要行级锁，这样能减少行级锁持有时间
                int updateCount = seckillDao.reduceNumber(seckillId, date);
                if(updateCount<=0){//减库存失败，秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                }else{
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch(SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            //所有编译期异常转化为运行期异常，这样spring声明式事务会帮我们做roll back
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }

    //执行秒杀操作by存储过程，将事务放在mysql端的存储过程来操作，避免gc和网络延时，优化高并发
    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5){
        //md5验证，如果不对则抛出异常，秒杀失败，防止用户私自篡改
        if(md5==null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑:减库存 + 记录购买行为，对里面抛出的异常进行try/catch并记录到日志，汇总后向外只抛一个总异常就好
        Date date = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", date);
        map.put("result", null);

        //执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {//秒杀成功
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}