package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farm.dto.res.MyCollectDTO;
import com.farm.entity.ApplyRecord;
import com.farm.entity.UserCollect;
import com.farm.mapper.UserCollectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 用户收藏 服务类
 * </p>
 *
 * @author wyulong
 * @since 2020-03-27
 */
@Service
public class UserCollectService extends ServiceImpl<UserCollectMapper, UserCollect> {

    @Resource
    private UserCollectMapper collectMapper;

    /**
     * 是否存在收藏
     *
     * @param userId
     * @param articleId
     * @return
     */
    public boolean exists(Integer userId, Integer articleId) {
        Integer count = collectMapper.selectCount(new QueryWrapper<>(UserCollect.builder().articleId(articleId).userId(userId).build()));
        return count > 0;
    }

    public IPage<UserCollect> getCollectPage(Integer userId, Integer currPage, Integer pageSize) {
        QueryWrapper<UserCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId).eq("status",1).orderByDesc("create_time");
        Page<UserCollect> page = new Page<>(currPage, pageSize);
        return collectMapper.selectPage(page, queryWrapper);
    }

    /**
     *  我的收藏
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public IPage<MyCollectDTO> getMyCollect(Integer userId, Long page, Long pageSize) {
        Page<MyCollectDTO> page1 = new Page<>(page,pageSize);
        return collectMapper.getMyCollect(page1,userId);
    }
}
