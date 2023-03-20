package com.aorez.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.aorez.reggie.common.BaseContext;
import com.aorez.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //uri匹配
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //doFilter，update，updateFill三个方法是同一个线程
        log.info("thread id " + Thread.currentThread().getId());

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();

        String[] uris = {"/employee/login", "/employee/logout", "/backend/**", "/front/**",
                "/common/**",
                "/user/sendMsg", "/user/login"};
        boolean match = matchPath(uris, uri);

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //路径不需要检查登录状态
        //直接放行
        if (match) {
            filterChain.doFilter(request, response);
            log.info("doFilter match");
            return;
        }

        //已登录则放行
        Long id = null;
        if ((id = (Long) request.getSession().getAttribute("employee")) != null) {
            //每次请求都会经过doFilter
            //因此直接在这里设置ThreadLocal
            //设置ThreadLocal要在放行的前面
            BaseContext.setUserId(id);

            filterChain.doFilter(request, response);
            log.info("doFilter login");

            return;
        }

        //移动端用户登录
        Long userId = null;
        if ((userId = (Long) request.getSession().getAttribute("user")) != null) {
            BaseContext.setUserId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        //由于前端中有对回写数据的检查，因此未登录则回写数据
        log.info("doFilter no login");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean matchPath(String[] uris, String uri) {
        for (String s : uris) {
            if (MATCHER.match(s, uri)) {
                return true;
            }
        }

        return false;
    }
}
