package example.web.securehome.exception.custom;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("No authenticated user found");
    }

    public UnauthorizedException(String message) {

        super(message);
    }
}
