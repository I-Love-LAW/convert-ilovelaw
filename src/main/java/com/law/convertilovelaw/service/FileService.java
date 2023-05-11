package com.law.convertilovelaw.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FileService {
    String uploadFile(File file, String fileName) throws IOException; // used to upload a file
    File convertToFile(MultipartFile multipartFile, String fileName) throws IOException; // used to convert MultipartFile to File
    String getExtension(String fileName);  // used to get extension of a uploaded file
    File convertByteArrayToFile(ByteArrayResource byteArrayResource, String fileName) throws IOException;
}
