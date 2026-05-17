package example.web.securehome.mapper;

import example.web.securehome.dto.request.SmartLockRequestDto;
import example.web.securehome.dto.response.SmartLockResponseDto;
import example.web.securehome.entity.SmartLock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SmartLockMapper extends DeviceMapper<SmartLock, SmartLockRequestDto, SmartLockResponseDto> {

    @Override
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    @Mapping(target = "lockStatus", constant = "LOCKED")
    SmartLock toEntity(SmartLockRequestDto requestDto);

    @Override
    SmartLockResponseDto toResponseDto(SmartLock entity);

    @Override
    @Mapping(target = "lockStatus", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    void updateEntity(@MappingTarget SmartLock entity, SmartLockRequestDto requestDto);
}

