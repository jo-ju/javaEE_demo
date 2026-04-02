package instance;

public class BeanClass {
    public String message;
    public BeanClass(){
        message = "构建方法实例化Bean";
    }
    public BeanClass(String s){
        message = s;
    }
}
