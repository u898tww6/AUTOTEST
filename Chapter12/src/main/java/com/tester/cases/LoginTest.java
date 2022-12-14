package com.tester.cases;

import com.tester.config.TestConfig;
import com.tester.model.InterfaceName;
import com.tester.model.LoginCase;
import com.tester.utils.ConfigFile;
import com.tester.utils.DatabaseUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class LoginTest {
    //测试之前获取链接
    @BeforeTest(groups = "loginTrue",description = "测试准备工作,获取HttpClient对象等")
    public void beforeTest() {
        TestConfig.defaultHttpClient = new DefaultHttpClient();
        TestConfig.getUserInfoUrl = ConfigFile.getUrl(InterfaceName.GETUSERINFO);
        TestConfig.getUserListUrl = ConfigFile.getUrl(InterfaceName.GETUSERLIST);
        TestConfig.addUserUrl = ConfigFile.getUrl(InterfaceName.ADDUSERINFO);
        TestConfig.loginUrl = ConfigFile.getUrl(InterfaceName.LOGIN);
        TestConfig.updateUserInfoUrl = ConfigFile.getUrl(InterfaceName.UPDATEUSERINFO);
    }

        @Test(groups = "loginTrue",description = "用户成功登陆接口")
        public void loginTrue() throws IOException {
            /**
             * DatabaseUtil文件加载databaseConfig.xml数据库配置文件
             * databaseConfig.xml加载操作数据库的SQLMapper.xml文件
             * DatabaseUtil生成SqlSession对象
             * SqlSession创建代理对象session
             * session.selectOne选择一个用户进行登录
             */
            SqlSession session = DatabaseUtil.getSqlSession();
            //使用前边model中创建好的类接受对应的case数据，读取SQLMapper.xml配置文件中的SQL语句
            LoginCase loginCase = session.selectOne("loginCase",1);
            System.out.println(loginCase.toString());
            System.out.println(TestConfig.loginUrl);

            //下边的代码为写完接口的测试代码(发送请求)
            String result = getResult(loginCase);
            //处理结果，就是判断返回结果是否符合预期
            Assert.assertEquals(loginCase.getExpected(),result);

        }

        @Test(description = "用户登陆失败接口")
        public void loginFalse() throws IOException {
            SqlSession session = DatabaseUtil.getSqlSession();
            LoginCase loginCase = session.selectOne("loginCase",2);
            System.out.println(loginCase.toString());
            System.out.println(TestConfig.loginUrl);

            //下边的代码为写完接口的测试代码
            String result = getResult(loginCase);
            //处理结果，就是判断返回结果是否符合预期
            Assert.assertEquals(loginCase.getExpected(),result);
        }

        private String getResult(LoginCase loginCase) throws IOException {
            //下边的代码为写完接口的测试代码
            HttpPost post = new HttpPost(TestConfig.loginUrl);
            //设置body部分
            JSONObject param = new JSONObject();
            param.put("userName",loginCase.getUserName());
            param.put("password",loginCase.getPassword());

            //设置请求头信息 设置header
            post.setHeader("content-type","application/json");
            //将参数信息添加到方法中
            StringEntity entity = new StringEntity(param.toString(),"utf-8");
            post.setEntity(entity);
            //声明一个对象来进行响应结果的存储
            String result;
            //执行post方法
            HttpResponse response = TestConfig.defaultHttpClient.execute(post);
            //获取响应结果
            result = EntityUtils.toString(response.getEntity(),"utf-8");
            System.out.println(result);
            TestConfig.store = TestConfig.defaultHttpClient.getCookieStore();
            return result;
        }
}
