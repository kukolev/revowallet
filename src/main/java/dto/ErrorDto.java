package dto;

public class ErrorDto {

    private int code = 200;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
