package com.farm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.constants.DateStatus;
import com.farm.constants.Errors;
import com.farm.dto.req.ArticleParamsDTO;
import com.farm.dto.res.ArticleDTO;
import com.farm.dto.res.BusinessSumupDTO;
import com.farm.entity.Article;
import com.farm.entity.BusinessSumup;
import com.farm.service.ArticleService;
import com.farm.service.AuthService;
import com.farm.service.BusinessSumupService;
import com.farm.service.UserService;
import com.farm.util.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.farm.constants.Errors.CONTENT_EMPTY;
import static com.farm.constants.Errors.NOT_FOUND;

/**
 * 技术人员模块
 * @Author xhua
 * @Date 2020/3/23 17:54
 **/
@RestController
@RequestMapping("/tech")
@Slf4j
public class TechnologyController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private BusinessSumupService sumupService;

    /**
     * 新增文章&编辑文章
     * @param articleParamsDTO
     */
    @PostMapping("/article")
    public Boolean saveArticle(@RequestBody ArticleParamsDTO articleParamsDTO){
        Article article = new Article();
        BeanUtils.copyProperties(articleParamsDTO,article);
        article.setAuthorId(userService.currentUser().getId());
        article.setStatus(DateStatus.VALID.getCode());
        return articleService.saveOrUpdate(article);
    }

    /**
     * 删除文章
     * @param id 文章id
     */
    @DeleteMapping("/article/{id}")
    public Boolean deleteArticle(@PathVariable Integer id){
        Article article = articleService.getById(id);
        if (article == null){
            Exceptions.throwss("文章不存在");
            return false;
        }else {
            article.setStatus(DateStatus.INVALID.getCode());
            return articleService.saveOrUpdate(article);
        }
    }

    /**
     * 文章分页列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/article")
    public IPage<ArticleDTO> getArticlePage(Integer type,long page, long pageSize) {
        return articleService.searchArticleByType(page,pageSize,type);
    }

    /**
     * 新增、修改下乡总结
     * @param businessSumup
     */
    @PostMapping("/sumup")
    public Boolean saveBusinessSumup(@RequestBody BusinessSumup businessSumup){
        if (StringUtils.isBlank(businessSumup.getContent())){
            Exceptions.throwss(CONTENT_EMPTY);
        }
        businessSumup.setAuthorId(userService.currentUser().getId());
        if (businessSumup.getId() == null){
            businessSumup.setCreateTime(LocalDateTime.now());
        }
        businessSumup.setUpdateTime(LocalDateTime.now());
        businessSumup.setStatus(DateStatus.VALID.getCode());
        return sumupService.saveOrUpdate(businessSumup);
    }

    /**
     * 下乡总结分页列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/sumup")
    public IPage<BusinessSumupDTO> getBusinessSumup(long page, long pageSize) {
        Integer userId = userService.currentUser().getId();
        return sumupService.listSumup(userId,page,pageSize);
    }

    /**
     * 下乡总结详情
     * @param sumupId
     * @return
     */
    @GetMapping("/sumup/{sumupId}")
    public BusinessSumupDTO getBusinessSumup(@PathVariable Integer sumupId){
        BusinessSumup sumup = sumupService.getById(sumupId);
        return sumupService.copy(sumup, userService.currentUser());
    }


    /**
     * 删除下乡总结
     * @param sumupId
     */
    @DeleteMapping("/sumup/{sumupId}")
    public Boolean deleteSumup(@PathVariable Integer sumupId){
        BusinessSumup businessSumup = Optional.ofNullable(sumupService.getById(sumupId)).orElseThrow(() -> Errors.of(NOT_FOUND));
        businessSumup.setStatus(DateStatus.INVALID.getCode());
        return sumupService.saveOrUpdate(businessSumup);
    }


}
