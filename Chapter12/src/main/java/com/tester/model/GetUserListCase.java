package com.tester.model;

import lombok.Data;

@Data
public class GetUserListCase {
    private String userName;
    //private String age;
    private int age;
    private String sex;
    private String expected;//预期结果

}
