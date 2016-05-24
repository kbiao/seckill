-- 数据库初始化脚本


-- 创建数据库

CREATE DATABASE seckill;

use seckill;

CREATE TABLE seckill(
 `seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` VARCHAR(120) NOT NULL COMMENT '商品名称',
  `number` INT NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP NOT NULL COMMENT '秒杀开始时间',
  `end_time` TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
  `creat_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_creat_time(creat_time)

)ENGINE = InnoDB AUTO_INCREMENT = 1000 DEFAULT CHAR SET = utf8 COMMENT = '秒杀库存表';


-- 初始化数据
  INSERT INTO
    seckill(name, number, start_time, end_time, creat_time)
    VALUES
      ('1000元秒杀iphone6',100,'2016-05-21 00:00:00','2016-05-22 00:00:00'),
      ('500元秒杀ipad2',200,'2016-05-21 00:00:00','2016-05-22 00:00:00'),
      ('300元秒杀小米4',300,'2016-05-21 00:00:00','2016-05-22 00:00:00'),
      ('1000元秒杀iphone6',100,'2016-05-21 00:00:00','2016-05-22 00:00:00'),
      ('1000元秒杀iphone6',100,'2016-05-21 00:00:00','2016-05-22 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息
CREATE TABLE success_killed(
  `seckill_id` BIGINT NOT NULL COMMENT '秒杀商品id',
  `user_phone` VARCHAR(20) NOT NULL COMMENT '用户手机号',
  `state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态表示：-1：无效 0：成功 1：已付款',
  `creat_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id,user_phone),
  KEY idx_create_time(creat_time)
)ENGINE = InnoDB DEFAULT CHAR SET = utf8 COMMENT = '秒杀成功明细表';


-- 连接数据库控制台
