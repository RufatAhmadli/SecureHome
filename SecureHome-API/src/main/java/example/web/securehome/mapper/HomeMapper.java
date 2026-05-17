package example.web.securehome.mapper;

import example.web.securehome.dto.request.HomeRequestDto;
import example.web.securehome.dto.response.HomeResponseDto;
import example.web.securehome.entity.Home;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HomeMapper {
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "id", ignore = true)
    Home toHomeEntity(HomeRequestDto homeRequestDto);

    HomeResponseDto toHomeResponseDto(Home home);

    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateHomeEntity(@MappingTarget Home home, HomeRequestDto homeRequestDto);
}
