package com.farm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.constants.ApplyStatus;
import com.farm.constants.ArticleType;
import com.farm.constants.Errors;
import com.farm.dto.req.ArticleParamsDTO;
import com.farm.dto.req.BusinessSumupParamsDTO;
import com.farm.dto.req.RegisterDTO;
import com.farm.dto.req.ResolveApplyDTO;
import com.farm.dto.res.ApplyDTO;
import com.farm.dto.res.ArticleDTO;
import com.farm.entity.ApplyRecord;
import com.farm.entity.Article;
import com.farm.entity.BusinessSumup;
import com.farm.entity.User;
import com.farm.service.*;
import com.farm.util.DateTimeUtil;
import com.farm.util.Exceptions;
import com.farm.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static com.farm.constants.Errors.INVALID_TOKEN;
import static com.farm.constants.Errors.of;

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

    @Autowired
    private ApplyRecordService applyRecordService;

    /**
     * 获取公告分页数据
     *
     * @param page     当前页
     * @param pageSize 每页数量
     * @return
     */
    @GetMapping("/notice")
    public IPage<ArticleDTO> getNoticePage(@RequestParam("page")Long page,@RequestParam("pageSize")Long pageSize) {
        User currentUser = Optional.ofNullable(userService.currentUser()).orElseThrow(() -> of(INVALID_TOKEN));
        return articleService.getNotice(currentUser.getId(),page,pageSize);
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
        //数据库字段策略，修改的时候不需要更新此字段
        //article.setCreateTime(LocalDateTime.now());
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
     *  申请记录
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("list-apply")
    public IPage<ApplyDTO> listApply(@RequestParam("page")Integer page, @RequestParam("pageSize")Integer pageSize){
        return applyRecordService.getApplyRecordPage(0,page,pageSize);
    }

    /**
     * 审批申请单
     */
    @PostMapping("/apply/resolve")
    public Boolean resolveApplies(@RequestBody ResolveApplyDTO resolveApplyDTO) {

        //申请单id
        //审批状态
        //添加图片

        ApplyRecord applyRecord = applyRecordService.getById(resolveApplyDTO.getApplyId());
        if (applyRecord == null){
            Exceptions.throwss("申请不存在");
        }

        if (resolveApplyDTO.getStatus() == ApplyStatus.REJECTED.getCode() && StringUtils.isEmpty(resolveApplyDTO.getRefuseDesc()))
        {
            Exceptions.throwss("请填写退回原因");
        }
        User currentUser = userService.currentUser();

        applyRecord.setAuthId(currentUser.getId());
        applyRecord.setContent(JSONArray.toJSON(resolveApplyDTO.getImages()).toString());
        applyRecord.setStatus(resolveApplyDTO.getStatus());
        applyRecord.setRefuseDesc(resolveApplyDTO.getRefuseDesc());

        return applyRecordService.updateById(applyRecord);

    }

    /**
     *  保存&修改下乡总结
     * @param businessSumupParamsDTO
     */
    @PostMapping("/sumup")
    public Boolean sumup(@RequestBody BusinessSumupParamsDTO businessSumupParamsDTO){
        BusinessSumup businessSumup = new BusinessSumup();
        BeanUtils.copyProperties(businessSumupParamsDTO,businessSumup);
        businessSumup.setTime(LocalDateTime.now());
        businessSumup.setAuthorId(userService.currentUser().getId());
        businessSumup.setStatus(1);
        //数据库字段策略，修改的时候不需要更新此字段
        //businessSumup.setCreateTime(LocalDateTime.now());
        return businessSumupService.saveOrUpdate(businessSumup);
    }

    /**
     *  撤回下乡总结
     * @param id
     */
    @DeleteMapping("/sumup/{id}")
    public Boolean deleteSumup(@PathVariable("id")Integer id){
        BusinessSumup businessSumup = businessSumupService.getById(id);
        if (ObjectUtils.isEmpty(businessSumup)){
            return false;
        }else {
            businessSumup.setStatus(0);
            return businessSumupService.saveOrUpdate(businessSumup);
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
