package example.web.securehome.service;

import example.web.securehome.dto.request.LoginRequestDto;
import example.web.securehome.dto.request.RegisterRequestDto;
import example.web.securehome.dto.response.LoginResponseDto;
import example.web.securehome.dto.response.RegisterResponseDto;
import example.web.securehome.entity.Role;
import example.web.securehome.entity.User;
import example.web.securehome.exception.custom.RoleNotFoundException;
import example.web.securehome.mapper.RegisterMapper;
import example.web.securehome.repository.RoleRepository;
import example.web.securehome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RegisterMapper registerMapper;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {
        User request = registerMapper.toUserEntity(dto);
        Role userRole = roleRepository.findByRoleNameContainsIgnoreCase("user")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        request.addRole(userRole);
        request.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        User saved = userRepository.save(request);
        return registerMapper.toRegisterResponseDto(saved);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(dto.getEmail());
        String token = jwtService.generateToken(userDetails);
        return LoginResponseDto.builder().token(token)
                .email(userDetails.getUsername())
                .build();
    }
}
