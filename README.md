# 快速了解
   
当前版本：V1.0.0

[微服务](https://gitee.com/orise001/orise-charge-cloud)
[管理后台UI源码](https://gitee.com/orise001/orise-admin)
[小程序源码](https://gitee.com/orise001/orise-mp)
   
### ⭐ 系统亮点

1. 支持云快充通信协议 ｜ 支持分时电价 ｜ 支持停车系统

2. 支持设备监控预警 ｜ 支持中电联互联互通 ｜ 支持各地区市政平台对接

3. 微服务架构 ｜ 系统-业务模块分离 ｜ 多租户 ｜ 高可用 ｜ 动态伸缩 ｜ 无感升级

4. 采用 SpringCloud + Mybatis-Plus + Redis + RabbitMQ + Smart-Socket 高并发方案

5. 系统内置“模拟桩”模块，可进行仿真充电，方便业务调试

```
1.易维护：基于Ruoyi-Cloud-Plus脚手架搭建，脚手架与业务模块分离，易于升级更新；
2.前后端分离：后端采用SpringCloud，关联端采用vue-admin-element，移动端采用uni-app；
3.权限管理：基于ruoyi体系，支持多租户；
4.二次开发能力：业务模块独立，方便业务扩展；
5.快速上手：完备的用户使用文档、可基于docker编排10分钟快速搭建仿真使用环境；
6.系统安全：支持接口数据加密、全局操作日志等；
7.高性能：基于Smart-socket通信架构，2CPU+4G单机支持2000台以上充电桩进行业务交换；
8.互通能力：支持对接特来电、快电、新电途、e充电、星星充电等各家充电平台。
```

### 🧭 参考文档
   
[👉 开始阅读](http://doc.trytowish.cn/)
   
### 💻 运行环境

```
nginx:1.22.1
mysql:8
redis:6.2.7
xxl-job-admin:2.3.1（可选）
rabbitmq:3.10.6
minio（可选）
nacos-server:v2.1.1
Java17
```

### 🔨 业务模块结构

- ruoyi-\*：请参考ruoyi-cloud-plus项目 [传送门](https://gitee.com/dromara/RuoYi-Cloud-Plus)
- omind-api：业务内部接口模块集合

```
omind-api-baseplat：充电基础设施服务外部接口模块
omind-api-common：通用模块
omind-api-mq：通用消息队列模块
omind-api-user-mq：用户客户端服务外部接口模块
omind-api-userplat：充电运营服务外部接口模块
```

- omind-modules：业务模块集合

```
omind-baseplat：充电基础设施服务模块
omind-userplat：充电运营服务模块
omind-mp：用户客户端服务模块
omind-simplat：模拟充电桩模块
```

### 💿 系统演示

[👉 进入演示系统](http://plat.trytowish.cn/)  
用户名：``admin``  
密码：``admin123``


### ⚙️ 快速开始

使用docker-compose快速构建模拟系统

[👉 开始构建服务](http://doc.trytowish.cn/develop/cloud.html)

[👉 开始构建用户端](http://doc.trytowish.cn/develop/front-end.html)

[👉 模拟桩使用教程](http://doc.trytowish.cn/guide/simtest.html)

### 🌈 核心功能

![extending-a-theme](/images/cloud/服务架构图.png)

- 系统基于互联互通协议，充电运营服务支持对接多个自有/第三方平台（如特来电、快电、新电途、e充电）和市政充电平台；
- 系统基础服务采用ruoyi框架，独立与业务模块，可无干扰同步升级系统非业务功能；
- 充电基础设施服务支持多类充电桩，支持一站多种不同协议混合使用；
- 系统支持动态伸缩，服务增减对业务0影响；
- 系统内部采用Dubbo通信，低延迟。

![extending-a-theme](/images/cloud/系统业务层级图.png)

### 🖥️ UI界面展示

![login](/images/front-end/operating-system-login.jpg)

![dashboard](/images/front-end/operating-system-dashboard.jpg)

![monitor](/images/front-end/operating-system-monitor.jpg)

<img src="/images/cloud/小程序-充电站列表.jpg" alt="小程序-充电站列表" height="300">
<img src="/images/cloud/模拟充电-选择充电桩.jpg" alt="模拟充电-选择充电桩" height="300">
<img src="/images/cloud/模拟充电-启动充电.jpg" alt="模拟充电-启动充电" height="300">
<img src="/images/cloud/模拟充电-充电中.jpg" alt="模拟充电-充电中" height="300">
<img src="/images/cloud/模拟充电-结束订单.jpg" alt="模拟充电-结束订单" height="300">

### 📖 支持中电联互联互通协议

- T／CEC 102.1—2016 电动汽车充换电服务信息交换 第1部分：总则
- T／CEC 102.2—2016 电动汽车充换电服务信息交换 第2部分：公共信息交换规范
- T／CEC 102.3—2016 电动汽车充换电服务信息交换 第3部分：业务信息交换规范
- T／CEC 102.4—2016 电动汽车充换电服务信息交换 第4部分：数据传输及安全

### 🙏 特别鸣谢

- smart-socket 项目 [传送门 https://gitee.com/smartboot/smart-socket](https://gitee.com/smartboot/smart-socket)
- ruoyi-cloud-plus 项目 [传送门 https://gitee.com/dromara/RuoYi-Cloud-Plus](https://gitee.com/dromara/RuoYi-Cloud-Plus)
