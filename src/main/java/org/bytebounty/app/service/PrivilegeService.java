package org.bytebounty.app.service;

import org.bytebounty.app.model.Privilege;
import org.springframework.stereotype.Service;

@Service("privilegeService")
public interface PrivilegeService {

    public Privilege save(Privilege privilege);

    public Privilege findByName(String name);

    
}

