package com.anudipgroupproject.socialize.models;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.anudipgroupproject.socialize.exceptions.PasswordMismatchException;
import com.anudipgroupproject.socialize.forms.UserCreationForm;
import com.anudipgroupproject.socialize.models.fields.MediaFile;
import com.anudipgroupproject.socialize.utils.PasswordHash;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	@Column(name="username", unique=true, nullable=false)
	private String username;

	@Column(name="displayname")
	private String displayname;

	@JsonIgnore
	@Column(name="password", length=25, nullable=false)
	private String password;

	@Column(name="mobile_no", length=20)
	private String mobile;

	@Column(name="email_id")
	private String email;

	@Type(value=MediaFile.class, parameters={ @Parameter(name="folderName", value="profile_image") })
	@Column(name="profile_image")
	private File image;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_on")
	private Date created_on;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_login")
	private Date last_login;

	/**
	 * If the user is active in their session, this value will be set to TRUE.
	 * This can be achieved using AJAX. A signal is passed from the client to the server to update this value.
	 */
	@Column(name="is_active", columnDefinition="BIT(1) DEFAULT FALSE")
	private boolean is_active;

	/**
	 * Indicates whether the user is deleted or not.
	 * By default, the value is set to FALSE, indicating that the user is not deleted.
	 */
	@Column(name="is_deleted", columnDefinition="BIT(1) DEFAULT FALSE")
	private boolean is_deleted;

	@OneToMany(mappedBy="user")
	private List<Post> posts;

	@PrePersist
	protected void onCreate() {
		if (this.getDisplayname() == null) {
			this.setDisplayname(this.getUsername());
		}
	}

	// Default constructor
	public User() {
		// Automatically set the date when the object is saved on create
		this.created_on = new Date();
	}

	// Getters and setters for the class properties
	public Long getId() {
		return id;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String newPassword, String confirmPassword) {
		if (!newPassword.equals(confirmPassword)) {
			throw new PasswordMismatchException();
		}
		this.password = newPassword;
	}
	public void setPassword(String password) {
		this.password = PasswordHash.make(password);
	}

	public File getImage() {
		return this.image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getCreatedOn() {
		return created_on;
	}

	public void setLastLogin(Date datetime) {
		this.last_login = datetime;
	}

	public Date getLastLogin() {
		return last_login;
	}

	public boolean getIsActive() {
		return is_active;
	}

	public void setIsActive(boolean is_active) {
		this.is_active = is_active;
	}

	public boolean getIsDeleted() {
		return is_deleted;
	}

	public void setIsDeleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	public Map<Long, Post> getPosts() {
		Map<Long, Post> postMap = new HashMap<>();
		for (Post post: this.posts) {
			postMap.put(post.getId(), post);
		}
		return postMap;
	}

	@Override
	public String toString() {
		return String.format("User(id=%s, username=%s, created_on=%s)", id, username, created_on);
	}

	public void copy(User user) {
		if (user.getUsername() != null) this.setUsername(user.getUsername());
		if (user.getDisplayname() != null) this.setDisplayname(user.getDisplayname());
		if (user.getMobile() != null) this.setMobile(user.getMobile());
		if (user.getEmail() != null) this.setEmail(user.getEmail());
		if (user.getImage() != null) this.setImage(user.getImage());
	}

	@JsonIgnore
	public UserCreationForm getForm() {
		UserCreationForm form = new UserCreationForm();
		form.setUsername(this.getUsername());
		form.setDisplayname(this.getDisplayname());
		form.setMobile(this.getMobile());
		form.setEmail(this.getEmail());
		//		form.setImage(this.getImage())
		return form;
	}
}