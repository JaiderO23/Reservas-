package util;

import java.security.SecureRandom;

public class CodigoGenerator {

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CODIGO = 10;
    private static final SecureRandom random = new SecureRandom();

    public static String generarCodigo() {
        StringBuilder codigo = new StringBuilder(LONGITUD_CODIGO);

        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            int index = random.nextInt(CARACTERES.length());
            codigo.append(CARACTERES.charAt(index));
        }

        return codigo.toString();
    }
}