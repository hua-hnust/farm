package com.farm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.constants.ArticleType;
import com.farm.constants.Errors;
import com.farm.dto.req.ArticleParamsDTO;
import com.farm.dto.req.RegisterDTO;
import com.farm.dto.res.ArticleDTO;
import com.farm.entity.Article;
import com.farm.entity.BusinessSumup;
import com.farm.entity.User;
import com.farm.service.*;
import com.farm.util.Exceptions;
import com.farm.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * 管理员接口
 *
 * @Author xhua
 * @Date 2020/3/23 17:51
 **/
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private BusinessSumupService businessSumupService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ArticleService articleService;

    /**
     * 获取公告分页数据
     *
     * @param page     当前页
     * @param pageSize 每页数量
     * @return
     */
    @GetMapping("/notice")
    public IPage<ArticleDTO> getNoticePage(@RequestParam("page")Long page,@RequestParam("pageSize")Long pageSize) {
        return articleService.getNotice(page,pageSize);
    }

    /**
     * 保存公告&修改公告
     *
     * @param articleParamsDTO 公告内容
     */
    @PostMapping("/notice")
    public Boolean saveNotice(@RequestBody ArticleParamsDTO articleParamsDTO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleParamsDTO,article);
        article.setAuthorId(userService.currentUser().getId());
        article.setStatus(1);
        article.setCreateTime(LocalDateTime.now());

        article.setType(ArticleType.NOTICE.getCode());

        return articleService.saveOrUpdate(article);
    }

    /**
     * 删除公告
     *
     * @param id 公告id
     */
    @DeleteMapping("/notice/{id}")
    public Boolean deleteNotice(@PathVariable Integer id) {
        Article article = articleService.getById(id);
        if (ObjectUtils.isEmpty(article)){
            return false;
        }else {
            article.setStatus(0);
            return articleService.saveOrUpdate(article);
        }
    }

    /**
     * 获取公告详细信息
     *
     * @param id 公告id
     * @return
     */
    @GetMapping("/notice/{id}")
    public ArticleDTO getNotice(@PathVariable Integer id) {

        return articleService.getNoticeDetailById(id);
    }

    /**
     * 审批申请单
     */
    @PostMapping("/apply/resolve")
    public void resolveApplies() {
        //申请单id
        //审批状态
        //添加图片

    }

    /**
     *  保存&修改下乡总结
     * @param businessSumup
     */
    @PostMapping("/sumup")
    public void sumup(@RequestBody BusinessSumup businessSumup){
        businessSumup.setAuthorId(userService.currentUser().getId());
        businessSumup.setStatus(1);
        businessSumupService.saveOrUpdate(businessSumup);
    }

    /**
     *  撤回下乡总结
     * @param id
     */
    @DeleteMapping("/sumup/{id}")
    public void deleteSumup(@PathVariable("id")Integer id){
        BusinessSumup businessSumup = businessSumupService.getById(id);
        if (ObjectUtils.isEmpty(businessSumup)){
            Exceptions.throwss("总结不存在");
        }else {
            businessSumup.setStatus(0);
        }
    }

    @PostMapping("/user")
    public void saveUser(@RequestBody RegisterDTO registerDTO){
        authService.register(registerDTO);
    }

    @PutMapping("/user")
    public void updateUser(@RequestBody User user){
        User dbUser = Optional.ofNullable(userService.getById(user.getId())).orElseThrow(() -> Errors.of("用户不存在"));
        userService.validUser(user);
        if (user.getPassword() != null){
            String password = MD5Util.encrypt(user.getPassword());
            if(!StringUtils.equals(dbUser.getPassword(),password)){
                user.setToken(null);
                user.setTokenExpireTime(new Date());
                user.setPassword(password);
            }
        }else {
            user.setToken(dbUser.getToken());
            user.setTokenExpireTime(dbUser.getTokenExpireTime());
            user.setPassword(dbUser.getPassword());
        }

        user.setUpdateTime(new Date());
        userService.saveOrUpdate(user);
    }

    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable Integer userId){
        User user = userService.getById(userId);
        user.setPassword(null);
        return user;
    }

    @GetMapping("/user")
    public IPage<User> getUserPage(long page,long pageSize){
        return userService.page(new Page<>(page,pageSize));
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@PathVariable Integer userId){
        User dbUser = Optional.ofNullable(userService.getById(userId)).orElseThrow(() -> Errors.of("用户不存在"));
        userService.removeById(userId);
    }


}
