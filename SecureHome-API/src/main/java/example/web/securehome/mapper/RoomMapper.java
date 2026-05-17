package example.web.securehome.mapper;

import example.web.securehome.dto.request.RoomRequestDto;
import example.web.securehome.dto.response.RoomResponseDto;
import example.web.securehome.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    @Mapping(target = "devices", ignore = true)
    Room toRoomEntity(RoomRequestDto roomRequestDto);

    RoomResponseDto toRoomResponseDto(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "home", ignore = true)
    @Mapping(target = "devices", ignore = true)
    void updateRoomEntity(@MappingTarget Room room, RoomRequestDto roomRequestDto);
}
