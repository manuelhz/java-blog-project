package org.bytebounty.app.service;

import org.bytebounty.app.model.Email;
import org.springframework.stereotype.Service;

@Service("emailService")
public interface EmailService {
    
    public boolean sendSimpleEmail(Email email);
    
    
    
}
