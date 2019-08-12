package exception;

public class NotEnoughMoneyException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Not enough money for transfer";
    }
}
