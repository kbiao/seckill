package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Created by kangb on 2016/5/21.
 */
public interface SeckillDao {


    /**
     *
     * @param seckillId
     * @param killTime
     * @return  如果影响行数>1，表示更新的记录行数
     * java运行时会把参数换成arg0 arg1等
     */

    int reduceNumber(@Param("seckillId")long seckillId , @Param("killTime")Date killTime);

    /**
     *
     * @param seckillId
     * @return
     */

    Seckill queryById(long seckillId);


    List<Seckill> queryAll(@Param("offset") int offset ,@Param("limit")  int limit) ;
}
