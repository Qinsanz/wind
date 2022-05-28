package com.worth.wind.common.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.worth.wind.blog.dto.UserDetailDTO;
import com.worth.wind.blog.entity.UserAuth;
import com.worth.wind.blog.service.RedisService;
import com.worth.wind.blog.service.impl.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;


/**
 * 用户工具类
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Component
public class UserUtils {

    private static final String USER_INFO="USER_INFO:";

    /**
     * 获取当前登录用户
     *
     * @return 用户登录信息
     */
    public static UserDetailDTO getLoginUser() {
        try {
            return (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取cookie中的 JSESSIONID
     * @return
     */
    public static String getJsessionid(HttpServletRequest request){
        // 将登录信息放入springSecurity管理
        request.getParameter("cookies");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 存用户信息 到redis
     * @param redisService
     */
    private static void setUserDetailDTO(RedisService redisService,String authKey){
        UserDetailDTO dto=getLoginUser();
        if(!(dto!=null&&!redisService.hasKey(authKey))){return;}
        UserAuth userAuth=BeanCopyUtils.copyObject(dto,UserAuth.class);
        redisService.set(authKey, JSON.toJSON(userAuth),60*60*24);

        //缓存当前用户信息jsessionid集合 方便退出时统一删除
        String userInfoKey=USER_INFO+userAuth.getUsername();
        redisService.sAdd(userInfoKey,authKey);
    }

    /**
     * 从redis取用户信息 存到Authentication
     * @param request
     */
    public static void setAuthentication(HttpServletRequest request, RedisService redisService, UserDetailsServiceImpl userDetailsService){
        String jsessionid=getJsessionid(request);
        if(StringUtils.isBlank(jsessionid)){return;}
        //存用户信息
        String authKey=USER_INFO+jsessionid;
        setUserDetailDTO(redisService,authKey);
        //判断用户信息 获取不到用户信息的时候存
        if(!(getLoginUser()==null && redisService.hasKey(authKey))){
            return;
        }
        //从redis取用户信息 存到Authentication
        UserAuth userAuth=JSON.parseObject(String.valueOf(redisService.get(authKey)),UserAuth.class);
        UserDetailDTO userDetailDTO=userDetailsService.convertUserDetail(userAuth, request);
        if(userDetailDTO==null){return;}
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetailDTO, null, userDetailDTO.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * 退出清空缓存
     * @param redisService
     */
    public static void delAuthentication( HttpServletRequest request,RedisService redisService){
        String jsessionid=getJsessionid(request);
        if(StringUtils.isBlank(jsessionid)){return;}
        String authKey=USER_INFO+jsessionid;
        UserAuth userAuth=JSON.parseObject(String.valueOf(redisService.get(authKey)),UserAuth.class);
        if(userAuth==null){return;}
        String userInfoKey=USER_INFO+userAuth.getUsername();
        Set<Object> userNameSet = redisService.sMembers(userInfoKey);
        if(CollectionUtils.isEmpty(userNameSet)){return;}
        for (Object o : userNameSet) {
            String oKey=String.valueOf(o);
            if(redisService.hasKey(oKey)){
                redisService.del(oKey);
            }
        }
        if(redisService.hasKey(userInfoKey)){
            redisService.del(userInfoKey);
        }
        SecurityContextHolder.getContext().setAuthentication(null);
    }



}
