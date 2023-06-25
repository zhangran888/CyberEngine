package com.datacyber.config;

import lombok.Data;

import java.io.Serializable;


@Data
public class BaseJdbcConnParam implements Serializable {

    /**
     * driver name
     */
    private String driverName;

    /**
     * IP
     */
    private String ip;

    /**
     * db server port
     */
    private Integer port;

    /**
     * db name
     */
    private String dbName;

    /**
     * db connection username
     */
    private String username;

    /**
     * db connection password
     */
    private String password;
}
