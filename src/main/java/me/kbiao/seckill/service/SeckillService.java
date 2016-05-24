package me.kbiao.seckill.service;

import me.kbiao.seckill.dto.Exposer;
import me.kbiao.seckill.dto.SeckillExcution;
import me.kbiao.seckill.entity.Seckill;
import me.kbiao.seckill.exception.RepeatKillException;
import me.kbiao.seckill.exception.SeckillCloseException;
import me.kbiao.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by kangb on 2016/5/23.
 *
 * 业务接口：站在使用者角度设计接口
 * 三个方面：方法定义的粒度，参数，返回类型（return 类型友好/异常）
 */
public interface SeckillService {

    List<Seckill> getSeckillList();


    Seckill getById(long seckillId);


    /**
     * 秒杀开启时候输出秒杀接口地址，
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExcution executeSeckill(long seckillId, long userPhone , String md5)
        throws SeckillException ,RepeatKillException ,SeckillCloseException;
}
