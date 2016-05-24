package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by kangb on 2016/5/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {

    @Resource
    private SuccessKillDao successKillDao;


    @Test
    public void testInsertSuccessKilled() throws Exception {
        long id = 1000L;
        long phone = 1234587541L;
        System.out.println(successKillDao.insertSuccessKilled(id,phone));
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {

    }
}