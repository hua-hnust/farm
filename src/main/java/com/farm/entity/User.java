package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @Author xhua
 * @Date 2020/3/22 0:25
 **/
@TableName("user")
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String phone;
    private String cardId;
    private String password;
    private Integer type;
    private String token;
    private Integer status;
    private Date tokenExpireTime;
    private Date createTime;
    private Date updateTime;
    private List<Article> articleList;

}
