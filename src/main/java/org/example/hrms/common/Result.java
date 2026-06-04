package org.example.hrms.common;

import lombok.Data;

/** 统一返回结果 */
@Data
public class Result {
    private Integer code;   // 200 成功, 其它失败
    private String msg;
    private Object data;

    public static Result ok() {
        return ok(null);
    }

    public static Result ok(Object data) {
        Result r = new Result();
        r.code = 200;
        r.msg = "操作成功";
        r.data = data;
        return r;
    }

    public static Result ok(String msg, Object data) {
        Result r = new Result();
        r.code = 200;
        r.msg = msg;
        r.data = data;
        return r;
    }

    public static Result fail(String msg) {
        return fail(500, msg);
    }

    public static Result fail(Integer code, String msg) {
        Result r = new Result();
        r.code = code;
        r.msg = msg;
        return r;
    }
}
