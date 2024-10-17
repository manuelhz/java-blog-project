package org.bytebounty.app.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Account;
import org.bytebounty.app.model.Password;
import org.bytebounty.app.model.Role;
import org.bytebounty.app.security.SecurityService;
import org.bytebounty.app.service.AccountService;
import org.bytebounty.app.service.EmailService;
import org.bytebounty.app.service.PasswordService;
import org.bytebounty.app.service.RoleService;
import org.bytebounty.app.util.FileStorage;
import org.bytebounty.app.validators.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;




@Controller
public class AccountController { 
    

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordService passwordService;

    @Autowired
    EmailService emailService;

    @Autowired
    RoleService roleService;

    @Autowired
    SecurityService securityService;

    @GetMapping("/login")
    public String login(Model model,
    @RequestParam(required = false) String error,
    RedirectAttributes attributes) {
        if (error != null) {

            attributes.addFlashAttribute("error", "Wrong user or password");
            return "redirect:/login";

        } else {
            return "accountView/login";
        }
    }


    // edit account
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model, Principal principal) {
        String authUser = "";
        if (principal != null) {
            authUser = principal.getName();
        }
        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            model.addAttribute("account", account);
            

            return "accountView/profile";
        } else {
            return "redirect:/?error";
        }
    }

    // edit post processing
    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(
        @RequestParam("file") MultipartFile file,        
        Principal principal,
        @Valid @ModelAttribute Account account,
        BindingResult result,
        RedirectAttributes attributes
    ) {
        if (result.hasErrors()) {
            return "accountView/profile";
        }

        String authUser = "";
        if(principal != null) {
            authUser = principal.getName();
        }

        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
        
        if (optionalAccount.isPresent()) {
            Account accountByEmail = optionalAccount.get();
            Account accountById  = accountService.findOneById(accountByEmail.getAccountId()).get();
            if(!file.isEmpty()) {
                if (!accountById.getProfilePicture().equals("/img/userImg/default.svg") ) {
                    FileStorage.delete(accountById.getProfilePicture());
                }
                String fileValidator = FileValidator.validateFile(file);
                if (!fileValidator.equals("OK")) {
                    attributes.addFlashAttribute("error", fileValidator);
                    return "redirect:/profile";
                }

                accountById.setProfilePicture(FileStorage.save(file));
                FileStorage.waitt();
            }
            accountById.setEmail(account.getEmail());
            accountById.setFirstName(account.getFirstName());
            accountById.setLastName(account.getLastName());
            accountById.setMobile(account.getMobile());
            accountById.setWebsite(account.getWebsite());
            accountService.save(accountById);
            return "redirect:/";

        } else {
            return "redirect:/?error";
        }
    }

    // Create a new account
    @GetMapping("/register")
    public String register(Model model) {

        Password password = new Password();
        Account account = new Account();        
        account.setRoles(new HashSet<Role>(Arrays.asList(roleService.findByName("ROLE_SUBSCRIBER"))));
        model.addAttribute("password", password);
        model.addAttribute("account", account);

        return "accountView/register";
    }
    @PostMapping("/register")
    public String registerPost(
        @RequestParam("file") MultipartFile file,
        @Valid @ModelAttribute Account account,
        BindingResult resultAccount,
        @Valid @ModelAttribute Password password,
        BindingResult resultPassword        
    ) {
        // check that email is not already taken
        Optional<Account> optionalAccount = accountService.findOneByEmail(account.getEmail());
        if(optionalAccount.isPresent()) {
            resultAccount.rejectValue("email","error.email", "This email address is already in use");            
        }

        // checking both resultAccount or resultPassword don't have errors
        if (resultAccount.hasErrors() | resultPassword.hasErrors()) {

            return "accountView/register";
        }

        if(!file.isEmpty()) {
            System.out.println("file: " + file);
            account.setProfilePicture(FileStorage.save(file, "userImg"));
            FileStorage.waitt();
        }
        passwordService.save(password);
        account.setPassword(password);
        accountService.save(account);
        return "redirect:/";
    }

    
    // accounts
    @GetMapping("/accounts")
    @PreAuthorize("@securityService.hasPrivilege('EDIT_USERS', 'DELETE_USERS')")
    public String accounts(Model model) {
        List<Account> accounts = accountService.findAll();
        model.addAttribute("accounts", accounts);
        return "accountView/accounts";
    }

    // delete account
    @GetMapping("/accounts/{accountId}/delete")
    @PreAuthorize("@securityService.hasPrivilege('DELETE_USERS')")
    public String deleteAccount(@PathVariable Long accountId, Model model) {

        Optional<Account> optionalAccount = accountService.findOneById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (!account.getProfilePicture().equals("/img/userImg/default.svg") ) {
                FileStorage.delete(account.getProfilePicture());
            }
            accountService.delete(account);
            return "redirect:/accounts";
        } else {
            return "404";
        }
    }

    // delete my own account
    @GetMapping("/accounts/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteMyOwnAccount() {

        return "accountView/deleteMyOwnAccount";

    }
    @PostMapping("/accounts/delete")
    public String deleteMyOwnAccountPost(
        Principal principal,
        @RequestParam("password") char[] inputPassword,
        RedirectAttributes attributes) {

            if (principal != null) {
               String authUser=principal.getName();

                Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
                if (optionalAccount.isPresent()) {
                    Account account = optionalAccount.get();
                    Password password = account.getPassword();
                    if (!passwordService.checkPassword(password, inputPassword)) {
                        
                        attributes.addFlashAttribute("error", "Password invalid");
                        return "redirect:/accounts/delete";
                    } else {

                        //delete profile picture
                        if (!account.getProfilePicture().equals("/img/userImg/default.svg") ) {

                            FileStorage.delete(account.getProfilePicture());
                            
                        }

                        //delete account
                        accountService.delete(account);

                        // log out user
                        SecurityContextHolder.getContext().setAuthentication(null);
                        SecurityContextHolder.clearContext();

                        // return success message
                        attributes.addFlashAttribute("message", "Account deleted successfully");                        
                        return "redirect:/accounts/delete";
                    }

                } else {

                    return "404";
               }
            } else {

                return "error";

            }
    }

    

    // edit account
    @GetMapping("/accounts/{accountId}/edit")
    @PreAuthorize("@securityService.hasPrivilege('EDIT_USERS','DELETE_USERS')")    
    public String editAccount(@PathVariable Long accountId, Model model) {
        Optional<Account> optionalAccount = accountService.findOneById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            model.addAttribute("account", account);
            List<Role> allRoles = roleService.findAll();
            model.addAttribute("allRoles", allRoles);
            return "accountView/editAccount";
        } else {
            return "404";
        }
    }
    @PostMapping("/accounts/edit")
    @PreAuthorize("@securityService.hasPrivilege('EDIT_USERS', 'DELETE_USERS')")
    public String editAccount(
        @RequestParam("file") MultipartFile file,
        Model model,
        @Valid @ModelAttribute Account receivedAccount,
        BindingResult result        
    ) {
        if (result.hasErrors()) {
            List<Role> allRoles = roleService.findAll();
            model.addAttribute("allRoles", allRoles);
            return "accountView/editAccount";
        }
        Optional<Account> optionalAccount = accountService.findOneById(receivedAccount.getAccountId());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if(!file.isEmpty()) {
                account.setProfilePicture(FileStorage.save(file));
                FileStorage.waitt();
            }
            account.setEmail(receivedAccount.getEmail());
            account.setFirstName(receivedAccount.getFirstName());
            account.setLastName(receivedAccount.getLastName());
            account.setMobile(receivedAccount.getMobile());
            account.setWebsite(receivedAccount.getWebsite());
            account.setRoles(receivedAccount.getRoles());
            account.setDisabled(receivedAccount.isDisabled());
            accountService.save(account);
            return "redirect:/accounts";
        } else {
            return "redirect:/?error";
        }
    }
}