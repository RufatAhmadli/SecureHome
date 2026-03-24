package example.web.securehome.exception.custom;

public class DeviceNotFoundException extends ResourceNotFoundException {
    public DeviceNotFoundException(Long id) {
        super("Device not found with id: " + id);
    }
}
