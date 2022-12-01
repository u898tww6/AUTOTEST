package com.tester.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String userName;
    private String password;
    private int age;
    //private String age;
    private String sex;
    private String permission;//权限
    private String isDelete;//是否删除

    //复写toString方法,处理json文件，这样比较准
    @Override
    public String toString(){
        return (
                "id:"+id+","+
                "userName:"+userName+","+
                "password:"+password+","+
                "age:"+age+","+
                "sex:"+sex+","+
                "permission:"+permission+","+
                "isDelete:"+isDelete+"}"
                );
    }
}
