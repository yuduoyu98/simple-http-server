package bean;

import exception.MalformedException;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Method SP Request-URI SP HTTP-Version CRLF
 */
public class RequestLine extends StartLine {

    public final RequestMethod method;
    public final URI URI;

    public RequestLine(RequestMethod method, String URI, ProtocolVersion HttpVersion) throws MalformedException {
        this(method, UriCheck(URI), HttpVersion);
    }

    public RequestLine(RequestMethod method, URI URI, ProtocolVersion HttpVersion) {
        this.method = method;
        this.URI = URI;
        this.protocolVersion = HttpVersion;
    }

    public static RequestLine parse(String requestLine) throws MalformedException {
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) throw new MalformedException("Invalid request line: " + requestLine);

        RequestMethod method = methodCheck(parts[0]);
        URI URI = UriCheck(parts[1]);
        ProtocolVersion protocolVersion = ProtocolVersion.parse(parts[2]);
        return new RequestLine(method, URI.toString(), protocolVersion);
    }

    private static RequestMethod methodCheck(String method) throws MalformedException {
        try {
            return RequestMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new MalformedException("Invalid or unsupported request method: " + method);
        }
    }

    private static URI UriCheck(String uri) throws MalformedException {
        if (StringUtils.isBlank(uri)) throw new MalformedException("URI is empty");
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new MalformedException("Invalid URI: " + uri);
        }
    }

    @Override
    public String toString() {
        return String.join(" ", method.toString(), URI.toString(), protocolVersion.toString());
    }

    public static void main(String[] args) throws MalformedException {
        RequestLine requestLine = RequestLine.parse("HEAD  HTTP/3");
        System.out.println(requestLine);
    }
}
