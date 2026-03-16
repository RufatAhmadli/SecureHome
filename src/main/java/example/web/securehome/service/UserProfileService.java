package example.web.securehome.service;

import example.web.securehome.dto.request.UserProfileRequestDto;
import example.web.securehome.dto.response.UserProfileResponseDto;
import example.web.securehome.entity.UserProfile;
import example.web.securehome.exception.custom.ProfileNotFoundException;
import example.web.securehome.mapper.ProfileMapper;
import example.web.securehome.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final ProfileMapper profileMapper;

    @Transactional(readOnly = true)
    public UserProfileResponseDto findUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .map(profileMapper::toUserProfileResponseDto)
                .orElseThrow(() -> new ProfileNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponseDto> findAllUserProfiles() {
        return userProfileRepository.findAll()
                .stream().map(profileMapper::toUserProfileResponseDto)
                .toList();
    }

    @Transactional
    public UserProfileResponseDto createUserProfile(UserProfileRequestDto userProfileRequestDto) {
        UserProfile saved = userProfileRepository.save(profileMapper.toUserProfile(userProfileRequestDto));
        return profileMapper.toUserProfileResponseDto(saved);
    }

    @Transactional
    public UserProfileResponseDto updateUserProfile(Long id,
                                                    UserProfileRequestDto userProfileRequestDto) {
        UserProfile found = userProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));
        profileMapper.updateUserProfileEntity(found, userProfileRequestDto);
        return profileMapper.toUserProfileResponseDto(userProfileRepository.save(found));
    }

    @Transactional
    public void deleteUserProfileById(Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));
        userProfileRepository.delete(userProfile);
    }
}
