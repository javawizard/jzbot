package jw.jzbot.fact;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by aboyd on 2016-01-19.
 */
public class Formatting {
    public static String shortDecimalToString(double value) {
        DecimalFormat format = new DecimalFormat("0");
        format.setMaximumFractionDigits(12);
        return format.format(value);
    }
}
