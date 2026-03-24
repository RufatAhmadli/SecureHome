package example.web.securehome.mapper;

import example.web.securehome.dto.request.DeviceRequestDto;
import example.web.securehome.dto.response.DeviceResponseDto;
import example.web.securehome.entity.Device;
import org.mapstruct.MappingTarget;

public interface DeviceMapper<T extends Device, REQ extends DeviceRequestDto, RES extends DeviceResponseDto> {
    T toEntity(REQ requestDto);
    RES toResponseDto(T entity);
    void updateEntity(@MappingTarget T entity, REQ requestDto);
}
