package com.sax.utils;

import com.sax.views.quanly.views.dialogs.SachDialog;

import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class CurrencyConverter {

    public static String parseString(long price) {
        return new DecimalFormat("#,###").format(price).replace(",", ".") + "đ";
    }

    public static long parseLong(String price) {
        return Long.parseLong(price.replace(".", "").replace("đ", "").replace("%", ""));
    }


    private static DefaultFormatterFactory defaultFormatterFactory;
    static
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

        NumberFormatter formatter = new NumberFormatter() {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.length() == 0) {
                    return null;
                }
                return super.stringToValue(text);
            }
        };
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        formatter.setFormat(decimalFormat);
        defaultFormatterFactory = new DefaultFormatterFactory(formatter);
    }

    public static DefaultFormatterFactory getVnCurrency() {
        return defaultFormatterFactory;
    }
}
