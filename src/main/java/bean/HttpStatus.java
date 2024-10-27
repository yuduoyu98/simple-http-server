package bean;


public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_MODIFIED(304, "Not Modified"),
    ;

    public final int code;
    public final String reasonPhrase;

    HttpStatus(int code, String msg) {
        this.code = code;
        this.reasonPhrase = msg;
    }

    public static HttpStatus parse(int code, String reasonPhrase) {
        for (HttpStatus status : values())
            if (status.code == code && status.reasonPhrase.equals(reasonPhrase))
                return status;

        return null;
    }

    @Override
    public String toString() {
        return String.format("%d %s", code, reasonPhrase);
    }
}
