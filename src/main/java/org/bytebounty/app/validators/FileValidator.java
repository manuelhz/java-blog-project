package org.bytebounty.app.validators;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class FileValidator {

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/jpeg", "image/png");

    private static final long MAX_FILE_SIZE = 500*1024; // 500KB

    public static String validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return "File is empty!";
        }

        String contentType = file.getContentType();
        if (!ALLOWED_FILE_TYPES.contains(contentType)) {
            return "File type not valid!";
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return "File size must be less than 500KB!";
        }

        return "OK";
        
    }

    public static String sanitizeFileName(String fileName) {

        if (fileName == null) {
            return null;
        }

        // Prevent directory traversal attacks
        return fileName.replaceAll("\\.\\.", "").replaceAll("/", "").replaceAll("&", "");
    }


    public static boolean resolvePath(final String baseDirPath, final String userPath) {
        File file = new File(baseDirPath, userPath);
        try {
            if (file.getCanonicalPath().startsWith(baseDirPath)) {
                return true;
            }
            else {
                return false;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        
    }
  
}
