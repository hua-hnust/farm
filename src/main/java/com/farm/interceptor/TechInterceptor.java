package com.farm.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.farm.constants.Errors;
import com.farm.constants.UserType;
import com.farm.entity.User;
import com.farm.mapper.UserMapper;
import com.farm.util.Exceptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 技术员拦截器
 * @Author xhua
 * @Date 2020/3/28 14:38
 **/
@Component
public class TechInterceptor extends AbstractInterceptor {

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 是否免登录校验
        if (super.isOpenApi(handler)) {
            return true;
        }
        if (!doCheck(request, response)){
            Exceptions.throwss("当前登录用户没有操作权限");
        }
        return true;
    }

    /**
     * 鉴权逻辑
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean doCheck(HttpServletRequest request, HttpServletResponse response) {
        if (SessionContext.getRemoteSid() == null) {
            Exceptions.throwss(Errors.INVALID_TOKEN);
        }
        if (!verifyToken()) {
            Exceptions.throwss(Errors.INVALID_TOKEN);
        }
        //是否是技术员
        User query = new User();
        query.setToken(SessionContext.getRemoteSid());
        User user = userMapper.selectOne(new QueryWrapper<>(query));
        //技术员或者管理员权限
        return user.getType().equals(UserType.MANAGE.getCode()) || user.getType().equals(UserType.ADMIN.getCode());
    }
}
