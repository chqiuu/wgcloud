package com.wgcloud.agent.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8698319936744959815L;
    private String id;

    private Integer page = 1;

    private Integer pageSize = 20;
}
