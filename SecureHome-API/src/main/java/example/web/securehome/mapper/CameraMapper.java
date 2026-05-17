package example.web.securehome.mapper;

import example.web.securehome.dto.request.CameraRequestDto;
import example.web.securehome.dto.response.CameraResponseDto;
import example.web.securehome.entity.Camera;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CameraMapper extends DeviceMapper<Camera, CameraRequestDto, CameraResponseDto> {

    @Override
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    Camera toEntity(CameraRequestDto requestDto);

    @Override
    CameraResponseDto toResponseDto(Camera entity);

    @Override
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    void updateEntity(@MappingTarget Camera entity, CameraRequestDto requestDto);
}
