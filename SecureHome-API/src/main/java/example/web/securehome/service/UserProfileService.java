package example.web.securehome.service;

import example.web.securehome.dto.request.UserProfileRequestDto;
import example.web.securehome.dto.response.UserProfileResponseDto;
import example.web.securehome.entity.User;
import example.web.securehome.entity.UserProfile;
import example.web.securehome.exception.custom.ProfileNotFoundException;
import example.web.securehome.mapper.ProfileMapper;
import example.web.securehome.repository.UserProfileRepository;
import example.web.securehome.repository.UserRepository;
import example.web.securehome.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public UserProfileResponseDto getMyProfile() {
        User currentUser = securityUtils.getCurrentUser();
        return Optional.ofNullable(currentUser.getUserProfile())
                .map(profileMapper::toUserProfileResponseDto)
                .orElse(null);
    }

    @Transactional
    public UserProfileResponseDto createMyProfile(UserProfileRequestDto dto) {
        User currentUser = securityUtils.getCurrentUser();
        UserProfile saved = userProfileRepository.save(profileMapper.toUserProfile(dto));
        currentUser.setUserProfile(saved);
        userRepository.save(currentUser);
        return profileMapper.toUserProfileResponseDto(saved);
    }

    @Transactional
    public UserProfileResponseDto updateMyProfile(UserProfileRequestDto dto) {
        User currentUser = securityUtils.getCurrentUser();
        UserProfile profile = Optional.ofNullable(currentUser.getUserProfile())
                .orElseThrow(() -> new ProfileNotFoundException(0L));
        profileMapper.updateUserProfileEntity(profile, dto);
        return profileMapper.toUserProfileResponseDto(userProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto findUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .map(profileMapper::toUserProfileResponseDto)
                .orElseThrow(() -> new ProfileNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponseDto> findAllUserProfiles() {
        return userProfileRepository.findAll()
                .stream()
                .map(profileMapper::toUserProfileResponseDto)
                .toList();
    }
}
