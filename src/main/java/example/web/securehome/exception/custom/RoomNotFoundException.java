package example.web.securehome.exception.custom;

public class RoomNotFoundException extends ResourceNotFoundException {
    public RoomNotFoundException(Long id) {
        super("Room not found with id: " + id);
    }
}
