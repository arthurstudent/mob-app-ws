package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class UserPrincipal implements UserDetails {

    private final UserEntity userEntity;

    private final String publicUserId;

    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.publicUserId = userEntity.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Set<AuthorityEntity> authorityEntities = new HashSet<>();

        List<RoleEntity> roles = userEntity.getRoles();

        if (roles == null) return grantedAuthorities;

        roles.forEach((roleEntity -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(roleEntity.getName()));
            authorityEntities.addAll(roleEntity.getAuthorities());
        }));

        authorityEntities.forEach((authorityEntity -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
        }));

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.userEntity.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return this.userEntity.getEmail();
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
        return this.userEntity.getEmailVerificationStatus();
    }
}
