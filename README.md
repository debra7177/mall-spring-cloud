
## 表关系
### 属性分组 - 规格参数 - 销售属性 - 三级分类 关联关系
![](https://pic.vbean.eu.org/images/2023/10/3564a3065019e53b48fc943035922102.png)

### SPU - SKU - 属性表
![](https://pic.vbean.eu.org/images/2023/10/c32674002176f6d7be2a0ef5d18ed092.png)


## 启动admin前台
```bash
/device/workspace/JAVA-BIG-PROJECT/Spring-Boot/nacos/bin/startup.sh -m standalone
```
设置当前会话mysql数据库隔离级别为读未提交(debug时使用)
```sql
set session transaction isolation level read uncommitted 
```
