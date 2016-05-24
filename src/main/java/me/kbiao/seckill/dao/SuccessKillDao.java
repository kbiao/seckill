package me.kbiao.seckill.dao;

import me.kbiao.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * Created by kangb on 2016/5/21.
 */
public interface SuccessKillDao {

    int insertSuccessKilled(@Param("seckillId") long seckillId ,@Param("userPhone") long userPhone) ;

    /**
     * 根据Id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */

    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
