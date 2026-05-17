package example.web.securehome.exception.custom;

public class ProfileNotFoundException extends ResourceNotFoundException {
    public ProfileNotFoundException(Long id) {
        super("Profile not found with id: " + id);
    }
}
