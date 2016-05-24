package me.kbiao.seckill.exception;

/**
 * Created by kangb on 2016/5/23.
 */
public class SeckillCloseException extends SeckillException {


    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
