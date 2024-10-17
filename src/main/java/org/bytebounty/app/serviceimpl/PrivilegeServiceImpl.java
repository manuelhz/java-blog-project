package org.bytebounty.app.serviceimpl;

import org.bytebounty.app.model.Privilege;
import org.bytebounty.app.repository.PrivilegeRepository;
import org.bytebounty.app.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("privilegeService")
public class PrivilegeServiceImpl implements PrivilegeService {

    @Autowired
    PrivilegeRepository privilegeRepository;

    @Override
    public Privilege save(Privilege privilege) {
        
        return privilegeRepository.save(privilege);
    }

    @Override
    public Privilege findByName(String name) {
        return privilegeRepository.findByName(name);
    }
    
}

