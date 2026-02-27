package com.mesara.app.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generatePdf(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);

        // Thymeleaf renderuje nas pdf-report.html sa podacima i slikama
        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, "/"); // "/" je baseUrl

            // Registracija fonta za Č, Ć, Š...
            File fontFile = new ClassPathResource("static/fonts/Roboto-Regular.ttf").getFile();
            if (fontFile.exists()) {
                builder.useFont(fontFile, "Roboto");
            }

            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Greška pri generisanju PDF dokumenta", e);
        }
    }
}
