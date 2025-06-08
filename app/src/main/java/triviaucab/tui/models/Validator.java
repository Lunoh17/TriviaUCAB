/**
 * Clase utilitaria que contiene métodos estáticos para validación
 * de entradas, conversión a JSON y encriptación de contraseñas.
 *
 * Se usa ampliamente en todo el proyecto para asegurar integridad
 * de datos ingresados por el usuario y generar hash de contraseñas.
 *
 * @author Jose alejandro Padron
 */
package triviaucab.tui.models;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;



public class Validator {
    /**
     * Solicita al usuario que introduzca un número entero y valida que la entrada sea correcta.
     *
     * @param pregunta pregunta El texto que se muestra al usuario solicitando la entrada.
     * @param entrada entrada El objeto Scanner utilizado para leer la entrada del usuario
     * @return El número entero validado introducido por el usuario.
     */

    static public int validarInt(String pregunta, Scanner entrada) {
        boolean entradaValida;
        int opcion = -1;
        do {
            System.out.println(pregunta);
            if (entrada.hasNextInt()) {
                opcion = entrada.nextInt();
                entrada.nextLine(); // Limpiar buffer
                entradaValida = true;
            } else {
                System.out.println("Entrada no válida. Por favor, introduce un número entero.");
                entrada.nextLine(); // Descartar la entrada incorrecta
                entradaValida = false;
            }
        } while (!entradaValida);
        return opcion;
    }

    /**
     *Convierte un objeto Java a su representación en formato JSON.
     *
     * @param objeto El objeto que se desea convertir a JSON.
     * @return La cadena JSON correspondiente al objeto.
     */
    static public String convertirAJson(Object objeto) {
        Gson gson = new Gson();
        return gson.toJson(objeto);
    }


    /**
     *Calcula el hash SHA-256 de una cadena de texto.
     *
     * @param input La cadena de texto a la que se le aplicará el algoritmo SHA-256.
     * @return El hash SHA-256 como una cadena hexadecimal.
     */
    static public String calcularSha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesAHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }

    /**
     * Convierte un array de bytes en una cadena hexadecimal.
     *
     * @param bytes  El arreglo de bytes a convertir.
     * @return La cadena hexadecimal correspondiente.
     */
    static private String bytesAHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Verifica si un correo electrónico tiene un formato válido.
     * @param correo La cadena de texto que representa el correo electrónico.
     * @return true si el correo tiene un formato válido, false en caso contrario.
     */
    public static boolean validorCorreo(String correo) {
        final String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern patronDelCorreo = Pattern.compile(regex);
        Matcher matcher = patronDelCorreo.matcher(correo);
        return matcher.matches();
    }

    /**
     * Convierte una posición 1-based (desde 1) a 0-based (desde 0) para trabajar con índices de listas.
     * @param position position La posición 1-based.
     * @return La posición correspondiente 0-based.
     */
    public static int realPosition(int position) {
        return position - 1;
    }


}

