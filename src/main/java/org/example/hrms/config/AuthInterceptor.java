package org.example.hrms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.hrms.entity.SysUser;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录与权限拦截器：
 *  - 所有 /api/** 接口必须登录（登录接口本身除外，已在注册时排除）
 *  - 标注 @RequireManage 的接口要求 ADMIN 或 HR(人事部) 角色
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 放行非控制器方法（静态资源等）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        HttpSession session = request.getSession();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser == null) {
            writeJson(response, 401, "未登录或登录已过期");
            return false;
        }

        // 是否需要管理权限
        boolean requireManage = handlerMethod.hasMethodAnnotation(RequireManage.class)
                || handlerMethod.getBeanType().isAnnotationPresent(RequireManage.class);
        if (requireManage && !isManager(loginUser)) {
            writeJson(response, 403, "无操作权限，仅人事部/管理员可管理");
            return false;
        }
        return true;
    }

    private boolean isManager(SysUser user) {
        return "ADMIN".equals(user.getRole()) || "HR".equals(user.getRole());
    }

    private void writeJson(HttpServletResponse response, int code, String msg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        String json = "{\"code\":" + code + ",\"msg\":\"" + msg + "\"}";
        response.getWriter().write(json);
    }
}
