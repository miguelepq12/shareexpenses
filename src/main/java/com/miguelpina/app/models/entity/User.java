package com.miguelpina.app.models.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;


@Entity
@Table(name = "users")
public class User implements Serializable{

	public static final long ID_ADMIN_ROLE=1;
	public static final long ID_USER_ROLE=2;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	@Email
	@Column(unique = true)
	private String email;
    
    @Column(unique = true, length = 40)
    @NotEmpty
    private String username;
    
    @Column(length = 60)
    @NotEmpty
    @JsonProperty(access = Access.WRITE_ONLY)
    private String pass;
    
    private String profileImg;
    
    @Temporal(TemporalType.DATE)
	@Column(name="create_at")
    @JsonIgnore
	private Date createAt;

    @ManyToMany
    @JsonIgnore
    private List<Role> roles;
    
    @PrePersist
	void prePersist() {
		createAt=new Date();
	}
    
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getProfileImg() {
		return profileImg;
	}


	public void setProfileImg(String profileImg) {
		this.profileImg = profileImg;
	}

	
	public List<Role> getRoles() {
		return roles;
	}


	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	

	public Date getCreateAt() {
		return createAt;
	}


	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}



	private static final long serialVersionUID = 1L;
}