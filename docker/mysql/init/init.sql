# grant all privileges on dbname.tablename to 'userName'@'%';
# 比如想给用户nacos赋予数据库test所有的表的权限并且不限制nacos用户的连接地址，代码如下
CREATE USER 'omind'@'%' IDENTIFIED BY 'omind';
grant all privileges on *.* to 'omind'@'%';
# 刷新权限
flush privileges;
use mysql;
alter user 'omind'@'%' IDENTIFIED WITH mysql_native_password BY 'omind2023';
