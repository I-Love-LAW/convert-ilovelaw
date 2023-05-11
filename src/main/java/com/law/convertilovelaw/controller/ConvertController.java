package com.law.convertilovelaw.controller;

import com.law.convertilovelaw.exception.HistoryNotFound;
import com.law.convertilovelaw.exception.UsernameNotMatch;
import com.law.convertilovelaw.model.ConvertHistory;
import com.law.convertilovelaw.payload.response.MessageResponse;
import com.law.convertilovelaw.service.ConvertService;
import com.law.convertilovelaw.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.rendering.ImageType;

import java.io.IOException;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/convert")
public class ConvertController {
    @Autowired
    ConvertService convertService;

    @Autowired
    FileService fileService;

    private String getMediaType(String imageFormat) {
        if (imageFormat.equalsIgnoreCase("PNG"))
            return "image/png";
        else if (imageFormat.equalsIgnoreCase("JPEG") || imageFormat.equalsIgnoreCase("JPG"))
            return "image/jpeg";
        else if (imageFormat.equalsIgnoreCase("GIF"))
            return "image/gif";
        else
            return "application/octet-stream";
    }

    @PostMapping("/pdf-to-img")
    public ResponseEntity<?> convertToImage(@RequestParam("fileInput") MultipartFile file, @RequestParam("imageFormat") String imageFormat,
                                                   @RequestParam("singleOrMultiple") String singleOrMultiple, @RequestParam("colorType") String colorType, @RequestParam("dpi") String dpi, @RequestParam("username") String username) throws IOException {
        byte[] pdfBytes = file.getBytes();
        ImageType colorTypeResult = ImageType.RGB;
        if ("greyscale".equals(colorType)) {
            colorTypeResult = ImageType.GRAY;
        } else if ("blackwhite".equals(colorType)) {
            colorTypeResult = ImageType.BINARY;
        }
        // returns bytes for image
        boolean singleImage = singleOrMultiple.equals("single");
        byte[] result = null;

        ConvertHistory newConvertHistory = convertService.createNewConvertHistory(username, file.getOriginalFilename());
        try {
            result = convertService.convertFromPdf(newConvertHistory, pdfBytes, imageFormat.toUpperCase(), colorTypeResult, singleImage, Integer.valueOf(dpi));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Convertion Proccess is failed!");
        }

        ByteArrayResource resource = new ByteArrayResource(result);
        String fileName = "";
        if (singleImage) {
            fileName = newConvertHistory.getId() + "." + imageFormat.toLowerCase();
        } else {
            fileName = newConvertHistory.getId() + "_convertedToImages.zip";
        }
        String resultUrl = fileService.uploadFile(fileService.convertByteArrayToFile(resource, fileName), fileName);
        convertService.setResult(newConvertHistory, resultUrl);
        return ResponseEntity.ok("File has successfully converted");
//        if (singleImage) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType(getMediaType(imageFormat)));
//            return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
//        } else {
//            // return the Resource in the response
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriginalFilename().replaceFirst("[.][^.]+$", "") + "_convertedToImages.zip")
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(resource.contentLength()).body(resource);
//        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<?> getHistory(@PathVariable String username){
        ArrayList<ConvertHistory> result = convertService.getAllHistory(username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/last/{username}")
    public ResponseEntity<?> getLastHistory(@PathVariable String username){
        ConvertHistory result = convertService.getLastHistory(username);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/history/{username}")
    public ResponseEntity<?> deleteHistory(@RequestParam String id, @PathVariable String username){
        try {
            convertService.deleteHistory(id, username);
            return ResponseEntity.ok().body(new MessageResponse("History deleted successfully!"));
        }
        catch (HistoryNotFound e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        }
        catch (UsernameNotMatch e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        }

    }
}
