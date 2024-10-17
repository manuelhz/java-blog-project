package org.bytebounty.app.service;

import org.bytebounty.app.model.ContactForm;
import org.springframework.stereotype.Service;

@Service("contactFormService")
public interface ContactFormService {
    
    ContactForm save(ContactForm contact);
    
}
