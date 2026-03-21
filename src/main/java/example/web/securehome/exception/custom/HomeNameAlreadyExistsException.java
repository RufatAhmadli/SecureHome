package example.web.securehome.exception.custom;

public class HomeNameAlreadyExistsException extends ResourceAlreadyExistsException {
    public HomeNameAlreadyExistsException(String name) {
        super("Home name already exists with name: " + name);
    }
}
