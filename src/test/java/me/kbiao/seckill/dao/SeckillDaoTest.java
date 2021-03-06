package me.kbiao.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import me.kbiao.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

/**
 * Created by kangb on 2016/5/22.
 */

//配置spring和junit整合，junit启动时加载springIoc容器  spring-test,junit

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void testReduceNumber() throws Exception {
        Date killTime = new Date();
       System.out.println( seckillDao.reduceNumber(1000L,killTime));

    }

    @Test
    public void testQueryById() throws Exception {
        long id = 1000 ;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void testQueryAll() throws Exception {

        List<Seckill> seckills = seckillDao.queryAll(0,100);
        for (Seckill seckill : seckills){
            System.out.println(seckill);
        }
    }
}