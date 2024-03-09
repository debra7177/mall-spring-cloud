后台接口: https://easydoc.net/s/78237135/ZUqEdvA4/vXMBBgw1
## 采购单维护(自营)

![](https://pic.vbean.eu.org/images/2024/03/5723fba21f9899e54f87b0b771fdbacf.png)
- 创建采购单(新建状态)
- 指定采购员(已领取状态)
- 创建采购需求
- 合并到整单(采购需求已分配)
- 完成采购(http://gateway:88/api/ware/purchase/done)
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
