package com.law.convertilovelaw.service;

import com.law.convertilovelaw.exception.HistoryNotFound;
import com.law.convertilovelaw.exception.UsernameNotMatch;
import com.law.convertilovelaw.model.ConvertHistory;
import com.law.convertilovelaw.repository.ConvertHistoryRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ConvertServiceImpl implements ConvertService{
    @Autowired
    ConvertHistoryRepository convertHistoryRepository;

    @Override
    public ConvertHistory createNewConvertHistory(String username, String fileName) {
        ConvertHistory newConvertHistory = new ConvertHistory();
        newConvertHistory.setUsername(username);
        newConvertHistory.setProgress(0);
        newConvertHistory.setFilename(fileName);
        convertHistoryRepository.save(newConvertHistory);
        return newConvertHistory;
    }

    public byte[] convertFromPdf(ConvertHistory newConvertHistory, byte[] inputStream, String imageType, ImageType colorType, boolean singleImage, int DPI) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(inputStream))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            List<BufferedImage> images = new ArrayList<>();

            // Create images of all pages
            for (int i = 0; i < pageCount; i++) {
                images.add(pdfRenderer.renderImageWithDPI(i, 300, colorType));
                newConvertHistory.setProgress((float) (i + 1) /(pageCount+1));
                convertHistoryRepository.save(newConvertHistory);
            }

            if (singleImage) {
                // Combine all images into a single big image
                BufferedImage combined = new BufferedImage(images.get(0).getWidth(), images.get(0).getHeight() * pageCount, BufferedImage.TYPE_INT_RGB);
                Graphics g = combined.getGraphics();
                for (int i = 0; i < images.size(); i++) {
                    g.drawImage(images.get(i), 0, i * images.get(0).getHeight(), null);
                }
                images = Arrays.asList(combined);
            }

            // Create a ByteArrayOutputStream to save the image(s) to
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (singleImage) {
                // Write the image to the output stream
                ImageIO.write(images.get(0), imageType, baos);

            } else {
                // Zip the images and return as byte array
                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    for (int i = 0; i < images.size(); i++) {
                        BufferedImage image = images.get(i);
                        try (ByteArrayOutputStream baosImage = new ByteArrayOutputStream()) {
                            ImageIO.write(image, imageType, baosImage);

                            // Add the image to the zip file
                            zos.putNextEntry(new ZipEntry(String.format("page_%d.%s", i + 1, imageType.toLowerCase())));
                            zos.write(baosImage.toByteArray());
                        }
                    }
                }
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public ConvertHistory getLastHistory(String username) {
        ArrayList<ConvertHistory> result = convertHistoryRepository.findAllByUsername(username);
        return result.get(result.size()-1);
    }

    @Override
    public ArrayList<ConvertHistory> getAllHistory(String username) {
        return convertHistoryRepository.findAllByUsername(username);
    }

    @Override
    public void deleteHistory(String id, String username) {
        Optional<ConvertHistory> optionalConvertHistory= convertHistoryRepository.findById(id);

        if (optionalConvertHistory.isPresent()) {
            ConvertHistory convertHistory = optionalConvertHistory.get();
            if (convertHistory.getUsername().equals(username)) {
                convertHistoryRepository.delete(optionalConvertHistory.get());
            }
            else {
                throw  new UsernameNotMatch("You do not have permission to perform this action.");
            }
        } else {
            throw new HistoryNotFound("History with id " + id + " not found");
        }

    public void setResult(ConvertHistory convertHistory, String result) {
        convertHistory.setProgress(1);
        convertHistory.setResult(result);
        convertHistoryRepository.save(convertHistory);
    }
}
