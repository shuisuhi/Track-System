package org.example.tlias.controller;

import org.example.tlias.utils.CosManager;
import org.example.tlias.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UploadController {

    @Autowired
    CosManager cosManager;


    /*    @PostMapping("/upload")
        Result upload(@RequestParam("image") MultipartFile file) throws IOException {
            String orginFileName= file.getOriginalFilename();
            assert orginFileName != null;
            String newFileName = UUID.randomUUID() +orginFileName.substring(orginFileName.lastIndexOf("."));
            file.transferTo(new File("D:/image/"+newFileName));
            return Result.success();
        }*/
    @PreAuthorize("hasAuthority('CAN_POST')")
    @PostMapping("/upload")
    Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 将MultipartFile转换为临时文件
        File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
        file.transferTo(tempFile);
        
        try {
            String url = cosManager.upload(tempFile);
            return Result.success(url);
        } finally {
            // 删除临时文件
            tempFile.delete();
        }
    }
}
