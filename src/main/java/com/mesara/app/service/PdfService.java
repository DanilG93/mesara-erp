package com.mesara.app.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generatePdf(String templateName, Map<String, Object> data) {

        Context context = new Context();
        context.setVariables(data);

        // Render HTML iz Thymeleaf template-a
        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // baseUrl je potreban da bi CSS i slike radile
            builder.withHtmlContent(htmlContent, "/");

            // Učitavanje fonta iz classpath-a (radi i u Docker/JAR)
            ClassPathResource fontResource = new ClassPathResource("static/fonts/Roboto-Regular.ttf");

            builder.useFont(() -> {
                try {
                    InputStream is = fontResource.getInputStream();
                    return is;
                } catch (Exception e) {
                    throw new RuntimeException("Font nije pronađen", e);
                }
            }, "Roboto");

            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Greška pri generisanju PDF dokumenta", e);
        }
    }
}