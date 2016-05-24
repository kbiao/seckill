package me.kbiao.seckill.exception;

/**
 * Created by kangb on 2016/5/23.
 */
public class SeckillException extends RuntimeException {


    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
