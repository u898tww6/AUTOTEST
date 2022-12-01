package com.tester.model;

import lombok.Data;

@Data
public class AddUserCase {
    private String userName;
    private String password;
    //private String age;
    private int age;
    private String sex;
    private String permission;//权限
    private String isDelete;//是否删除
    private String expected;//预期结果
}
