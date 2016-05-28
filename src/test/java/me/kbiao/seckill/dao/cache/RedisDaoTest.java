package me.kbiao.seckill.dao.cache;

import me.kbiao.seckill.dao.SeckillDao;
import me.kbiao.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by kangb on 2016/5/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    private long id = 1000 ;
    @Autowired
    private RedisDao redisDao ;

    @Autowired
    private SeckillDao seckillDao ;

    @Test
    public void testGetSeckill() throws Exception {

        Seckill seckill = redisDao.getSeckill(id) ;
        if (seckill == null){
            seckill = seckillDao.queryById(id) ;
            if (seckill != null){}
                String result = redisDao.putSeckill(seckill) ;
                System.out.println(result);
                seckill = redisDao.getSeckill(id);
                System.out.println(seckill);
        }

    }

    @Test
    public void testPutSeckill() throws Exception {

    }
}