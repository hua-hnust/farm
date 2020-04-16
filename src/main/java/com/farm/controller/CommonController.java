package com.farm.controller;

import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.farm.annotation.OpenApi;
import com.farm.configurations.UploadConfiguration;
import com.farm.constants.*;
import com.farm.dto.req.LoginParamsDTO;
import com.farm.dto.req.RegisterDTO;
import com.farm.dto.req.UpdatePasswordDTO;
import com.farm.dto.res.ArticleDTO;
import com.farm.dto.res.BusinessSumupDTO;
import com.farm.dto.res.LoginInfoDTO;
import com.farm.dto.res.MyCollectDTO;
import com.farm.entity.Article;
import com.farm.entity.User;
import com.farm.entity.UserCollect;
import com.farm.mapper.UserCollectMapper;
import com.farm.mapper.UserMapper;
import com.farm.service.*;
import com.farm.util.Exceptions;
import com.farm.util.FileUtil;
import com.farm.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

import static com.farm.constants.Errors.INVALID_TOKEN;
import static com.farm.constants.Errors.of;

/**  通用接口
 * @Author xhua
 * @Date 2020/3/23 14:39
 **/
//登录、评论、收藏、搜索功能  不做权限拦截
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Resource
    private AuthService authService;

    @Resource
    private ArticleService articleService;

    @Resource
    private UploadConfiguration uploadConfiguration;

    @Resource
    private BusinessSumupService businessSumupService;

    @Resource
    private UserService userService;

    @Resource
    private UserCollectService userCollectService;

    @Resource
    private UserMapper userMapper;

    /**
     *  注册接口
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    public Boolean register(@RequestBody RegisterDTO registerDTO){
        return authService.register(registerDTO);
    }

    /**
     *  登录接口
     * @param loginParamsDTO
     * @return
     */
    @PostMapping("/login")
    @OpenApi
    public LoginInfoDTO login(@RequestBody LoginParamsDTO loginParamsDTO){
        return authService.verifyPhoneAndPassword(loginParamsDTO.getPhone(), loginParamsDTO.getPassword());
    }

    /**
     *  退出登录
     * @return
     */
    @PostMapping("logout")
    public Boolean logout(){
        User currentUser = Optional.ofNullable(userService.currentUser()).orElseThrow(() -> of(INVALID_TOKEN));
        currentUser.setTokenExpireTime(new Date(System.currentTimeMillis() - CommonConstants.TOKEN_EXPIRE_TIME));
        return userService.updateById(currentUser);
    }

    /**
     *  修改密码
     * @param updatePasswordDTO
     * @return
     */
    @PostMapping("update-password")
    public Boolean updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        User currentUser = Optional.ofNullable(userService.currentUser()).orElseThrow(() -> of(INVALID_TOKEN));
        String old = MD5Util.encrypt(updatePasswordDTO.getOldPassword());
        if (!currentUser.getPassword().equals(old)){
            Exceptions.throwss("旧密码错误");
            return false;
        }else {
            currentUser.setPassword(MD5Util.encrypt(updatePasswordDTO.getNewPassword()));
            return userService.updateById(currentUser);
        }
    }

    /**
     *   菜单权限接口
     */
    @GetMapping("/menu")
    public List menu(){
        User user = userService.currentUser();
        if (ObjectUtils.isEmpty(user)){
            return Collections.emptyList();
        }else {
            return userService.getMenu(user.getType());
        }
    }

    /**
     *  搜索文章
     * @param content
     * @return
     */
    @GetMapping("/article/search")
    public IPage<ArticleDTO> search(@RequestParam(value = "content",required = false)String content,@RequestParam("page") Long page, @RequestParam("pageSize") Long pageSize){
        User currentUser = Optional.ofNullable(userService.currentUser()).orElseThrow(() -> of(INVALID_TOKEN));
        return articleService.searchArticle(currentUser.getId(),content,page,pageSize);
    }

    /**
     * 文章分类查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/article")
    public IPage<ArticleDTO> getArticlePage(Integer type,long page, long pageSize) {
        return articleService.searchArticleByType(page,pageSize,type);
    }

    /**
     *  获取文章详情
     * @param id
     * @return
     */
    @GetMapping("article-detail")
    public ArticleDTO getArticleDetail(@RequestParam("id")Integer id){
        User currentUser = Optional.ofNullable(userService.currentUser()).orElseThrow(() -> of(INVALID_TOKEN));
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",DateStatus.VALID.getCode());
        queryWrapper.in("type",2,3);
        queryWrapper.eq("id",id);
        Article article = articleService.getOne(queryWrapper);
        ArticleDTO articleDTO = new ArticleDTO();
        BeanUtils.copyProperties(article,articleDTO);
        User user = userService.getById(article.getAuthorId());
        articleDTO.setAuthorName(user.getName());
        ArticleType articleType = Enums.valueOf(article.getType(),ArticleType.class);
        articleDTO.setTypeDesc(articleType == null?"未知":articleType.getDesc());
        //用户是否收藏
        UserCollect userCollect = new UserCollect();
        userCollect.setUserId(currentUser.getId());
        userCollect.setArticleId(id);
        userCollect.setStatus(DateStatus.VALID.getCode());
        UserCollect exist = userCollectService.getOne(new QueryWrapper<>(userCollect));
        if (exist != null){
            articleDTO.setFollow(true);
        }else {
            articleDTO.setFollow(false);
        }

        return articleDTO;
    }

    /**
     * 获取公告详细信息
     * @param id 公告id
     * @return
     */
    @GetMapping("/notice-detail")
    public ArticleDTO getNotice(@RequestParam("id") Integer id) {
        return articleService.getNoticeDetailById(id);
    }

    /**
     *  公告
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/notice")
    public IPage<ArticleDTO> notice(@RequestParam("page")Long page, @RequestParam("pageSize")Long pageSize){
        return articleService.getNotice(page,pageSize);
    }

    /**
     *  下乡总结
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/sumup")
    public IPage<BusinessSumupDTO> listSumup(@RequestParam("page")Long page, @RequestParam("pageSize")Long pageSize){
        return businessSumupService.listSumup(page,pageSize);
    }

    /**
     *  下乡总结详情
     * @param id
     * @return
     */
    @GetMapping("/sumup-detail")
    public BusinessSumupDTO sumupDetail(@RequestParam("id")Integer id){
        return businessSumupService.sumupDetail(id);
    }

    /**
     *  我的收藏
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("my-collect")
    public IPage<MyCollectDTO> getMyCollect(@RequestParam("page")Long page, @RequestParam("pageSize")Long pageSize){
        User currentUser = Optional.ofNullable(userService.currentUser()).orElseThrow(() -> of(INVALID_TOKEN));
        return userCollectService.getMyCollect(currentUser.getId(),page,pageSize);
    }

    /**
     *  上传文件
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping("upload")
    public String upload(@RequestParam("file")MultipartFile file){

        String fileName = file.getOriginalFilename();
        fileName = FileUtil.renameFile(fileName);

        try {
            FileUtil.upload(file.getBytes(),uploadConfiguration.getFilePath(),fileName);
        }catch (Exception e){
            Exceptions.throwss("上传文件失败");
        }

        return "/static/" + fileName;
    }

    /**
     *  检查token是否有效
     * @param token
     * @return
     */
    @RequestMapping("check-token")
    public Boolean checkToken(@RequestParam("token")String token){
        User query = new User();
        query.setToken(token);
        User user = userMapper.selectOne(new QueryWrapper<>(query));
        return user != null;
    }

}
