package solution.clear.test.exception;


public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3360537366297284801L;


    public UserNotFoundException() {
        super("User not found");
    }


    public UserNotFoundException(long id) {
        super("User (id=" + id + ") not found");
    }


    public UserNotFoundException(Throwable err) {
        super("User not found", err);
    }
    
}
