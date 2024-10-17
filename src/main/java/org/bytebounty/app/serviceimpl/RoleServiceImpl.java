package org.bytebounty.app.serviceimpl;

import java.util.List;

import org.bytebounty.app.model.Role;
import org.bytebounty.app.repository.RoleRepository;
import org.bytebounty.app.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    
}