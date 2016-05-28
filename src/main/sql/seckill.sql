-- 秒杀执行存储过程
-- ;

DELIMITER $$   -- console ; 转换为 $$
-- 定义存储过程  参数输入IN  输出 OUT
-- row_count(); 返回上一条修改类型SQL(delect,insert,update)的影响行数
-- row_count(); 0未修改数据  》 表示修改的行数  《 0  错误或未执行修改SQL
CREATE PROCEDURE `seckill`.`execute_seckill`
  (IN v_seckill_id BIGINT , IN  v_phone BIGINT,
    IN v_kill_time TIMESTAMP , OUT r_result INT)
  BEGIN 
    DECLARE inset_count INT DEFAULT 0;
    START TRANSACTION ;
    INSERT IGNORE  INTO success_killed(
      seckill_id, user_phone, creat_time
    ) VALUES (v_seckill_id,v_phone,v_kill_time);
    SELECT row_count() INTO inset_count ;

    IF (inset_count = 0) THEN
      ROLLBACK ;
      SET  r_result = -1 ;
    ELSEIF (inset_count < 0) THEN
      ROLLBACK ;
      SET r_result = -2 ;
    ELSE
      UPDATE seckill
        SET number = number - 1
      WHERE seckill_id = v_seckill_id
        AND end_time > v_kill_time
        AND start_time < v_kill_time
        AND number > 0;
      SELECT row_count() INTO inset_count ;
      IF (inset_count = 0) THEN
        ROLLBACK ;
        SET r_result = 0 ;
      ELSEIF (inset_count < 0) THEN
        ROLLBACK ;
        SET r_result = -2 ;
      ELSE
        COMMIT ;
      END IF ;
    END IF ;
  END ;

$$

-- 存储过程结束
DELIMITER  ;
SET @r_result = -3;
CALL execute_seckill(1003,13502178891,now(),@r_result);

SELECT @r_result;

-- 存储过程
-- 1.存储过程优化：事务航迹锁持有的时间
-- 2.不要过度依赖存储过程
-- 3.简单逻辑可以应用存储过程
-- 4.QPS ：一个秒杀单6000/qps