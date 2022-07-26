package com.bdqn.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.bdqn.reggie.common.BaseContext;
import com.bdqn.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 员工登录拦截器
 */
@Slf4j
@WebFilter(filterName = "logincheckfilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的URL
        String requestURI = request.getRequestURI();

        log.info("拦截到请求:{}",requestURI);

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/backend/page/login/login.html",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        //判断本次请求是否需要处理
        Boolean check = check(urls, requestURI);

        //如果不需要处理直接放行
        if (check){
            log.info("本次请求:{}===不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户ID:{}===已经登录,直接放行",request.getSession().getAttribute("employee"));

            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee")) ;

            long threadId = Thread.currentThread().getId();
            log.info("线程ID为:{}",threadId);

            filterChain.doFilter(request,response);
            return;
        }

        //判断手机用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户ID:{}===已经登录,直接放行",request.getSession().getAttribute("user"));

//            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user")) ;

            long userId = Thread.currentThread().getId();
            log.info("线程ID为:{}",userId);

            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录则放回未登录结果，并且通过输出流的方式想客户端发送响应
        log.info("用户没有登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配，检查本次请求是否放行
     * @param urls
     * @param requstURI
     * @return
     */
    public Boolean check(String[] urls , String requstURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requstURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
