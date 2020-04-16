package com.farm.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author xhua
 * @Date 2020/4/8 14:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperateRecordParams implements Serializable {

    private Integer id;

    /**
     * 操作类型 1、施肥 2、打药
     */
    private Integer type;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 用量
     */
    private String usedAomut;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

}
