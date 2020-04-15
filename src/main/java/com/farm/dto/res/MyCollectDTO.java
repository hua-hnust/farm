package com.farm.dto.res;

import com.farm.dto.PageResult;
import lombok.*;

import java.util.Date;

/**
 * @Author xhua
 * @Date 2020/4/13 22:43
 **/
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyCollectDTO {

    private Integer id;
    private Integer userId;
    private Integer articleId;
    private String articleTitle;
    private Date createTime;

}
