package solution.clear.test.exception;


public class AgeNotValidException extends RuntimeException {

    private static final long serialVersionUID = -3115680494878154782L;


    public AgeNotValidException() {
        super("Age not valid");
    }

    
    public AgeNotValidException(long limit) {
        super("Age not valid. Minimum value is " + limit + ".");
    }

}
