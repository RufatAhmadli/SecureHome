package example.web.securehome.exception.custom;

public class MemberHomeMismatchException extends RuntimeException {
    public MemberHomeMismatchException(String message) {
        super(message);
    }

    public MemberHomeMismatchException(Long memberId, Long homeId) {
        super("Member " + memberId + " is not a member of home " + homeId);
    }
}
