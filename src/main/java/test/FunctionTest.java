package test;

import bean.*;
import exception.MalformedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.net.Socket;

import static bean.HttpMessage.CRLF;
import static bean.RequestMethod.GET;

/**
 * Function test <p>
 * start the httpserver (port 8080) first, then run this test <p>
 */
public class FunctionTest {

    /*****************************************************************************************
     * Test Entrance                                                                         *
     *****************************************************************************************/

    public static void main(String[] args) throws MalformedException {
        int port = 8080;
        GET200Test(port);
        GET304Test(port);
        GET400Test(port);
        GET404Test(port);
    }

    /*****************************************************************************************
     * Test Cases                                                                            *
     *****************************************************************************************/

    /**
     * request:
     * GET /helloworld.html HTTP/1.1[CRLF]
     * Connection: close[CRLF]
     * User-Agent: Mozilla/5.0[CRLF]
     * Host: localhost:8080[CRLF]
     * Accept-Language: zh-CN,zh;q=0.9,en;q=0.8[CRLF]
     * [CRLF]
     *
     * response:
     * HTTP/1.1 200 OK[CRLF]
     * Server: Yu's Server[CRLF]
     * Connection: close[CRLF]
     * Last-Modified: Wed, 23 Oct 2024 21:53:26 CST[CRLF]
     * Content-Length: 205[CRLF]
     * Date: Thu, 24 Oct 2024 00:36:15 CST[CRLF]
     * Content-Type: text/html[CRLF]
     * [CRLF]
     * { BODY: 205 bytes }
     */
    public static void GET200Test(int port) throws MalformedException {
        // 200 GET request: GET helloworld html
        HttpRequest request = HttpRequest.Builder.builder()
                .addRequestLine(new RequestLine(GET, "/helloworld.html", ProtocolVersion.HTTP_1_1))
                .addHeader("Host", "localhost:8080")
                .addHeader("Connection", "close")
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .build();

        System.out.println("-----------------------------------------");
        System.out.println("request:");
        System.out.println(request);

        // send request
        HttpResponse response = sendRequest(port, request.toBytes());
        System.out.println("response:");
        System.out.print(response);

        // check
        assert response != null : "Response is null";
        assert response.statusLine.status == HttpStatus.OK : "Response status should be 200 OK";
        assert response.body != null : "Response body is null";
        assert response.body.length == 205 : "Response body mismatch";
        System.out.println("-----------------------------------------");
    }

    /**
     * request:
     * GET /helloworld.md HTTP/1.1[CRLF]
     * Connection: close[CRLF]
     * User-Agent: Mozilla/5.0[CRLF]
     * Host: localhost:8080[CRLF]
     * Accept-Language: zh-CN,zh;q=0.9,en;q=0.8[CRLF]
     * [CRLF]
     *
     * response:
     * HTTP/1.1 404 Not Found[CRLF]
     * Server: Yu's Server[CRLF]
     * Connection: close[CRLF]
     * Date: Thu, 24 Oct 2024 00:51:21 CST[CRLF]
     * [CRLF]
     */
    public static void GET404Test(int port) throws MalformedException {
        // 404 GET request: helloworld.md does not exist
        HttpRequest request = HttpRequest.Builder.builder()
                .addRequestLine(new RequestLine(GET, "/helloworld.md", ProtocolVersion.HTTP_1_1))
                .addHeader("Host", "localhost:8080")
                .addHeader("Connection", "close")
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .build();

        System.out.println("-----------------------------------------");
        System.out.println("request:");
        System.out.println(request);

        // send request
        HttpResponse response = sendRequest(port, request.toBytes());
        System.out.println("response:");
        System.out.print(response);

        // check
        assert response != null : "Response is null";
        assert response.statusLine.status == HttpStatus.NOT_FOUND : "Response status should be 404 Not Found";
        assert response.body == null : "Response body is null";
        System.out.println("-----------------------------------------");
    }

    /**
     * request:
     * GET /helloworld.html HTTP/1.1[CRLF]
     * Host: localhost:8080[CRLF]
     * Connection: keep-alive[CRLF]
     * User-Agent: Mozilla/5.0[CRLF]
     * Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
     * BODY[CRLF]
     * BODY[CRLF]
     * BODY
     *
     * response:
     * HTTP/1.1 400 Bad Request[CRLF]
     * Server: Yu's Server[CRLF]
     * Connection: close[CRLF]
     * Date: Thu, 24 Oct 2024 01:02:50 CST[CRLF]
     * [CRLF]
     */
    public static void GET400Test(int port) throws MalformedException {
        // malformed GET request: missing CRLF between headers and body
        String[] lines = new String[]{
                "GET /helloworld.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "User-Agent: Mozilla/5.0",
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8",
        };
        String requestMsg = String.join(CRLF, lines);
        requestMsg += CRLF;
        String body = "BODY" + CRLF + "BODY" + CRLF + "BODY";
        requestMsg += body;

        System.out.println("-----------------------------------------");
        System.out.println("request:");
        System.out.println(requestMsg.replaceAll(CRLF, "[CRLF]" + CRLF) + "\n");

        // send request
        HttpResponse response = sendRequest(port, requestMsg.getBytes());
        System.out.println("response:");
        System.out.print(response);

        // check
        assert response != null : "Response is null";
        assert response.statusLine.status == HttpStatus.BAD_REQUEST : "Response status should be 400 Bad Request";
        assert response.body == null : "Response body is null";
        System.out.println("-----------------------------------------");
    }

    /**
     * request:
     * GET /helloworld.html HTTP/1.1[CRLF]
     * Connection: close[CRLF]
     * User-Agent: Mozilla/5.0[CRLF]
     * If-Modified-Since: Wed, 23 Oct 2025 21:53:26 CST[CRLF]
     * Host: localhost:8080[CRLF]
     * Accept-Language: zh-CN,zh;q=0.9,en;q=0.8[CRLF]
     * [CRLF]
     *
     * response:
     * HTTP/1.1 304 Not Modified[CRLF]
     * Server: Yu's Server[CRLF]
     * Connection: close[CRLF]
     * Date: Thu, 24 Oct 2024 00:47:58 CST[CRLF]
     * [CRLF]
     */
    public static void GET304Test(int port) throws MalformedException {
        // generate GET request: If-Modified-Since 2025
        HttpRequest request = HttpRequest.Builder.builder()
                .addRequestLine(new RequestLine(GET, "/helloworld.html", ProtocolVersion.HTTP_1_1))
                .addHeader("Host", "localhost:8080")
                .addHeader("Connection", "close")
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("If-Modified-Since", "Wed, 23 Oct 2025 21:53:26 CST")
                .build();

        System.out.println("-----------------------------------------");
        System.out.println("request:");
        System.out.println(request);

        // send request
        HttpResponse response = sendRequest(port, request.toBytes());
        System.out.println("response:");
        System.out.print(response);

        // check
        assert response != null : "Response is null";
        assert response.statusLine.status == HttpStatus.NOT_MODIFIED : "Response status should be 304 Not Modified";
        assert response.body == null : "Response body should be null";
        System.out.println("-----------------------------------------");
    }

    /*****************************************************************************************
     * Helper Functions                                                                      *
     *****************************************************************************************/

    public static HttpResponse sendRequest(int port, byte[] requestBytes) {
        try (Socket socket = new Socket("localhost", port);
             OutputStream os = socket.getOutputStream();
             InputStream is = socket.getInputStream();
             InputStreamReader isr = new InputStreamReader(is)
        ) {
            os.write(requestBytes);
            os.flush();

            socket.shutdownOutput();

            // decode response
            return decodeResponse(is, isr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpResponse decodeResponse(InputStream is, InputStreamReader isr) throws IOException, MalformedException {
        HttpResponse.Builder builder = HttpResponse.Builder.builder();
        String startLine = readByCRLF(isr);
        StatusLine statusLine = StatusLine.parse(startLine);
        builder.addStatusLine(statusLine);
        int contentLength = readAndAddHeaders(isr, builder);
        readAndAddResponseBodyIfExists(is, builder, contentLength);
        return builder.build();
    }

    private static void readAndAddResponseBodyIfExists(InputStream is, HttpResponse.Builder builder, int contentLength) throws IOException {
        if (contentLength == 0) return;

        byte[] body = new byte[contentLength];
        is.read(body);
        builder.addBody(body);
    }

    private static int readAndAddHeaders(InputStreamReader isr, HttpResponse.Builder reqBuilder) throws IOException, MalformedException {
        String line;
        int contentLength = 0;
        while (true) {
            line = readByCRLF(isr);
            if (StringUtils.isBlank(line)) break;
            Pair<String, String> kv = HttpMessage.decodeAndAddHeader(line);
            String key = kv.getLeft();
            String val = kv.getRight();
            if (HttpHeaders.Entity.CONTENT_LENGTH.equals(key)) contentLength = Integer.parseInt(val);
            reqBuilder.addHeader(key, val);
        }
        return contentLength;
    }

    public static Pair<String, String> decodeAndAddHeader(String kv) throws MalformedException {
        if (StringUtils.isBlank(kv)) throw new MalformedException("Empty http header");
        int index = kv.indexOf(HttpHeaders.HEADER_SEP);
        if (index == -1) throw new MalformedException("Invalid http header: " + kv);
        String key = kv.substring(0, index).trim();
        String val = kv.substring(index + 1).trim();
        return Pair.of(key, val);
    }

    private static String readByCRLF(InputStreamReader isr) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        boolean cr = false;
        while ((ch = isr.read()) != -1) {
            if (cr && ch == '\n') {
                sb.setLength(sb.length() - 1); // remove the last CR
                break;
            } ;
            if (ch == '\r') cr = true;
            sb.append((char) ch);
        }
        return sb.toString();
    }

}
