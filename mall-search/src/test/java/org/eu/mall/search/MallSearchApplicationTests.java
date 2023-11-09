package org.eu.mall.search;


import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.eu.mall.search.config.MallElasticSearchConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 测试存储数据到es
     * 更新也可以
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");//数据的id
//        indexRequest.source("userName","zhangsan","age",18,"gender","男");
        User user = new User();
        user.setUserName("zhangsan");
        user.setAge(18);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);//要保存的内容


        //执行操作
        IndexResponse index = restHighLevelClient.index(indexRequest, MallElasticSearchConfig.COMMON_OPTIONS);

        //提取有用的响应数据
        System.out.println(index);
    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;

    }

    @Test
    public void contextLoads() {
        System.out.println(restHighLevelClient);
    }

}
