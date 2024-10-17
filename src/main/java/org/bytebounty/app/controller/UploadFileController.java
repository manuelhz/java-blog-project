package org.bytebounty.app.controller;

import org.bytebounty.app.util.FileStorage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UploadFileController {

    @PostMapping("/image-upload")
    public String uploadImage(MultipartFile file) {
        String fileName = FileStorage.save(file, "tmp");
        FileStorage.waitt();        
        return fileName;
    }
    
}
