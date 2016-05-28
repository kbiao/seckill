package me.kbiao.seckill.service.impl;

import me.kbiao.seckill.dao.cache.RedisDao;
import me.kbiao.seckill.enums.SeckillStatEnum;
import me.kbiao.seckill.exception.SeckillCloseException;
import me.kbiao.seckill.service.SeckillService;
import me.kbiao.seckill.dao.SeckillDao;
import me.kbiao.seckill.dao.SuccessKillDao;
import me.kbiao.seckill.dto.Exposer;
import me.kbiao.seckill.dto.SeckillExcution;
import me.kbiao.seckill.entity.Seckill;
import me.kbiao.seckill.entity.SuccessKilled;
import me.kbiao.seckill.exception.RepeatKillException;
import me.kbiao.seckill.exception.SeckillException;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import sun.util.resources.cldr.fr.CalendarData_fr_RE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kangb on 2016/5/23.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass()) ;

    @Autowired
    private SeckillDao seckillDao ;

    @Autowired
    private SuccessKillDao successKillDao ;

    @Autowired
    private RedisDao redisDao ;


    //混淆MD5
    public final String salt = "asujikdhfgq342y6t785&*6*(&E489235/-*39851";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //缓存优化
        /**getFrom cache    超市的基础上维护一致性
         * if null
         * get db
         * else
         * put cache
         * logic
         * 不应该放入业务逻辑
         */
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //不存在则访问数据库
            seckill = getById(seckillId);
            if (seckill == null){
                return  new Exposer(false ,seckillId) ;
            }else {
                //放入redis
                redisDao.putSeckill(seckill);
            }

        }

        Date startTime = seckill.getStartTime() ;
        Date endTime = seckill.getEndTime() ;
        Date now = new Date() ;
        if (now.getTime() <startTime.getTime()
                || now.getTime() >endTime.getTime()){
            return new Exposer(false,seckillId ,now.getTime() ,startTime.getTime() ,endTime.getTime());
        }
        String md5 = getMD5(seckillId) ;
        return new Exposer(true,md5 ,seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId +"/" +salt ;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
/**
 * 使用注解控制事务方法的优点：
 * 1.开发团队达成一致的约定，明确标注事务方法的标称风格。
 * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作 RPC/HTTP
 * 3.不是所有的方法都需要事务，如只有一条修改操作，制度操作不需要事务控制。
 */
    public SeckillExcution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite") ;
        }
        //执行秒杀逻辑：件库存，记录购买行为
        Date nowTime = new Date();
        try {

            //记录购买行为
            int insertCount = successKillDao.insertSuccessKilled(seckillId, userPhone);

            //唯一性 seckillId, userPhone
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减库存，热点商品竞争  /**简单优化部分  网络和GC影响*/
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录  秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExcution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }

            }


        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            //所有编译期异常转化为运行期异常
            throw new SecurityException("seckill inner error:" + e.getMessage());
        }


    }


    public SeckillExcution executeSeckillProcedure(long seckillId, long userPhone, String md5)  {

        if (md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExcution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date() ;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("seckillId",seckillId) ;
        map.put("phone" ,userPhone) ;
        map.put("killTime" ,killTime);
        map.put("result",null) ;
//        执行结束后map被赋值
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map,"result", -2);
            if (result == 1){
                SuccessKilled sk = successKillDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExcution(seckillId,SeckillStatEnum.SUCCESS,sk);
            }else {
                return new SeckillExcution(seckillId,SeckillStatEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExcution(seckillId,SeckillStatEnum.INNER_ERROR);
        }


    }
}
