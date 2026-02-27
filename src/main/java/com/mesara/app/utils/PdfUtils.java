package com.mesara.app.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PdfUtils {
    private final DecimalFormat df;

    public PdfUtils() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        // Pattern #.### prikazuje do 3 decimale, ali briše nule na kraju (trailing zeros)
        this.df = new DecimalFormat("0.0##", symbols);
    }

    public String format(Double value) {
        if (value == null || value == 0) return "0.0";
        return df.format(value);
    }
}
