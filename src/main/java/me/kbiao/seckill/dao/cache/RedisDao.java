package me.kbiao.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.kbiao.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * Created by kangb on 2016/5/28.
 */
public class RedisDao {


    private final Logger logger = LoggerFactory.getLogger(this.getClass()) ;
    private  final JedisPool jedisPool ;

    public RedisDao(String ip , int port){
        jedisPool = new JedisPool(ip, port) ;

    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class) ;

    public Seckill getSeckill(long seckillId){

        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+ seckillId ;
                //并没有实现内部序列化操作
                //get -> byte[] -> 反序列化  -> 对象（Seckill）
                //采用自定义序列化方式  protoStuff  : POJO

                byte[] bytes = jedis.get(key.getBytes()) ;

                //缓存中获取到了
                if (bytes != null){
                    //创建一个空对象
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    //seckill 被反序列化
                    return  seckill ;
                }

             }
            finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        return null ;
    }


    public String putSeckill(Seckill seckill){

        //set Object (Seckill) -》序列化  - > byte[] ;

        try {
            Jedis jedis = jedisPool.getResource() ;
            try {
                String key = "seckill:" + seckill.getSeckillId() ;
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)) ;

//                超时缓存
                int timeout = 60 * 60 ; //一小时
                String result = jedis.setex(key.getBytes(), timeout ,bytes) ;
                return result ;
            }finally {
                jedis.close();
            }
        }catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
        return null;

    }
}
