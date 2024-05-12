package solution.clear.test.exception;

import java.time.LocalDate;

public class BadRangeException extends RuntimeException {

    private static final long serialVersionUID = -1393117183307529558L;


    public BadRangeException(String message) {
        super(message);
    }
    
    
    public BadRangeException(LocalDate from, LocalDate to) {
        super("Bad range : " + from + " must be after " + to);
    }
    
}
