package com.example.backendchat.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.backendchat.exception.UploadFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class UploadFileUtil {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        try {
            String resourceType = getResourceType(file);
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", resourceType));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new UploadFileException("Upload file failed!");
        }
    }

    public String uploadImage(byte[] bytes) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap("resource_type", "image"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new UploadFileException("Upload image failed!");
        }
    }

    public void destroyFileWithUrl(String url) {
        int startIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.lastIndexOf(".");
        String publicId = url.substring(startIndex, endIndex);
        try {
            Map<String, String> params = new HashMap<>();
            if (url.contains("/video/")) {
                params.put("resource_type", "video");
            } else if (url.contains("/image/")) {
                params.put("resource_type", "image");
            } else if (url.contains("/audio/")) {
                params.put("resource_type", "audio");
            } else if (url.contains("/raw/")) { // Assuming you have a "document" folder
                params.put("resource_type", "raw"); // Use "raw" for documents
            }
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, params);
            log.info(String.format("Destroy file public id %s %s", publicId, result.toString()));
        } catch (IOException e) {
            throw new UploadFileException("Remove file failed!");
        }
    }

    private static String getResourceType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return "image";
            } else if (contentType.startsWith("video/")) {
                return "video";
            } else if (contentType.startsWith("audio/")) {
                return "audio";
            } else if (contentType.startsWith("application/")) {
                return "raw";
            } else {
                return "auto";
            }
        } else {
            throw new UploadFileException("Invalid file!");
        }
    }

}
