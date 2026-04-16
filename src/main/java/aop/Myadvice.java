package aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Myadvice {
    @Pointcut("execution(* dao.StudentDaoImpi.update(..))")
    private void pt(){}
    @Before("pt()")
    public void method(){
        System.out.println("方法执行前，系统时间："+System.currentTimeMillis());
    }
}
