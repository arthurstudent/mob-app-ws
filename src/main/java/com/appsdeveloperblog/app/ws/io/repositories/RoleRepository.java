package com.appsdeveloperblog.app.ws.io.repositories;

import com.appsdeveloperblog.app.ws.io.entity.RoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
    // try with r
    @Query(value = "SELECT * FROM Roles r WHERE r.name IN (?1)", nativeQuery = true)
    Set<RoleEntity> findAllRoles(Set<String> roles);
}
