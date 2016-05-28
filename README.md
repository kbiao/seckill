
## 常见秒杀高并发优化方案方案

原子计数器（减库存） --> redis/NoSQL
记录行为消息              --> 分布式MQ（消息队列）
消费消息并落地           -->MySQL 

mySQL 每秒可以执行40K以上的Update操作   
性能的瓶颈在于每次事务过程中的串行执行  有通信延时以及GC操作的发生 

行级锁在Commit之后释放，优化方向就是减少行级锁持有时间  

网络时延 同城都要2MS
Update之后 GC  大约20毫秒

如何判断Update更新库存成功？  意识Update自身没有报错  客户端确认Update影响行数

优化思路  把哭护短逻辑放到M有sq服务端  避免网络延时和GC影响 

定制SQL  / 整个事务在M有SQL端完成   

前端按钮防重复  动静态数据分离   CDN缓存  后端缓存  




## 系统可能用到哪些服务

1. CDN
2. WebServer : Ngnix + jetty
3. Redis
4. MySQL
