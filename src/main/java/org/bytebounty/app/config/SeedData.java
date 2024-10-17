package org.bytebounty.app.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import org.bytebounty.app.model.Account;
import org.bytebounty.app.model.Privilege;
import org.bytebounty.app.model.Role;
import org.bytebounty.app.model.Password;
import org.bytebounty.app.service.AccountService;
import org.bytebounty.app.service.CategoryService;
import org.bytebounty.app.service.CommentService;
import org.bytebounty.app.service.PasswordService;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.service.PrivilegeService;
import org.bytebounty.app.service.RoleService;
import org.bytebounty.app.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    AccountService accountService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    TagService tagService;

    @Autowired
    PostService postService;

    @Autowired
    CommentService postCommentService;

    @Autowired
    PrivilegeService privilegeService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordService passwordService;

    

    @Override
    public void run(String... args) throws Exception {

        //
        // Roles and privileges set up
        //

        // Adming privileges
        Privilege editUsersPrivilege = createPrivilegeIfNotFound("EDIT_USERS");
        Privilege deleteUsersPrivilege = createPrivilegeIfNotFound("DELETE_USERS");
        
        Set<Privilege> adminPrivileges= new HashSet<>();
        adminPrivileges.add(editUsersPrivilege);
        adminPrivileges.add(deleteUsersPrivilege);
        Role roleAdmin = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);


        // Editor privileges
        Privilege deleteOthersPosts = createPrivilegeIfNotFound("DELETE_OTHERS_POSTS");
        Privilege deletePrivatePosts = createPrivilegeIfNotFound("DELETE_PRIVATE_POSTS");
        Privilege editOthersPosts = createPrivilegeIfNotFound("EDIT_OTHERS_POSTS");
        Privilege editPrivatePosts = createPrivilegeIfNotFound("EDIT_PRIVATE_POSTS");
        Privilege readPrivatePosts = createPrivilegeIfNotFound("READ_PRIVATE_POSTS");
        Privilege manageCategories = createPrivilegeIfNotFound("MANAGE_CATEGORIES");
        Privilege moderateComments = createPrivilegeIfNotFound("MODERATE_COMMENTS");
        Privilege unfilteredHtml = createPrivilegeIfNotFound("UNFILTERED_HTML");
        Set<Privilege> editorPrivileges = new HashSet<>();
        editorPrivileges.add(deleteOthersPosts);
        editorPrivileges.add(deletePrivatePosts);
        editorPrivileges.add(editOthersPosts);
        editorPrivileges.add(editPrivatePosts);
        editorPrivileges.add(readPrivatePosts);
        editorPrivileges.add(manageCategories);
        editorPrivileges.add(moderateComments);
        editorPrivileges.add(unfilteredHtml);
        Role roleEditor = createRoleIfNotFound("ROLE_EDITOR", editorPrivileges);

        // autor privileges
        Privilege deletePosts = createPrivilegeIfNotFound("DELETE_POSTS");
        Privilege deletePublishedPosts = createPrivilegeIfNotFound("DELETE_PUBLISHED_POSTS");
        Privilege editPosts = createPrivilegeIfNotFound("EDIT_POSTS");
        Privilege editPublishedPosts = createPrivilegeIfNotFound("EDIT_PUBLISHED_POSTS");
        Privilege publishPosts = createPrivilegeIfNotFound("PUBLISH_POSTS");
        Privilege uploadFiles = createPrivilegeIfNotFound("UPLOAD_FILES");
        Set<Privilege> authorPrivileges = new HashSet<>();
        authorPrivileges.add(deletePosts);
        authorPrivileges.add(deletePublishedPosts);
        authorPrivileges.add(editPosts);
        authorPrivileges.add(editPublishedPosts);
        authorPrivileges.add(publishPosts);
        authorPrivileges.add(uploadFiles);
        Role roleAuthor = createRoleIfNotFound("ROLE_AUTHOR", authorPrivileges);

        // suscriber role privileges
        Privilege read = createPrivilegeIfNotFound("READ");
        Set<Privilege> subscriverPrivileges = new HashSet<>();
        subscriverPrivileges.add(read);
        Role roleSubscriver = createRoleIfNotFound("ROLE_SUBSCRIBER", subscriverPrivileges);

        //
        // Create accounts
        //


        Password password01 = new Password(new char[]{'a', 'e', 'i', 'o'});
           

        Account account01 = new Account();
        account01.setFirstName("FirstName");
        account01.setLastName("LastName");
        account01.setEmail("mail@mail.com");        
        account01.setProfilePicture("/img/userImg/default.svg");
        account01.setRoles(new HashSet<Role>(Arrays.asList(roleAdmin, roleEditor, roleAuthor, roleSubscriver)));
        
        account01 = createAccountIfNotFound(account01, password01);
    }


    private Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeService.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeService.save(privilege);
        }
        return privilege;
    }


    Role createRoleIfNotFound(String name, Set<Privilege> privileges) {
        
        Role role = roleService.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setPrivileges(privileges);
            roleService.save(role);
        }
        return role;
    }


        Account createAccountIfNotFound(Account account, Password password) {

            Optional<Account> optionalAccount = accountService.findOneByEmail(account.getEmail());
            if (!optionalAccount.isPresent()) {
                passwordService.save(password);                
                account.setPassword(password);
                accountService.save(account);                
            } else {
                account = optionalAccount.get();
            }
            return account;
        }
}