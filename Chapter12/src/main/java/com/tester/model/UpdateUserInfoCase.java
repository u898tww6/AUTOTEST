package com.tester.model;

import lombok.Data;

@Data
public class UpdateUserInfoCase {
    private int id;
    private int userId;
    private String userName;
    private String sex;
    //private String age;
    private int age;
    private String permission;//权限
    private String isDelete;
    private String expected;

}
