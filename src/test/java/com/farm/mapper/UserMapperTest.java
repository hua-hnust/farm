package com.farm.mapper;

import com.alibaba.fastjson.JSON;
import com.farm.Application;
import com.farm.entity.ApplyRecord;
import com.farm.entity.User;
import com.farm.service.ApplyRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author xhua
 * @Date 2020/3/24 9:45
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void test(){
        List<User> users =  userMapper.getUserAndArticle();
        System.out.println(JSON.toJSONString(users));
    }

}
