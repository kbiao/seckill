package org.seckill.exception;

/**
 * 重复秒杀异常  （运行期异常）
 * Spring 只能回滚运行期异常的事务
 * Created by kangb on 2016/5/23.
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
