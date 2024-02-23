# 部署手册
## 一、项目初始化
### 1.1 项目必备环境
    使用Dokcer安装，项目内置docker编排文件。
### 1.2 需勾选maven对应环境
    开发机本地部署请勾选local，云端业务部署请勾选dev（建议）
### 1.3 默认 JDK-17 如有变动需要更新以下配置
### 1.4 对外服务端口
- 80
- 443
    
## 二、部署步骤
    本项目测试环境基于Ubuntu 22.04 64位操作系统举例，dev环境将所有系统和业务服务部署到一台4CPU+32G内存的云服务器端
### 2.1 安装Docker  
#### a.更新 apt 包索引并安装 ca-certificates、curl、gnupg、lsb-release等，以允许 apt 通过 HTTPS 使用存储库;
```
apt update
sudo apt install -y ca-certificates curl gnupg lsb-release
```
#### b.添加 Docker 的官方 GPG 密钥;
```
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
```
#### c.使用下面命令设置 stable 仓库。
```
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```
#### d.安装最新版本的 Dokcer Engine
```
sudo apt-get update
sudo apt install docker-ce docker-ce-cli containerd.io -y
```
安装指定版本的 Dokcer Engine，首先使用cpt-cache命令查看仓库中 docker-ce 的版本，选择对应的版本，使用install命令安装。
#### e.Docker Compose 安装方法
运行下面的命令将 Docker Compose 1.29.2 的二进制版本安装到 /usr/local/bin/docker-compose 中。
要安装不同版本的 Compose，请将 1.29.2 替换为您要使用的 Compose 版本。
```
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```
对二进制文件 /usr/local/bin/docker-compose 赋予可执行权限。

### 2.2 拷贝部署目录  
- 将项目目录下docker文件夹全部内容拷贝至服务器端，如`/usr/local/omind/`目录下
### 2.3 开始启动服务
依次执行以下命令，每个服务启动成功后执行下一条
- 启动nginx
```
docker-compose --compatibility up -d nginx
```
- 启动Redis
```
docker-compose --compatibility up -d redis
```
- 启动MySQL
```
docker-compose --compatibility up -d mysql
```
数据库初始化sql均存放在``docker\mysql\init``目录下，在容器启动后会自动载入，如需要更新或者重置数据，请替换sql并删除data目录下的数据文件。
- 启动xxl-job
```
docker-compose --compatibility up -d xxl-job
```
- 启动RabbitMQ
```
docker-compose --compatibility up -d rabbitmq
```
- 启动Minio
```
docker-compose --compatibility up -d minio
```
- 启动Nacos
```
docker-compose --compatibility up -d nacos
```
启动Nacos成功后，请创建``dev``命名空间，并导入``config``目录下对应的配置文件

- 启动ruoyi-gateway 网关服务
```
docker-compose --compatibility up -d ruoyi-gateway
```
- 启动ruoyi-auth 通用服务
```
docker-compose --compatibility up -d ruoyi-auth
```
- 启动ruoyi-system 系统服务
```
docker-compose --compatibility up -d ruoyi-system
```
- 启动uoyi-resource 资源服务
```
docker-compose --compatibility up -d ruoyi-resource
```
- 启动omind-baseplat 充电基础基础设施服务
```
docker-compose --compatibility up -d omind-baseplat
```
- 启动omind-userplat 充电运营服务
```
docker-compose --compatibility up -d omind-userplat
```
- 启动omind-simplat 充电桩模拟器
```
docker-compose --compatibility up -d omind-simplat
```
- 启动omind-mp 充电用户端（小程序）服务
```
docker-compose --compatibility up -d omind-mp
```

## 三 API文档
容器部署启动成功后，可访问相关文档   
- 业务：omind-baseplat 充电基础基础设施服务
```
http://[服务器IP]:9801/swagger-ui/index.html
```
- 业务：omind-simplat 充电桩模拟器
```
http://[服务器IP]:9804/swagger-ui/index.html
```
- 业务：omind-userplat 充电运营服务
```
http://[服务器IP]:9805/swagger-ui/index.html
```
- 业务：omind-mp 充电用户端（小程序）服务
```
http://[服务器IP]:9807/swagger-ui/index.html
```
- 系统：ruoyi-auth 通用服务  
```
http://[服务器IP]:9210/swagger-ui/index.html
```
- 系统：ruoyi-system 系统服务
```
http://[服务器IP]:9201/swagger-ui/index.html
```
- 系统：ruoyi-resource 资源服务
```
http://[服务器IP]:9204/swagger-ui/index.html
```