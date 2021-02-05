package com.arteriatech.ss.msecsales.rspl.common;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by e10526 on 15-06-2018.
 */

public class UtilsCurrency {
    public static SortedMap<Currency, Locale> currencyLocaleMap = new TreeMap(new Comparator<Currency>() {
        public int compare(Currency c1, Currency c2) {
            return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
        }
    });

    UtilsCurrency() {
    }

    public static String getCurrencySymbol(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        System.out.println(currencyCode + ":-" + currency.getSymbol((Locale)currencyLocaleMap.get(currency)));
        return currency.getSymbol((Locale)currencyLocaleMap.get(currency));
    }

    static {
        Locale[] var0 = Locale.getAvailableLocales();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            Locale locale = var0[var2];

            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (Exception var5) {
                ;
            }
        }

    }
}
