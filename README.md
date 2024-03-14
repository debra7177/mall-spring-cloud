后台接口: https://easydoc.net/s/78237135/ZUqEdvA4/vXMBBgw1

renren后台: http://192.168.1.102:8001/#/login
## 采购单维护(自营)

![](https://pic.vbean.eu.org/images/2024/03/5723fba21f9899e54f87b0b771fdbacf.png)
- 创建采购单(新建状态)
- 指定采购员(已领取状态)
- 创建采购需求
- 合并到整单(采购需求已分配)
- 完成采购(http://gateway:88/api/ware/purchase/done)
## 电商订单流程图
![](https://pic.vbean.eu.org/images/2024/03/761f40b5c8e20b5537258c8bf4f9f1e7.png)
## 订单确认流程
![](https://pic.vbean.eu.org/images/2024/03/4bc66ba628e03cb133dbc4319a9a42c7.png)
## 下单流程
![](https://pic.vbean.eu.org/images/2024/03/820bac24ce324d6854baf051ea5b6eec.png)
![](https://pic.vbean.eu.org/images/2024/03/048616d81517422648b2e0d16991fe33.png)
## 锁定库存
![](https://pic.vbean.eu.org/images/2024/03/0117d82972c13ee949e2713a1899cea2.png)
![](https://pic.vbean.eu.org/images/2024/03/da75a217d723d32c820a596f9f17b09c.png)
**阿里巴巴分布式事务解决方案seata 案例**
[官网](https://seata.apache.org/zh-cn/docs/overview/what-is-seata)
[案例仓库](https://github.com/apache/incubator-seata-samples)

https://github.com/apache/incubator-seata-samples/tree/master/springcloud-jpa-seata
seata windows启动
```bash 
.\seata-server.bat -h 192.168.1.100 -p 8091
```
## 分布式事务 可靠消息 + 最终一致性
![](https://pic.vbean.eu.org/images/2024/03/e28229c7a45755b0591e5a1412f9a880.jpg)
http://studio:15672/#/queues
### 设置队列过期时间的的延时队列
![](https://pic.vbean.eu.org/images/2024/03/97806109b4b4c726a0a2d8e7cadcdefe.png)
![](https://pic.vbean.eu.org/images/2024/03/1028d25862be6b18eef53c0599cd205c.png)
### 锁库存
![](https://pic.vbean.eu.org/images/2024/03/3b93374f774863bcf6566bffd3634b31.png)
### 如何保证消息的可靠性
![](https://pic.vbean.eu.org/images/2024/03/2a41b6f03b946b3d34c6e4368af60966.png)
## 部署

## 表关系
### 属性分组 - 规格参数 - 销售属性 - 三级分类 关联关系
![](https://pic.vbean.eu.org/images/2023/10/3564a3065019e53b48fc943035922102.png)

### SPU - SKU - 属性表
![](https://pic.vbean.eu.org/images/2023/10/c32674002176f6d7be2a0ef5d18ed092.png)

## 过滤器和拦截器
![](https://pic.vbean.eu.org/images/2024/02/0c30d628ba935145481e36a2ee3188cc.png)
![](https://pic.vbean.eu.org/images/2024/02/f9810c54584b9e58297a55713ab61c71.png)
![](https://pic.vbean.eu.org/images/2024/02/11cce15a0bbb6b4c17bd2a90ef2865fd.png)

## 启动admin前台
```bash
/device/workspace/JAVA-BIG-PROJECT/Spring-Boot/nacos/bin/startup.sh -m standalone
```
设置当前会话mysql数据库隔离级别为读未提交(debug时使用)
```sql
set session transaction isolation level read uncommitted 
```
