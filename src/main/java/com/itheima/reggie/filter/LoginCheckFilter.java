package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
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

    // 路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 对请求和响应进行强转，我们需要的是带http的
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1 获取本次请求的URI
        String requestURI = request.getRequestURI();
//        log.info("拦截到请求{}", requestURI);
        // 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        // 2 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3 如果不需要处理，则直接放行
        if (check) {
//            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        // 4 判断登陆状态， 如果已登陆，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
//            log.info("用户已登陆，用户id为{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        // 5 如果未登陆则返回未登陆结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            // 把浏览器发过来的请求和我们定义的不拦截的url做比较，匹配则放行
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
