package example.web.securehome.mapper;

import example.web.securehome.dto.request.RegisterRequestDto;
import example.web.securehome.dto.response.RegisterResponseDto;
import example.web.securehome.entity.Role;
import example.web.securehome.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password",ignore = true)
    User toUserEntity(RegisterRequestDto registerRequestDto);

    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    @Mapping(target = "message", constant = "Registered successfully")
    RegisterResponseDto toRegisterResponseDto(User user);

    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateUserEntity(@MappingTarget User user, RegisterRequestDto registerRequestDto);

    default Set<String> mapRoles(User user) {
        Set<Role> roles = user.getRoles();
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getRoleName)
               .collect(Collectors.toSet());
    }
}
