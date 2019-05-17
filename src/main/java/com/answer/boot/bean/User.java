package com.answer.boot.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/17 15 03
 */
@Data
@Getter
@Setter
public class User {
    private Integer id;
    private String loginName;
    private String pwd;
}
