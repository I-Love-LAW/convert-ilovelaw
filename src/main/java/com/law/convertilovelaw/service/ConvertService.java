package com.law.convertilovelaw.service;


import com.law.convertilovelaw.model.ConvertHistory;
import org.apache.pdfbox.rendering.ImageType;

import java.io.IOException;
import java.util.ArrayList;

public interface ConvertService {
    ConvertHistory createNewConvertHistory(String username);
    byte[] convertFromPdf(ConvertHistory newConvertHistory, byte[] inputStream, String imageType, ImageType colorType, boolean singleImage, int DPI) throws IOException;

    ConvertHistory getLastHistory(String username);
    ArrayList<ConvertHistory> getAllHistory(String username);
}