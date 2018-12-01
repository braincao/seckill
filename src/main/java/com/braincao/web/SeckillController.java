package com.braincao.web;

import com.braincao.dto.Exposer;
import com.braincao.dto.SeckillExecution;
import com.braincao.dto.SeckillResult;
import com.braincao.entity.Seckill;
import com.braincao.enums.SeckillStateEnum;
import com.braincao.exception.RepeatKillException;
import com.braincao.exception.SeckillCloseException;
import com.braincao.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @FileName: SeckillController
 * @Author: braincao
 * @Date: 2018/11/28 20:16
 * @Description: 实现Restful接口的Controller层：接受请求与参数，跳转页面的控制，并且返回参数都是已经dto传输层封装好的类型
 */
@Controller
//url: 模块/资源/{id}/细分
@RequestMapping("/seckill")
public class SeckillController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    //获取所有秒杀商品列表页
    @RequestMapping(value="/list", method = RequestMethod.GET)
    public String list(Model model){
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list", seckillList);
        //jsp页面 + model数据 = ModelAndView

        //跳转到/WEB-INF/jsps/list.jsp
        return "list";
    }

    //获取单个秒杀商品的详情页
    @RequestMapping(value="/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId==null){
            return "redirect:/seckill/list"; //重定向
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill==null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        //jsp页面 + model数据 = ModelAndView

        //跳转到/WEB-INF/jsps/detail.jsp
        return "detail";
    }

    //获取秒杀地址。接收ajaxPOST请求，返回json格式。produces解决乱码问题
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId")long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<>(true, exposer);
        }catch (Exception e){
            logger.error(e.getMessage());
            result = new SeckillResult<>(false,e.getMessage());
        }

        return result;
    }

    //执行秒杀的ajax请求，返回json。CookieValue是通过cookie获取手机号，如果不存在也不报错，交给程序try/catch
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> exposer(
            @PathVariable("seckillId")Long seckillId,
            @PathVariable("md5") String md5,
            @CookieValue(value = "killPhone", required = false)Long userPhone){

        if(userPhone==null){
            return new SeckillResult<SeckillExecution>(false,"用户未注册");
        }
        try{
            //SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,userPhone,md5);
            //不用上面这个了，用下面优化后的：将秒杀事务放在mysql端(存储过程)
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId,userPhone,md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (RepeatKillException e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (SeckillCloseException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }
    }

    //前端获取系统时间，ajax请求
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }

}