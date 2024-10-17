package org.bytebounty.app.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.bytebounty.app.model.Account;
import org.bytebounty.app.model.Email;
import org.bytebounty.app.model.Password;
import org.bytebounty.app.service.AccountService;
import org.bytebounty.app.service.EmailService;
import org.bytebounty.app.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {

    @Value("${password.token.reset.timeout.minutes}")
    private int PASSWORD_TOKEN_RESET_TIME_OUT;

    @Value("${site.domain}")
    private String SITE_DOMAIN;

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordService passwordService;

    @Autowired
    EmailService emailService;


    // reset password
    @GetMapping("/reset-password")
    @PreAuthorize("isAuthenticated()")
    public String resetPassword(
        Model model
        ) {
        Password password = new Password();        
        model.addAttribute("password", password);
        return "accountView/resetPassword";
    }
    @PostMapping("/reset-password")
    public String resetPasswordPost(  
        @ModelAttribute Password password,
        @RequestParam("oldPassword") char[] oldPassword,
        RedirectAttributes attributes,
        Principal principal
    ) {
        String authAccount;
        if (principal != null) {
            authAccount = principal.getName();
            Optional<Account> optionalAccount = accountService.findOneByEmail(authAccount);
            Account account= optionalAccount.get();
            Password currentPassword = account.getPassword();
            if (!passwordService.checkPassword(currentPassword, oldPassword)) {
                
                attributes.addFlashAttribute("error", "Password invalid");
                return "redirect:/reset-password";
            }
            
            currentPassword.setWord(password.getWord());
            passwordService.save(currentPassword);
            SecurityContextHolder.getContext().setAuthentication(null);
            SecurityContextHolder.clearContext();
            attributes.addFlashAttribute("message", "Your password has been changed successfully.");
            return "redirect:/login";
        } else {
            return "redirect:/?error";
        }
    }

    // Forgot password
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "accountView/forgotPassword";
    }
    @PostMapping("/forgot-password")
    public String forgotPasswordPost(
        @RequestParam("email") String email,
        RedirectAttributes attributes,
        Model model
    ) {
        Optional<Account> optionalAccount = accountService.findOneByEmail(email);

        if (optionalAccount.isPresent()) {
            

            Account account = accountService.findOneById(optionalAccount.get().getAccountId()).get();

            Password password = account.getPassword();

            String resetToken = UUID.randomUUID().toString();
            password.setResetToken(resetToken);
            password.setResetTokenExpiry(LocalDateTime.now().plusMinutes(PASSWORD_TOKEN_RESET_TIME_OUT));
            passwordService.save(password);

            Email emailMsg = new Email();
            emailMsg.setRecipient(account.getEmail());
            emailMsg.setSubject("Reset Account");
            emailMsg.setMsgBody(
                "<h1>Byte Bounty Reset Password<h1>"+
                "<p><a href=\"" + SITE_DOMAIN + "/reset-forgotten-password?token=" + resetToken + 
                "\">This link</a> is to reset your password and it will last for 10 minutes</p>"
            );

            if (emailService.sendSimpleEmail(emailMsg) == false) {
                attributes.addFlashAttribute("error", "error while sendidng email ...");
                return "redirect:/forgot-password";
            }
            
            attributes.addFlashAttribute("message", "password reset email sent");

            return "redirect:/login";

        } else {

            attributes.addFlashAttribute("error", "user not found with the email supplied");
            return "redirect:/forgot-password";

        }
    }
    // reset forgoten password
    @GetMapping("/reset-forgotten-password")
    public String resetForgottenPassword(
        Model model,
        RedirectAttributes attributes,
        @RequestParam("token") String token
    ) {
        if (token.equals("")) {
            attributes.addFlashAttribute("error", "Invalid token");
            return "redirect:/forgot-password";
        }
        Optional<Password> optionalPassword = passwordService.findByResetToken(token);
        if (optionalPassword.isPresent()){
            Password password = passwordService.findById(optionalPassword.get().getPasswordId()).get();
            LocalDateTime now = LocalDateTime.now();

            // if token is expired
            if (now.isAfter(password.getResetTokenExpiry())) {
                attributes.addAttribute("error", "Token expired");
                return "redirect:/forgot-password";

            }
            model.addAttribute("password", password);
            return "accountView/resetForgottenPassword";

        } else {
            attributes.addFlashAttribute("error", "Invalid token");
            return "redirect:/forgot-password";
        }
    }
    @PostMapping("/reset-forgotten-password")
    public String resetForgottenPasswordPost(
        @ModelAttribute Password password,
        RedirectAttributes attributes
    ) {
        Optional<Password> optionalPassword = passwordService.findByResetToken(password.getResetToken());
        if (optionalPassword.isPresent()) {

            Password currentPassword = optionalPassword.get();
            currentPassword.setWord(password.getWord());
            currentPassword.setResetToken("");
            passwordService.save(currentPassword);
            attributes.addAttribute("message", "Password updated");

            return "redirect:/login";



        } else {
            attributes.addFlashAttribute("error", "Invalid token");
            return "redirect:/forgot-password";
        }
    }

    
}