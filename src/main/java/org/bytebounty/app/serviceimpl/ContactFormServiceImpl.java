package org.bytebounty.app.serviceimpl;

import org.bytebounty.app.model.ContactForm;
import org.bytebounty.app.repository.ContactFormRepository;
import org.bytebounty.app.service.ContactFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("contactFormService")
public class ContactFormServiceImpl implements ContactFormService {

    @Autowired
    ContactFormRepository contactFormRepository;

    @Override
    public ContactForm save(ContactForm contact) {
        return contactFormRepository.save(contact);
    }
    
}
