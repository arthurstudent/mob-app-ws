package com.appsdeveloperblog.app.ws;

import com.appsdeveloperblog.app.ws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.io.repositories.AuthorityRepository;
import com.appsdeveloperblog.app.ws.io.repositories.RoleRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class InitialUserSetUp {

    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    public InitialUserSetUp(AuthorityRepository authorityRepository, RoleRepository roleRepository) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        AuthorityEntity read = createAuthority("READ_AUTHORITY");
        AuthorityEntity write = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity delete = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole("ROLE_USER", Arrays.asList(read, write));
        RoleEntity roleAdmin = createRole("ROLE_ADMIN", Arrays.asList(read, write, delete));
    }

    private AuthorityEntity createAuthority(String name) {
        AuthorityEntity authorityEntity = authorityRepository.findByName(name);
        if (authorityEntity == null) {
            authorityEntity = new AuthorityEntity(name);
            authorityRepository.save(authorityEntity);
        }
        return authorityEntity;
    }

    private RoleEntity createRole(String name, List<AuthorityEntity> authorityEntities) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorityEntities);
            roleRepository.save(role);
        }
        return role;
    }
}
