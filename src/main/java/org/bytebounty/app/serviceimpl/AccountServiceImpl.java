package org.bytebounty.app.serviceimpl;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


import org.bytebounty.app.model.Account;
import org.bytebounty.app.model.Comment;
import org.bytebounty.app.model.Post;
import org.bytebounty.app.model.Privilege;
import org.bytebounty.app.model.Role;
import org.bytebounty.app.repository.AccountRepository;
import org.bytebounty.app.service.AccountService;
import org.bytebounty.app.service.CommentService;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService, UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;


    @Autowired
    private RoleService roleService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Override
    public Account save(Account account) {

        

        if (account.getRegisteredAt() == null) {
            account.setRegisteredAt(LocalDateTime.now());
        }

        if (account.getProfilePicture() == null) {      
                account.setProfilePicture("/img/userImg/default.svg");            
        }

            if (account.getRoles() == null) {
            
            account.setRoles(new HashSet<Role>(Arrays.asList(roleService.findByName("ROLE_SUBSCRIBER"))) );
        }

        return accountRepository.save(account);
        
    }

    @Override
    public Optional<Account> findOneByEmail(String email) {
        
        return accountRepository.findOneByEmailIgnoreCase(email);
    }

    @Override
    public Optional<Account> findOneById(Long i) {
        return accountRepository.findById(i);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findOneByEmailIgnoreCase(email);

        if(!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Account not found");
        }
        
        Account account = optionalAccount.get();

        if (account.isDisabled()) {
            throw new UsernameNotFoundException("Account disabled");
        }

        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        for (Role role : account.getRoles()) {
            grantedAuthority.add(new SimpleGrantedAuthority(role.getName()));
            for (Privilege privilege : role.getPrivileges()) {
                grantedAuthority.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        }

            
        
        return new User(account.getEmail(), String.copyValueOf(account.getPassword().getWord()), grantedAuthority);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public void delete(Account account) {
        List<Post> posts = postService.findAllByAccountId(account.getAccountId());

        for(Post post: posts) {
            postService.delete(post);
        }
        List<Comment> comments = commentService.findByAccountId(account.getAccountId());
        for(Comment comment: comments) {
            commentService.delete(comment);
        }
        accountRepository.delete(account);
    }

    @Override
    public void updateLastLoginByEmail(String email) {
        Account account = this.findOneByEmail(email).get();
        account.setLastLogin(LocalDateTime.now());
        this.save(account);
    }
    
    
}