package com.tester.utils;
/**
 * 创建SqlSeesionFactory工厂，使用工厂 生产SqlSeesion对象，使用SqlSeesion创建Dao接口的代理对象
 */

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.Reader;
import java.io.IOException;

public class DatabaseUtil {
    public static SqlSession getSqlSession() throws IOException {
        //获取配置的资源文件
        Reader reader= Resources.getResourceAsReader("databaseConfig.xml");//读文件
        //得到SqlSessionFactory，使用类加载器加载xml文件[转换格式]
        SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(reader);
        //得到sqlsession对象，这个对象就能执行配置文件中的sql语句啦
        //SqlSession能够执行配置文件的sql语句
        SqlSession session=factory.openSession();
        return session;

    }
}
