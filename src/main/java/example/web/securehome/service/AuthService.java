package example.web.securehome.service;

import example.web.securehome.dto.request.RegisterRequestDto;
import example.web.securehome.dto.response.RegisterResponseDto;
import example.web.securehome.entity.Role;
import example.web.securehome.entity.User;
import example.web.securehome.mapper.RegisterMapper;
import example.web.securehome.repository.RoleRepository;
import example.web.securehome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RegisterMapper registerMapper;
    private final RoleRepository roleRepository;

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto){
        User request = registerMapper.toUserEntity(dto);
        Role userRole = roleRepository.findByRoleNameContainsIgnoreCase("user");
        request.addRole(userRole);
        User saved = userRepository.save(request);
        return registerMapper.toRegisterResponseDto(saved);
    }
}
