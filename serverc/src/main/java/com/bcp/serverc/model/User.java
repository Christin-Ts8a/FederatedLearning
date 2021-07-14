package com.bcp.serverc.model;

import java.util.Collection;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.swagger.annotations.ApiModelProperty;

public class User implements UserDetails {

	private static final long serialVersionUID = 706376374456144622L;

	@Id
	@Column(name = "user_id")
	@NotBlank(message = "userId不能为空")
	private String userId;

	@Column(name = "nickname")
	private String nickname;

	private String password;

	@Transient
	@ApiModelProperty(hidden = true)
	private Boolean locked = false;

	@Transient
	@ApiModelProperty(hidden = true)
	private Boolean enabled = true;

	/**
	 * @return user_id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId == null ? null : userId.trim();
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// 实现UserDetails方法
	@Override
	@ApiModelProperty(hidden = true)
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	/**
	 * userId为自定义不是自动生成，所以在这里返回id而不是name
	 */
	@Override
	@ApiModelProperty(hidden = true)
	public String getUsername() {
		// TODO Auto-generated method stub
		return userId;
	}

	@Override
	@ApiModelProperty(hidden = true)
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	@ApiModelProperty(hidden = true)
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return !locked;
	}

	@Override
	@ApiModelProperty(hidden = true)
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}
}