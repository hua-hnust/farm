package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farm.entity.OperationRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.farm.entity.UserPlant;
import com.farm.mapper.BusinessSumupMapper;
import com.farm.mapper.OperationRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 操作记录表 服务类
 * </p>
 *
 * @author wyulong
 * @since 2020-03-27
 */
@Service
public class OperationRecordService extends ServiceImpl<OperationRecordMapper, OperationRecord>{

    @Resource
    private OperationRecordMapper operationRecordMapper;

    public IPage<OperationRecord> getOperationRecordInfo(Integer type, int userId, long page, long pageSize) {
        OperationRecord record = OperationRecord.builder().userId(userId).type(type).build();
        return operationRecordMapper.selectPage(new Page<>(page, pageSize),new QueryWrapper<>(record));
    }
}
