package example.web.securehome.exception.custom;

public class MemberAlreadyExistsException extends RuntimeException {
    public MemberAlreadyExistsException(String message) {
        super(message);
    }

    public MemberAlreadyExistsException(Long userId, Long homeId) {
        super("Member already exists for user " + userId + " in home " + homeId);
    }

}
