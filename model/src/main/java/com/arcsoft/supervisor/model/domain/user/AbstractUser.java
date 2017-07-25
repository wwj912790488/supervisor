package com.arcsoft.supervisor.model.domain.user;

import javax.persistence.*;

/**
 *  Abstract base class defines commonly fields and properties for user.
 *
 * @author zw.
 */
@MappedSuperclass
public class AbstractUser {

    /**
     * The identifier of administrator user.
     */
    public static final int ID_ADMINISTRATOR = 1;

    /**
     * The constant value of administrator role.
     */
    public static final int ROLE_MANAGER = 1;

    /**
     * The constant value of operator role.
     */
    public static final int ROLE_OPERATOR = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The identify string */
    private String userName;

    /** A md5 string */
    private String password;

    @Transient
    private String newPassword;

    @Column(name = "real_name")
    private String realName;

    private Integer role;

    public AbstractUser() {}

    public AbstractUser(String userName, String password) {
        this(userName, password, 1);
    }

    public AbstractUser(String userName, String password, Integer role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.realName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * Checks the user is <tt>administrator</tt> or not.
     *
     * @return {@code true} if the value of user id is {@code 1} otherwise {@code false}
     */
    public boolean isSuperAdmin() {
        return id == ID_ADMINISTRATOR;
    }

    /**
     * Checks the user is <tt>operator</tt> or not.
     *
     * @return {@code true} if the role of user is {@code 0} otherwise {@code false}
     */
    public boolean isOperator() {
        return role != null && role == ROLE_OPERATOR;
    }

    /**
     * Checks the user is <tt>manager</tt> or not.
     *
     * @return {@code true} if the role of user is {@code 1} otherwise {@code false}
     */
    public boolean isManager() {
        return role != null && role == ROLE_MANAGER;
    }
}
