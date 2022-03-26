package com.example.shortform.config.auth;


import com.example.shortform.domain.User;
import com.example.shortform.exception.CustomUsernameNotFoundException;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws CustomUsernameNotFoundException {
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomUsernameNotFoundException("존재하지 않는 이메일입니다. email : "+ email)
        );


        return new PrincipalDetails(findUser);
    }
}
