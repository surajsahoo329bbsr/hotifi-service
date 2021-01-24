package com.api.hotifi.identity.services.implementations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class HotifiUserDetailsImpl implements UserDetails {

    private boolean isEnabled;
    private String username;
    private String password;
    private List<UserRole> roles;
    private List<GrantedAuthority> grantedAuthorities;

    public HotifiUserDetailsImpl(String username, String password, boolean isEnabled, List<GrantedAuthority> grantedAuthorities) {
        this.username = username;
        this.password = password;
        this.isEnabled = isEnabled;
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    @Getter
    @Setter
    public static class UserRole {
        private String name;
        private String description;
    }
}