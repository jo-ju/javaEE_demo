package org.example.hrms.config;

import java.lang.annotation.*;

/**
 * 标注需要“管理权限”（ADMIN 或 HR 人事部）才能访问的接口。
 * 可用于方法或整个 Controller 类。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireManage {
}
