package com.farm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.dto.res.MyCollectDTO;
import com.farm.entity.UserCollect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户收藏 Mapper 接口
 * </p>
 *
 * @author wyulong
 * @since 2020-03-27
 */
public interface UserCollectMapper extends BaseMapper<UserCollect> {

    IPage<MyCollectDTO> getMyCollect(Page<MyCollectDTO> page1, @Param("userId") Integer userId);
}
