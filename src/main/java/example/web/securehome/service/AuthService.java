package example.web.securehome.service;

import example.web.securehome.dto.request.ChangePasswordRequestDto;
import example.web.securehome.dto.request.LoginRequestDto;
import example.web.securehome.dto.request.RegisterRequestDto;
import example.web.securehome.dto.request.UpdateUserRequestDto;
import example.web.securehome.dto.response.LoginResponseDto;
import example.web.securehome.dto.response.RegisterResponseDto;
import example.web.securehome.dto.response.UserAccountResponseDto;
import example.web.securehome.entity.Role;
import example.web.securehome.entity.User;
import example.web.securehome.exception.custom.PasswordMismatchException;
import example.web.securehome.exception.custom.ResourceAlreadyExistsException;
import example.web.securehome.exception.custom.RoleNotFoundException;
import example.web.securehome.mapper.RegisterMapper;
import example.web.securehome.repository.MemberRepository;
import example.web.securehome.repository.RoleRepository;
import example.web.securehome.repository.UserRepository;
import example.web.securehome.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RegisterMapper registerMapper;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("An account with this email already exists");
        }
        User request = registerMapper.toUserEntity(dto);
        Role userRole = roleRepository.findByRoleNameContainsIgnoreCase("user")
                .orElseThrow(() -> new RoleNotFoundException("user"));
        request.addRole(userRole);
        request.setPassword(passwordEncoder.encode(dto.getPassword()));
        return registerMapper.toRegisterResponseDto(userRepository.save(request));
    }

    @Transactional(readOnly = true)
    public UserAccountResponseDto getMe() {
        return registerMapper.toUserAccountResponseDto(securityUtils.getCurrentUser());
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDto dto) {
        User currentUser = securityUtils.getCurrentUser();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), currentUser.getPassword())) {
            throw new PasswordMismatchException("Current password is incorrect");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException("New passwords do not match");
        }
        currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Transactional
    public void deleteMyAccount() {
        User currentUser = securityUtils.getCurrentUser();
        memberRepository.deleteAll(memberRepository.findAllByUserId(currentUser.getId()));
        userRepository.delete(currentUser);
    }

    @Transactional
    public UserAccountResponseDto updateMyName(UpdateUserRequestDto dto) {
        User currentUser = securityUtils.getCurrentUser();
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            currentUser.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            currentUser.setLastName(dto.getLastName());
        }
        return registerMapper.toUserAccountResponseDto(userRepository.save(currentUser));
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return LoginResponseDto.builder().token(token).email(userDetails.getUsername()).build();
    }
}
