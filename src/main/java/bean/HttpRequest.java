package bean;

import exception.MalformedException;

import java.net.URI;

/**
 * HTTP Request
 */
public class HttpRequest extends HttpMessage {

    private RequestLine requestLine;

    private HttpRequest() {
        super();
    }

    public RequestMethod method() {
        return this.requestLine.method;
    }

    public URI getURI() {
        return this.requestLine.URI;
    }

    @Override
    protected String startLine() {
        return requestLine.toString();
    }

    public static class Builder {
        private final HttpRequest request;
        private Builder() {
            request = new HttpRequest();
        }

        public static Builder builder() {
            return new Builder();
        }

        public HttpRequest.Builder addRequestLine(RequestLine requestLine) {
            request.requestLine = requestLine;
            return this;
        }

        public HttpRequest.Builder addRequestLine(RequestMethod method, String URI, ProtocolVersion protocolVersion) throws MalformedException {
            request.requestLine = new RequestLine(method, URI, protocolVersion);
            return this;
        }

        public HttpRequest.Builder addHeader(String key, String value) {
            request.addHeader(key, value);
            return this;
        }

        public HttpRequest.Builder addBody(byte[] body) {
            request.body = body;
            return this;
        }

        public HttpRequest build() {
            if (request.requestLine == null) {
                throw new IllegalStateException("Request line is required");
            }
            return request;
        }
    }

    public static void main(String[] args) {
        String str = "START LINE" + CRLF + "HEADER 1" + CRLF + "HEADER 2" + CRLF + CRLF + "BODY";
        String[] splits = str.split(CRLF);
        for (String split : splits) {
            System.out.println(split);
        }
    }
}
