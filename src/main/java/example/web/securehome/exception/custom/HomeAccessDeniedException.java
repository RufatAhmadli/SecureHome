package example.web.securehome.exception.custom;

public class HomeAccessDeniedException extends UnauthorizedException {
    public HomeAccessDeniedException() {
        super("You do not have permission to access this home.");
    }

    public HomeAccessDeniedException(String message) {
        super(message);
    }
}
