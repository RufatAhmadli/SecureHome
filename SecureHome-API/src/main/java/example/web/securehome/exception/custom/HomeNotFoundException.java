package example.web.securehome.exception.custom;

public class HomeNotFoundException extends ResourceNotFoundException {
    public HomeNotFoundException(Long id) {
        super("Home not found with id: " + id);
    }
}
