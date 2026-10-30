package academy.tochkavhoda.competition.exception;

public class ServerException extends Exception {
    private final ServerErrorCode errorCode;

    public ServerException(ServerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServerException(ServerErrorCode errorCode, String details) {
        super(errorCode.getMessage() + ": " + details);
        this.errorCode = errorCode;
    }

    public ServerErrorCode getErrorCode() {
        return errorCode;
    }
}
