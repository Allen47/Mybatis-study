package com.msq.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Msq
 * @date 2021/5/13 - 16:54
 */
@Data
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private String pwd;
}
