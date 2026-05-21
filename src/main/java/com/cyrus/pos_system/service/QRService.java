package com.cyrus.pos_system.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QRService {

    public QRService() {
        // No filesystem usage anymore; QR codes are produced in-memory as Base64 strings.
    }

    public String decode(InputStream in) throws Exception {
        BufferedImage image = ImageIO.read(in);
        if (image == null) throw new IllegalArgumentException("Could not read image");

        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    public String decode(File file) throws Exception {
        try (InputStream is = new FileInputStream(file)) {
            return decode(is);
        }
    }

    public String decode(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            return decode(is);
        }
    }

    public String generateQRCodeBase64(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            byte[] pngData = baos.toByteArray();
            return Base64.getEncoder().encodeToString(pngData);
        }
    }

    public String generateQRCodeBase64ForId(String id) throws WriterException, IOException {
        return generateQRCodeBase64(id, 300, 300);
    }
}
