package org.example.tlias.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Component
public class CosManager {

    @Autowired
    private CosClientConfig cosClientConfig;

    @Autowired
    private COSClient cosClient;

    /**
     * 实现上传图片到COS
     */
    public String upload(File file) throws IOException {
        String originalFilename = file.getName();
        String uuid = RandomUtil.randomString(16);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/trail/%s", uploadFilename);

        // 上传文件到 COS
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), uploadPath, file);
        cosClient.putObject(putObjectRequest);

        String url = cosClientConfig.getHost() + "/" + uploadPath;
        
        return url;
    }
}
