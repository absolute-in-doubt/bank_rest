package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository repository){
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOpt = repository.findByUsername(username);
        if(userOpt.isEmpty())
            throw new UsernameNotFoundException("User not found");
        User user = userOpt.get();
        return new UserDetailsImpl(user.getUsername(), user.getPasswordHash(), user.getId());
    }
}
