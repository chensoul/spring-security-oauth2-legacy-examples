package com.chensoul.oauth2.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 */
@Data
public class LoggedUser implements Serializable {
    private Long id;
    private String name;
    private String username;
    private String password;
    private Collection<String> permissions = new HashSet<>();
}
