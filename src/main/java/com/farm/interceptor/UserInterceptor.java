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

/** 普通用户拦截器
 * @Author xhua
 * @Date 2020/3/28 15:06
 **/
@Component
public class UserInterceptor extends AbstractInterceptor{

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 是否免登录校验
        if (super.isOpenApi(handler)) {
            return true;
        }

        return doCheck(request, response);
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
        return user.getType().equals(UserType.GENERAL_USER.getCode());
    }

}
