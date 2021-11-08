package com.example.demo.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Moon Wu
 * @date 2021/5/8
 */
public class EntityParent extends EntityParent2{
    @Id
    private Long id;
}
