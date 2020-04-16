package com.farm.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.dto.res.ArticleDTO;
import com.farm.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 * @author wyulong
 * @since 2020-03-27
 */
public interface ArticleMapper extends BaseMapper<Article> {

    List<ArticleDTO> searchArticle(IPage<ArticleDTO> iPage,@Param("content")String content);

    List<ArticleDTO> getNotice(IPage<ArticleDTO> iPage, @Param("userId")Integer userId);

    List<ArticleDTO> getNoticePage(IPage<ArticleDTO> iPage);

    List<ArticleDTO> searchArticleByType(Page<ArticleDTO> ipage, @Param("type")Integer type);
}
