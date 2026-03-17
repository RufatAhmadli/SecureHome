package example.web.securehome.exception.custom;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
}
