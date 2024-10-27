package bean;

/**
 * Enumerations of Header field
 * <href a = "https://datatracker.ietf.org/doc/html/rfc2616#section-4.2 /a>
 * classification:
 * - general-header (section 4.5)
 * - request-header (section 5.3)
 * - response-header (section 6.2)
 * - entity-header (section 7.1)
 */
public final class HttpHeaders {

    private HttpHeaders() {
        // Don't allow instantiation.
    }

    public static final String HEADER_SEP = ":";

    public static class General {
        public static final String CONNECTION = "Connection";
        public static final String DATE = "Date";
    }

    public static class Request {
        public static final String ACCEPT_LANGUAGE = "Accept-Language";
        public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
        public static final String USER_AGENT = "User-Agent";
        public static final String HOST = "Host";
        public static final String DATE = "Date";
    }

    public static class Response {
        public static final String SERVER = "Server";
    }

    public static class Entity {
        public static final String CONTENT_LENGTH = "Content-Length";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String LAST_MODIFIED = "Last-Modified";
    }
}
