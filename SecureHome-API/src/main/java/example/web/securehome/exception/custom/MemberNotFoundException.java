package example.web.securehome.exception.custom;

public class MemberNotFoundException extends ResourceNotFoundException {
    public MemberNotFoundException(Long id) {
        super("Member not found with id: " + id);
    }
}
