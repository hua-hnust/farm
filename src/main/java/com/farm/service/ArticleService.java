package com.farm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farm.constants.ArticleType;
import com.farm.constants.Enums;
import com.farm.dto.res.ArticleDTO;
import com.farm.entity.Article;
import com.farm.mapper.ArticleMapper;
import com.farm.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 * @author wyulong
 * @since 2020-03-27
 */
@Service
public class ArticleService extends ServiceImpl<ArticleMapper, Article> {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;


    public IPage<ArticleDTO> searchArticle(String content,Long page,Long pageSize) {
        Page<ArticleDTO> ipage = new Page<>(page,pageSize);

        List<ArticleDTO> list = articleMapper.searchArticle(ipage,content);
        //文章描述
        for (ArticleDTO articleDTO:list){
            ArticleType articleType = Enums.valueOf(articleDTO.getType(),ArticleType.class);
            articleDTO.setTypeDesc(articleType == null ? null:articleType.getDesc());
        };

        return ipage.setRecords(list);
    }

    /**
     *  查询公告
     * @param page
     * @param pageSize
     * @return
     */
    public IPage<ArticleDTO> getNotice(Integer userId,Long page, Long pageSize) {
        Page<ArticleDTO> iPage = new Page<>(page,pageSize);
        List<ArticleDTO> list = articleMapper.getNotice(iPage,userId);
        return iPage.setRecords(list);
    }

    public IPage<ArticleDTO> getNotice(Long page, Long pageSize){
        Page<ArticleDTO> iPage = new Page<>(page,pageSize);
        List<ArticleDTO> list = articleMapper.getNoticePage(iPage);
        return iPage.setRecords(list);
    }

    /**
     * 获取公告详情
     * @param id
     * @return
     */
    public ArticleDTO getNoticeDetailById(Integer id) {

        Article article = articleMapper.selectById(id);
        if (ObjectUtils.isEmpty(article) || !(ArticleType.NOTICE.getCode() == article.getType())){
            return null;
        }

        ArticleDTO articleDTO = new ArticleDTO();
        BeanUtils.copyProperties(article,articleDTO);
        //两次IO了，其实应该一次SQL关联查询
        String authorName = userMapper.selectById(article.getAuthorId()).getName();
        articleDTO.setAuthorName(authorName);
        ArticleType articleType = Enums.valueOf(articleDTO.getType(),ArticleType.class);
        articleDTO.setTypeDesc(articleType == null ? null:articleType.getDesc());

        return articleDTO;
    }

    public IPage<ArticleDTO> searchArticleByType(long page, long pageSize, Integer type) {
        Page<ArticleDTO> ipage = new Page<>(page,pageSize);
        List<ArticleDTO> list = articleMapper.searchArticleByType(ipage,type);
        //文章描述
        for (ArticleDTO articleDTO:list){
            ArticleType articleType = Enums.valueOf(articleDTO.getType(),ArticleType.class);
            articleDTO.setTypeDesc(articleType == null ? null:articleType.getDesc());
        };
        return ipage.setRecords(list);
    }
}
