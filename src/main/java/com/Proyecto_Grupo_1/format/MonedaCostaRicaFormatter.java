package com.Proyecto_Grupo_1.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Formatea importes de AGLO siempre como colones costarricenses, sin depender
 * de que el locale de idioma seleccionado tenga una moneda asociada.
 */
@Component("monedaCostaRica")
public class MonedaCostaRicaFormatter {

    private static final Currency COLON_COSTARRICENSE = Currency.getInstance("CRC");
    private static final String SIMBOLO_COLON = "₡";

    public String formatear(Number importe) {
        Locale locale = LocaleContextHolder.getLocale();
        DecimalFormat formato = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        formato.setCurrency(COLON_COSTARRICENSE);

        DecimalFormatSymbols simbolos = formato.getDecimalFormatSymbols();
        simbolos.setInternationalCurrencySymbol(COLON_COSTARRICENSE.getCurrencyCode());
        simbolos.setCurrencySymbol(SIMBOLO_COLON);
        formato.setDecimalFormatSymbols(simbolos);
        formato.setPositivePrefix(SIMBOLO_COLON);
        formato.setPositiveSuffix("");
        formato.setNegativePrefix("-" + SIMBOLO_COLON);
        formato.setNegativeSuffix("");

        return formato.format(importe);
    }
}
