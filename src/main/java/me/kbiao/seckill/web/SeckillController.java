package me.kbiao.seckill.web;

import me.kbiao.seckill.dto.SeckillExcution;
import me.kbiao.seckill.dto.SeckillResult;
import me.kbiao.seckill.entity.Seckill;
import me.kbiao.seckill.enums.SeckillStatEnum;
import me.kbiao.seckill.exception.RepeatKillException;
import me.kbiao.seckill.exception.SeckillCloseException;
import me.kbiao.seckill.service.SeckillService;
import me.kbiao.seckill.dto.Exposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by kangb on 2016/5/24.
 */

@Controller     //放入spring容器中
@RequestMapping("/seckill")   // url: /模块/资源/{id}/细分/list
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService ;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list (Model model){

        List<Seckill> list = seckillService.getSeckillList() ;
        model.addAttribute("list",list) ;
        return "list" ;
    }


    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId")Long seckillId, Model model){
        if (seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill" ,seckill);
        return "detail";
    }
    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;chartset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") long seckillId){

        SeckillResult<Exposer> result ;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId) ;
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }

        return result ;


    }


    @RequestMapping(value = "/{seckillId}/{md5}/execution",
                    method = RequestMethod.POST,
                    produces = {"application/json;chartset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExcution> execute(@PathVariable("seckillId") long seckillId,
                                                  @PathVariable("md5") String md5,
                                                  @CookieValue(value = "killPhone" ,required = false) Long phone){

       if (phone == null) {
           return new SeckillResult<SeckillExcution>(false,"用户未注册");
       }


        try {
            SeckillExcution excution = seckillService.executeSeckill(seckillId,phone,md5 );
            return new SeckillResult<SeckillExcution>(true,excution) ;

        }catch (RepeatKillException repeartException){
            SeckillExcution excution = new SeckillExcution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExcution>(true,excution) ;
        }catch (SeckillCloseException closeException){
            SeckillExcution excution = new SeckillExcution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExcution>(true,excution) ;
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            SeckillExcution excution = new SeckillExcution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExcution>(true,excution) ;
        }


    }
    @RequestMapping(value = "time/now",method = RequestMethod.GET )
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
}
