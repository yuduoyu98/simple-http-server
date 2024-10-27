import bean.*;
import exception.MalformedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import utils.Logger;
import utils.CommonUtils;

import java.io.*;
import java.net.Socket;

import static bean.ProtocolVersion.HTTP_1_1;
import static bean.RequestMethod.HEAD;

/**
 * Server task thread
 */
public class HttpTask implements Runnable {

    private final Socket socket;
    private static final String SERVER_NAME = "Yu's Server";

    public HttpTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (InputStream is = socket.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             OutputStream os = socket.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(os)
        ) {
            HttpResponse response;
            HttpRequest request = null;

            try {
                // 1. read and decode request
                request = decodeRequest(is, isr);
                Logger.debug("decoded request >> \n\n" + request);

                // 2. handle request and generate response
                response = handleRequest(request);

            } catch (MalformedException e) {
                Logger.error("Malformed request: " + e.getMessage());
                response = basicReponseBuilder(HttpStatus.BAD_REQUEST).build();
            }

            // 3. send response
            if (response != null) {
                bos.write(response.toBytes());
                bos.flush();
                Logger.debug("response sent >> \n\n" + response);
            }

            // log request and response
            Logger.info(CommonUtils.generateRequestLog(request, response));

        } catch (IOException e) {
            // internal server error
            Logger.error("IOException occurred while handling request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param bs byte input stream
     * @param cs char input stream
     * @return HttpRequest
     */
    private HttpRequest decodeRequest(InputStream bs, InputStreamReader cs) throws IOException, MalformedException {
        // read by CRLF and stop at an empty line with CRLF
        HttpRequest.Builder reqBuilder = HttpRequest.Builder.builder();
        // read and decode request line
        String startLine = readByCRLF(cs);
        RequestLine requestLine = RequestLine.parse(startLine);
        reqBuilder.addRequestLine(requestLine);
        // read and decode request headers
        int contentLength = readAndAddRequestHeaders(cs, reqBuilder);
        // read body if exists
        readAndAddRequestBodyIfExists(bs, reqBuilder, contentLength);
        return reqBuilder.build();
    }

    // read request body if exists and add to request builder
    private static void readAndAddRequestBodyIfExists(InputStream is, HttpRequest.Builder reqBuilder, int contentLength) throws IOException {
        if (contentLength == 0) return;

        byte[] body = new byte[contentLength];
        int readBytes = is.read(body);
        assert contentLength == readBytes : "content length mismatch";
        reqBuilder.addBody(body);
    }

    // read headers and add to request builder
    // return contentLength if exists, otherwise 0
    private int readAndAddRequestHeaders(InputStreamReader isr, HttpRequest.Builder reqBuilder) throws IOException, MalformedException {
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

    private HttpResponse handleRequest(HttpRequest request) {
        if (request == null) return null;
        HttpResponse response = null;
        switch (request.method()) {
            case HEAD:
            case GET:
                response = getAndHeadHandler(request, request.method() == HEAD);
                break;
            default:
                Logger.error("Unsupported request method: " + request.method());
        }
        return response;
    }

    // GET & HEAD method handler
    private HttpResponse getAndHeadHandler(HttpRequest request, boolean isHead) {
        Resource resource = new Resource(request.getURI());

        if (!resource.isValid()) return basicReponseBuilder(HttpStatus.NOT_FOUND).build();

        String modifiedSinceHeaderVal = request.getHeaderValue(HttpHeaders.Request.IF_MODIFIED_SINCE);
        boolean notModifiedSince = modifiedSinceHeaderVal != null && !resource.isModifiedSince(CommonUtils.parseDate(modifiedSinceHeaderVal));
        if (notModifiedSince)
            return basicReponseBuilder(HttpStatus.NOT_MODIFIED).build();

        HttpResponse.Builder builder = basicReponseBuilder(HttpStatus.OK)
                .addHeader(HttpHeaders.Entity.CONTENT_TYPE, resource.getContentType())
                .addHeader(HttpHeaders.Entity.LAST_MODIFIED, CommonUtils.formatDate(resource.lastModifiedDate()))
                .addHeader(HttpHeaders.Entity.CONTENT_LENGTH, resource.getFileLength() + "");

        if (isHead) {return builder.build();}

        try {
            builder.addBody(resource.getBytes());
            return builder.build();
        } catch (IOException e) {
            // internal server error
            e.printStackTrace();
        }

        return null;
    }


    // read until CRLF (exclude CRLF)
    private String readByCRLF(InputStreamReader isr) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        boolean cr = false;
        while ((ch = isr.read()) != -1) {
            if (cr && ch == '\n') {
                sb.setLength(sb.length() - 1); // remove the last CR
                break;
            }
            if (ch == '\r') cr = true;
            sb.append((char) ch);
        }
        return sb.toString();
    }

    private static HttpResponse.Builder basicReponseBuilder(HttpStatus status) {
        return HttpResponse.Builder.builder()
                .addStatusLine(HTTP_1_1, status)
                .addHeader(HttpHeaders.General.CONNECTION, "close")
                .addHeader(HttpHeaders.General.DATE, CommonUtils.currentDate())
                .addHeader(HttpHeaders.Response.SERVER, SERVER_NAME);
    }

}
