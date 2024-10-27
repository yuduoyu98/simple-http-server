package bean;

/**
 * HTTP Response
 */
public class HttpResponse extends HttpMessage {
    public StatusLine statusLine;

    private HttpResponse() {
        super();
    }

    @Override
    protected String startLine() {
        return statusLine.toString();
    }

    public static class Builder {

        private final HttpResponse response;

        private Builder() {
            response = new HttpResponse();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder addStatusLine(StatusLine statusLine) {
            response.statusLine = statusLine;
            return this;
        }

        public Builder addStatusLine(ProtocolVersion protocolVersion, HttpStatus status) {
            response.statusLine = new StatusLine(protocolVersion, status);
            return this;
        }

        public Builder addHeader(String key, String value) {
            response.addHeader(key, value);
            return this;
        }

        public Builder addBody(byte[] body) {
            response.body = body;
            return this;
        }
        public HttpResponse build() {
            if (response.statusLine == null) {
                throw new IllegalStateException("Status line is required");
            }
            return response;
        }

    }
}
