package bean;

import exception.MalformedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Http Message (RFC 2616)
 * - generic message: start-line
 *                    *(message-header CRLF)
 *                    CRLF
 *                    [ message-body ]
 *   - start-line: Request-Line | Status-Line
 */
public abstract class HttpMessage {
    public static final String CRLF = "\r\n";
    protected Map<String, String> headers;
    public byte[] body;

    protected HttpMessage() {
        headers = new HashMap<>();
    }

    protected HttpMessage(Map<String, String> headers, byte[] body) {
        this.headers = headers;
        this.body = body;
    }

    public void addHeader(String key, String val) {
        if (StringUtils.isAnyBlank(key)) return;
        headers.put(key, val);
    }

    protected abstract String startLine();

    public static Pair<String, String> decodeAndAddHeader(String kv) throws MalformedException {
        if (StringUtils.isBlank(kv)) throw new MalformedException("Empty http header");
        int index = kv.indexOf(HttpHeaders.HEADER_SEP);
        if (index == -1) throw new MalformedException("Invalid http header: " + kv);
        String key = kv.substring(0, index).trim();
        String val = kv.substring(index + 1).trim();
        return Pair.of(key, val);
    }

    public String getHeaderValue(String key) {
        return headers.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(startLine()).append("[CRLF]").append(CRLF);
        headers.forEach((k, v) -> sb.append(k).append(HttpHeaders.HEADER_SEP).append(" ").append(v).append("[CRLF]").append(CRLF));
        sb.append("[CRLF]").append(CRLF);
        if (body != null) sb.append("{ BODY: ").append(body.length).append(" bytes }");
        return sb.toString();
    }

    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append(startLine()).append(CRLF);
        headers.forEach((k, v) -> sb.append(k).append(HttpHeaders.HEADER_SEP).append(" ").append(v).append(CRLF));
        sb.append(CRLF);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            os.write(sb.toString().getBytes());
            if (body != null) os.write(body);
            return os.toByteArray();
        } catch (IOException e) {
            // internal server error
            e.printStackTrace();
        }
        return null;
    }

}
