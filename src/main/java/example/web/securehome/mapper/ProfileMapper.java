package example.web.securehome.mapper;

import example.web.securehome.dto.request.UserProfileRequestDto;
import example.web.securehome.dto.response.UserProfileResponseDto;
import example.web.securehome.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    UserProfile toUserProfile(UserProfileRequestDto userProfileRequestDto);

    UserProfileResponseDto toUserProfileResponseDto(UserProfile userProfile);

    @Mapping(target = "id", ignore = true)
    void updateUserProfileEntity(@MappingTarget UserProfile userProfile, UserProfileRequestDto userProfileRequestDto);
}
