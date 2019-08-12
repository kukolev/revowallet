package exception;

public class AccountNotFoundException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Account not exists";
    }
}
