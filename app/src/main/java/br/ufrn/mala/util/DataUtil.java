package br.ufrn.mala.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Joel Felipe on 03/10/2017.
 */

public class DataUtil {

    public static String formatLongToDate(Long dateInMillis){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR")).format(new Date(dateInMillis));
    }
}
