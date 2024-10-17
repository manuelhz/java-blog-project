package org.bytebounty.app.service;

import java.util.List;

import org.bytebounty.app.model.Role;
import org.springframework.stereotype.Service;

@Service("roleService")
public interface RoleService {

    public Role save(Role role);

    public Role findByName(String name);

    public List<Role> findAll();    

}
