package example.web.securehome.mapper;

import example.web.securehome.dto.request.MemberRequestDto;
import example.web.securehome.dto.response.MemberResponseDto;
import example.web.securehome.entity.HomeMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    HomeMember toMemberEntity(MemberRequestDto memberRequestDto);

    MemberResponseDto toMemberResponseDto(HomeMember homeMember);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    void updateMemberEntity(@MappingTarget HomeMember homeMember, MemberRequestDto memberRequestDto);
}
