## Mybatis-9.28

环境：

- JDK 1.8
- Mysql 5.7
- maven 3.6.1
- IDEA

框架：大部分有配置文件的，最好的方式就是看官网文档





## 一、简介

### 1.1 什么是Mybatis？

![image-20210411205528369](Mybatis笔记md.assets\image-20210411205528369.png)

- MyBatis 是一款优秀的**持久层框架**
- MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作
- MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录
- *MyBatis* 本是apache的一个开源项目iBatis, 2010年这个项目由apache software foundation 迁移到了google code，并且改名为*MyBatis* 。2013年11月迁移到Github。



如何获得Mybatis？

- maven

```xml
<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.6</version>
</dependency>

```

- Github：https://github.com/mybatis/mybatis-3/releases/tag/mybatis-3.5.6
- 中文文档：https://mybatis.org/mybatis-3/zh/index.html



### 1.2 持久化

数据持久化

- 持久化就是将程序的数据在持久状态和瞬时状态转化的过程
- 内存：**断电即失**
- 方式：数据库(jdbc)，io文件持久化
- 生活中的持久化：冷藏、罐头



为什么需要持久化？

- 有一些对象不能丢失
- 内存太贵了



### 1.3 持久层（Dao 层）

- 完成持久化工作的代码块
- 层之间的节限十分明显



### 1.4 为什么需要 Mybatis？

- 帮助程序猿将数据存入到数据库中
- 方便
- 传统的 JDBC 代码太复杂。简化操作、框架化、自动化
- 不用Mybatis也可以，只是更容易上手而已。**技术没有高低之分** 
- 优点：
  - 简单易学
  - 灵活
  - sql 和代码的分离，提高了可维护性 
  - 提供映射标签，支持对象与数据库的 orm 字段关系映射
  - 提供对象关系映射标签，支持对象关系组建维护
  - 提供 xml 标签，支持编写动态 sql

**最重要的一点：使用的人多！！**

spring、springMVC、SpringBoot 均如是



## 二、第一个Mybatis程序

思路：搭建环境 --> 导入 Mybatis --> 编写代码 --> 测试

### 2-1 搭建环境

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.msq</groupId>
    <artifactId>Mybatis-Study</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.20</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.5</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version> <!-- 用4.12才行，原因不详 -->
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
```





SqlSessionFactoryBuilder 创建一次 SqlSessionFactory 后就没用了，所以作为局部变量最好；

SqlSessionFactory 则是创建一次后一直被重复使用，用于重复创建 SqlSession，所以使用单例模式最好

SqlSession 是访问数据库的，**且不是线程安全**，必须需要进行代码同步；每次收到一个 http 请求就打开，返回响应时就得关闭，否则会出事。最佳实践：try(.....){....}

![image-20210513161755713](E:\GoodgoodStudy\笔 记\Mybatis笔记md.assets\image-20210513161755713.png)



### 2-2 创建一个模块

- 在resource下创建配置文件 mybaitis-config.xml，从官网复制内容

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
      <environments default="development">
          <environment id="development">
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                  <property name="driver" value="com.mysql.jdbc.Driver"/>
                  <property name="url" value="jdbc:mysql://localhost:3306/mybatis_study?serverTimezome=GMT%2B8"/>
                  <property name="username" value="root"/>
                  <property name="password" value="123456"/>
              </dataSource>
          </environment>
      </environments>
      <mappers>
          <mapper resource="org/mybatis/example/BlogMapper.xml"/>
      </mappers>
  </configuration>
  ```

  

- 编写mybatis工具类（这部分代码以后可不动）

  ```java
  public class MybatisUtils {
  
      private static SqlSessionFactory sqlSessionFactory;
  
      static{
          try {
              String resource = "mybatis-config.xml";
              InputStream inputStream = Resources.getResourceAsStream(resource);
              sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
          } catch (IOException e) {
              e.printStackTrace();
          }
  
      }
  
  
      public static SqlSession getSqlSession(){
          return sqlSessionFactory.openSession();
      }
  }
  ```

- 编写测试代码：

  ```java
  public class UserMapperTest {
  
      @Test
      public void getUserList(){
          SqlSession sqlSession = MybatisUtils.getSqlSession();
          UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
          List<User> users = userMapper.getUserList();
          for (User user : users) {
              System.out.println(user);
          }
          sqlSession.close();
  
      }
  }
  ```

  

## 三、CRUD

注：@Test 声明单元测试是要求void修饰方法且方法无参

### namespace

UserMapper 中的 namespace 必须和 mapper 名称一致才会生效



### CRUD

接口

```JAVA
public interface UserMapper {
    List<User> getUserList();

    User getUserById(int id);
    
    int addUser(User user);

    int updateUser(User user);

    void deleteUser(int id);

}
```



mapper 实现

```XML
<select id="getUserById" parameterType="int" resultType="com.msq.pojo.User">
        select * from user where id = #{id}
    </select>

    <insert id="addUser" parameterType="com.msq.pojo.User">
        insert into user(id, name, pwd) values(#{id}, #{name}, #{pwd});
    </insert>

    <update id="updateUser" parameterType="com.msq.pojo.User">
        update user set name=#{name}, pwd=#{pwd} where id=#{id};
    </update>

    <delete id="deleteUser" parameterType="int">
        delete from user where id = #{id}
    </delete>
```



测试：

```java
@Test
    public void getUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.getUserById(1);
        System.out.println(user);
        sqlSession.close();

    }

    @Test
    public void addUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int res = mapper.addUser(new User(4, "msq", "123456"));
        if(res > 0)
            System.out.println("新增用户成功");
        sqlSession.commit();
        sqlSession.close();
    }
```

