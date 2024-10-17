package org.bytebounty.app.controller;

import org.bytebounty.app.model.ContactForm;
import org.bytebounty.app.model.Email;
import org.bytebounty.app.service.ContactFormService;
import org.bytebounty.app.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;




@Controller
public class HomeController {

    @Autowired
    ContactFormService contactFormService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String home(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);

        return "homeView/home";

    }


    @GetMapping("/about")
    public String about(Model model) {

        return "redirect:/#about";

    }

    @GetMapping("/qualification")
    public String qualification(Model model) {

        return "redirect:/#qualification";

    }

    @GetMapping("/experience")
    public String experience(Model model) {

        return "redirect:/#experience";

    }
    
    @GetMapping("/skill")
    public String skill(Model model) {

        return "redirect:/#skill";

    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {

        return "redirect:/#portfolio";

    }

    @GetMapping("/contact")
    public String contact(Model model) {

        return "redirect:/#contact";

    }

    @PostMapping("/home/contact")
    public String contactForm(@Valid @ModelAttribute ContactForm contactForm, BindingResult result) {

        if (result.hasErrors()) {
            return "homeView/home";
        }

        contactFormService.save(contactForm);

        Email email = new Email();
        email.setRecipient("mhernandez80@gmail.com");
        email.setSubject(contactForm.getSubject());
        email.setMsgBody(
            "<h1>Message from:"  + "</h1>" +
            "<p><ul><li>email: " + contactForm.getEmail() + "</li><li>name: "+ contactForm.getName() + "</li></ul>"+
            "<h1> Subject: </h1><ul><li>" + contactForm.getSubject() + "</li></ul>" + 
            "<h1> Message body:</h1><p>" + contactForm.getMessage() + "</p>"
        );
        emailService.sendSimpleEmail(email);
        
        return "redirect:/#contact";
    }

}
