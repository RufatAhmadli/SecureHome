package example.web.securehome.exception.custom;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(Long id) {
        super("Profile not found with id: " + id);
    }
}
