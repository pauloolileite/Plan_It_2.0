package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {

    public static String formatarData(String dataISO) {
        try {
            Date data = new SimpleDateFormat("yyyy-MM-dd").parse(dataISO);
            return new SimpleDateFormat("dd-MM-yyyy").format(data);
        } catch (Exception e) {
            return dataISO;
        }
    }

    public static String formatarPadrao(Object entrada) {
        try {
            if (entrada instanceof String) {
                Date data = new SimpleDateFormat("dd-MM-yyyy").parse((String) entrada);
                return new SimpleDateFormat("yyyy-MM-dd").format(data);
            } else if (entrada instanceof Date) {
                return new SimpleDateFormat("yyyy-MM-dd").format((Date) entrada);
            } else {
                throw new IllegalArgumentException("Tipo não suportado para formatação.");
            }
        } catch (Exception e) {
            return entrada.toString();
        }
    }
    public static String formatar(Date data, String formato) {
        return new SimpleDateFormat(formato).format(data);
    }
}
