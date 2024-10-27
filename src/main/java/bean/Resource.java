package bean;

import utils.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

public class Resource {

    private final File file;
    private final URI uri;
    private static final String BASE_DIR = "resources";

    public Resource(URI uri) {
        this.uri = uri;
        this.file = new File(BASE_DIR + uri.getPath());
    }

    // check if file exists and is a file
    public boolean isValid() {
        return file == null || (file.exists() && file.isFile());
    }

    public long getFileLength() {
        assert isValid();
        return file.length();
    }

    public Date lastModifiedDate() {
        assert isValid();
        //
        return new Date(file.lastModified());
    }

    public boolean isModifiedSince(Date cachedModifiedDate) {
        assert isValid();
        // cachedModifiedDate is null -> assume modified
        return cachedModifiedDate == null || CommonUtils.after(lastModifiedDate(), cachedModifiedDate);
    }

    // file type -> HTTP content type
    public String getContentType() {
        assert isValid();

        String path = uri.getPath();
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        String fileExtension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) fileExtension = fileName.substring(i + 1).toLowerCase();

        switch (fileExtension) {
            case "html":
            case "htm":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "ico":
                return "image/x-icon";
            case "pdf":
                return "application/pdf";
            case "zip":
                return "application/zip";
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            default:
                return "application/octet-stream";
        }
    }

    // file -> byte array
    public byte[] getBytes() throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            int bytesRead = fis.read(data);
            if (bytesRead != file.length()) throw new IOException("Could not completely read the file");
            return data;
        }
    }

}
