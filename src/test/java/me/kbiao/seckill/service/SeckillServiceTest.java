package me.kbiao.seckill.service;

import me.kbiao.seckill.entity.Seckill;
import me.kbiao.seckill.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import me.kbiao.seckill.dto.Exposer;
import me.kbiao.seckill.dto.SeckillExcution;
import me.kbiao.seckill.exception.RepeatKillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by kangb on 2016/5/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})

public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void testGetById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id) ;
        logger.info("seckill= {}", seckill);
    }


    //继承测试代码完整逻辑，注意可以重复执行
    @Test
    public void testSeckilLogic() throws Exception {
        long id = 1000L ;
        Exposer exposer = seckillService.exportSeckillUrl(id) ;
        logger.info("exposer={}",exposer);
        if (exposer.isExposed()){

            long phone = 18702528641L;
            String md5 = exposer.getMd5();
            try {

                SeckillExcution excution = seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}", excution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else {
            logger.warn("exposer={}",exposer);
        }
        /**
         * =Exposer{exposed=true,
         * md5='bbe60ed85740b3f9937eacdc2293dec8',
         * seckillId=1000, now=0, start=0, end=0}
         */

    }

    @Test
    public void testExecuteSeckill() throws Exception {
        long id = 1000L ;
        long phone = 18702528641L;
        String md5 = "bbe60ed85740b3f9937eacdc2293dec8";
        try {

            SeckillExcution excution = seckillService.executeSeckill(id, phone, md5);
            logger.info("result={}", excution);
        }catch (RepeatKillException e){
            logger.error(e.getMessage());
        }catch (SeckillCloseException e){
            logger.error(e.getMessage());
        }

    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1003 ;
        long phone = 15845654641l;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            String md5 = exposer.getMd5() ;
            SeckillExcution seckillExcution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(seckillExcution.getStateInfo());
        }
    }
}