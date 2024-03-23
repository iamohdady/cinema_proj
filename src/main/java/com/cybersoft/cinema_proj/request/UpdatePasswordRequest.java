package com.cybersoft.cinema_proj.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class UpdatePasswordRequest {

    @JsonProperty("username")
    public String username;

    @JsonProperty("old_password")
    public String oldPassword;

    @JsonProperty("new_password")
    public String newPassword;

    @JsonProperty("confirm_new_password")
    public String confirmPassword;

    public boolean isValid() {
        return StringUtils.hasLength(username)
            && StringUtils.hasLength(oldPassword)
            && StringUtils.hasLength(newPassword)
            && StringUtils.hasLength(confirmPassword);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
