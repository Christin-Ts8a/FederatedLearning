package com.bcp.serverc.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
public class User {

	private static final long serialVersionUID = 706376374456144622L;

	@Id
	@Column(name = "user_id")
	private Long userId;

	@NotNull
	@Column(name = "username")
	private String username;

	@NotNull
	@Column(name = "password")
	private String password;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "email")
	private String email;

	@Column(name = "telephone")
	private String telephone;

	@Column(name = "org_code")
	private Long orgCode;

	@Column(name = "role_type")
	private Integer roleType;
}