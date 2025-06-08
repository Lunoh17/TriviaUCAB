package triviaucab.tui;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Collectors;

import triviaucab.tui.models.Usuario;
import triviaucab.tui.models.Ficha;
import triviaucab.tui.models.TableTop;
import triviaucab.tui.models.Questions;
import triviaucab.tui.models.Validator;
import com.google.gson.reflect.TypeToken;

/**
 * Clase principal de la aplicación Trivia UCAB.
 * Gestiona la configuración del juego, los usuarios, las preguntas y las
 * partidas.
 */
public class AppTabletop {

    Scanner scanner = new Scanner(System.in);
    ArrayList<triviaucab.tui.models.Usuario> listaUsuarios = new ArrayList<Usuario>();
    Questions questions = new Questions();
    Usuario usuarioActual;
    String homeFolder = System.getProperty("user.home");
    final int MAX_PLAYERS = 6; // Máximo de jugadores
    ArrayList<Ficha> fichasJugadores = new ArrayList<>();
    TableTop partida;

    /**
     * Método principal que inicia la aplicación.
     * Permite cargar la configuración, usuarios y manejar el menú principal.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {

        System.out.println("Bienvenido a la trivia UCAB configuration!");

        AppTabletop aplicacion = null;
        aplicacion = new AppTabletop();
        try {
            aplicacion.saveUsersJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor de la clase. Inicializa la aplicación, carga la configuración,
     * los usuarios y presenta un menú de opciones al usuario.
     */
    public AppTabletop() {
        System.out.println("Bienvenidos al menu para iniciar el juego");
        loadJson();
        loadUsuariosJson();
        int opcion = -1;
        while (opcion != 0) {
            opcion = Validator.validarInt(
                    "1. Iniciar sesión de  usarios Registrados\n2. Cargar partida Anterior\n" + "0. Salir",
                    scanner);
            if (opcion == 1) {
                cargarUsuarios();
                if (fichasJugadores.isEmpty()) {
                    System.out.println("No se han registrado usuarios. Saliendo del juego.");
                } else {
                    this.partida = new TableTop(fichasJugadores, scanner, questions);
                    try {
                        this.saveUsersJson();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (opcion == 2) {
                System.out.println("Cargar partida anterior");
                loadJson();
                loadUsuariosJson();
                partida = new TableTop(scanner, questions);
            } else if (opcion == 0) {
                System.out.println("Hasta la proxima");
            } else {
                System.out.println("Opcion no valida, por favor ingrese 0,1 o 2");
            }
        }

    }

    /**
     * Método para cargar los usuarios registrados. Permite a los jugadores iniciar
     * sesión.
     * <p>
     * Registra hasta un máximo de 6 jugadores, y crea fichas para cada uno.
     */
    public void cargarUsuarios() {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            Usuario usuario = joinSesion();
            if (usuario != null) {
                fichasJugadores.add(new Ficha(usuario.getUserName(), usuario,
                        null));
                System.out.println("Usuario " + usuario.getUserName() + " registrado correctamente.");
                if (!doContinue(" agregando otro usuario?")) {
                    break;
                }
            } else {
                System.out.println("Usuario no encontrado o contraseña incorrecta. Intente nuevamente.");
                if (!doContinue(" agregando otro usuario?")) {
                    break;
                }
                i--; // Decrementa el contador para permitir reintentar
            }
        }
    }

    /**
     * Método que permite registrar múltiples usuarios nuevos.
     * <p>
     * Valida que la cantidad de usuarios sea adecuada y los almacena en la lista.
     */
    public void addUsers() {
        int cantidadUsuarios = Validator.validarInt("Cuantos  usuario deseas registrar: ", scanner);
        while (cantidadUsuarios > 9999 - listaUsuarios.size() || cantidadUsuarios <= 0) {
            cantidadUsuarios = Validator.validarInt(
                    "Exisiste la cantidad de usuarios, El maximos de usuario es " + (6 - listaUsuarios.size()),
                    scanner);
        }
        for (int i = 0; i < cantidadUsuarios; i++) {
            Usuario usuarioNuevo = new Usuario(askName(), askPassword());
            listaUsuarios.add(usuarioNuevo);
        }
    }

    /**
     * Pregunta al usuario si desea continuar con la acción actual.
     *
     * @param message El mensaje que se mostrará al usuario.
     * @return true si desea continuar, false en caso contrario.
     */
    private boolean doContinue(String message) {
        int option = -1;
        while (option != 0 && option != 1) {
            option = Validator.validarInt("Desea continuar" + message + "0=No , 1=Si (0/1): ", scanner);
        }
        return (option == 1);
    }

    /**
     * Solicita al usuario ingresar un nombre válido (correo electrónico).
     *
     * @return Nombre de usuario ingresado.
     */
    public String askName() {
        System.out.println("Ingrese su nombre de usuario");
        String usuario = scanner.nextLine();
        while (!Validator.validorCorreo(usuario)) { // no toma el "@" corregir
            System.out.println("El usuario que ingreso no es valido, por favor ingrese un correo valido");
            usuario = scanner.nextLine();
        }

        return usuario;
    }

    /**
     * Solicita una contraseña válida de 6 caracteres.
     *
     * @return Contraseña ingresada.
     */
    public String verifyPassword() {
        System.out.println("Ingrese su password");
        String password = scanner.nextLine();
        while (password.length() != 6) {
            System.out.println("La clave no cumple con los 6 caracteres");
            System.out.println("Ingrese una clave valida");
            password = scanner.nextLine();
        }
        return password;
    }

    /**
     * Solicita al usuario que ingrese y repita una contraseña válida.
     *
     * @return Contraseña confirmada.
     */
    public String askPassword() {
        String password = verifyPassword();
        String repeatPassword;
        System.out.println("Repita la clave con su password");
        repeatPassword = scanner.nextLine();
        while (!Objects.equals(repeatPassword, password)) {
            System.out.println("La clave no es identica a la anterior");
            System.out.println("Por favor ingrese la clave");
            repeatPassword = scanner.nextLine();
        }
        return password;
    }

    /**
     * Inicia sesión con validación de usuario y contraseña.
     *
     * @return El usuario que se ha autenticado correctamente o null si no se
     *         encuentra.
     */
    public Usuario joinSesion() {
        System.out.println("Inciar sesión");
        String searchName = askName();
        ArrayList<Usuario> usuariosFiltrados = this.listaUsuarios.stream()
                .filter(usuario -> searchName.equals(usuario.getUserName()))
                .collect(Collectors.toCollection(ArrayList<Usuario>::new));
        if (!usuariosFiltrados.isEmpty()) {
            String password = this.askPassword();
            for (Usuario usuario : usuariosFiltrados) {
                if (usuario.getPassword().equals(Validator.calcularSha256(password))) {
                    return usuario;
                } else {
                    System.out.println("La contraseña ingresada no es correcta");
                    return null;
                }
            }

        } else {
            System.out.println("El usuario no se encuentra registrado");
            return null;
        }
        return null;
    }

    /**
     * Muestra una lista de preguntas y permite al usuario seleccionar una por
     * índice.
     *
     * @param option Tipo de lista (1 = espera, 2 = aprobadas, 3 = rechazadas, 4 =
     *               aprobadas con visual diferente).
     * @return Índice seleccionado por el usuario (ajustado a base 0).
     */
    public int questionSelector(int option) {
        int selected = -1;
        do {
            if (option != 4)
                questions.visualQuestions(option, usuarioActual);
            else
                questions.visualAproved(usuarioActual);
            selected = Validator.validarInt("Ingrese el número de la pregunta o 0 para salir : ", scanner);

        } while (selected > questions.getSize(option) || selected < 0);

        return selected - 1;
    }

    /**
     * Método auxiliar para seleccionar una pregunta en espera o rechazada.
     *
     * @param option 1 para preguntas en espera, 2 para rechazadas.
     * @return Índice seleccionado.
     */
    public int waitingOrDeleted(int option) {
        int selected = -1;
        if (option == 1) {
            selected = questionSelector(option);
        } else if (option == 2) {
            selected = questionSelector(3);
        }
        return selected;
    }

    /**
     * Guarda la lista de usuarios en un archivo JSON en la carpeta `.config`.
     *
     * @throws IOException Si ocurre un error de escritura.
     */
    public void saveUsersJson() throws IOException {
        String destinyFolder = homeFolder + File.separator + ".config";
        File destinyFolderFile = new File(destinyFolder);
        if (!destinyFolderFile.exists()) {
            boolean created = destinyFolderFile.mkdir();
            if (!created)
                throw new IOException();
        }
        Gson gson = new Gson();
        String json = gson.toJson(this.listaUsuarios);
        File data = new File(destinyFolder + File.separator + "users.json");

        try (FileWriter writer = new FileWriter(data)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Guarda las preguntas actuales (objeto Questions) en un archivo JSON.
     */
    public void saveJson() {
        String destinyFolder = homeFolder + File.separator + ".config";
        File destinyFolderFile = new File(destinyFolder);
        if (!destinyFolderFile.exists()) {
            boolean created = destinyFolderFile.mkdir();
            if (!created) {
                throw new RuntimeException();
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(questions);
        File data = new File(destinyFolder + File.separator + "data.json");

        try (FileWriter writer = new FileWriter(data)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Carga la información de las fichas de jugadores desde un archivo JSON.
     */

    /**
     * Carga los usuarios desde el archivo JSON. Si no existe, se crea uno nuevo.
     */
    public void loadUsuariosJson() {
        Gson gson = new Gson();
        String destinyFolder = homeFolder + File.separator + ".config";
        File destinyFolderFile = new File(destinyFolder);
        if (!destinyFolderFile.exists()) {
            boolean created = destinyFolderFile.mkdir();
            if (!created) {
                throw new RuntimeException();
            }
        }
        var a = new File(destinyFolderFile + File.separator + "users.json");
        if (!(a.exists())) {
            try {
                boolean created = a.createNewFile();
                if (!created)
                    throw new IOException();
                this.listaUsuarios = new ArrayList<Usuario>();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (FileReader r = new FileReader(destinyFolderFile + File.separator + "users.json")) {
                BufferedReader bufferedReader = new BufferedReader(r);
                Type listType = new TypeToken<ArrayList<Usuario>>() {
                }.getType();
                listaUsuarios = gson.fromJson(bufferedReader, listType);
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo JSON", e);
            }
        }

    }

    /**
     * Carga las preguntas desde el archivo JSON. Si no existe, se crea uno nuevo.
     */
    public void loadJson() {
        Gson gson = new Gson();
        String destinyFolder = homeFolder + File.separator + ".config";
        File destinyFolderFile = new File(destinyFolder);
        if (!destinyFolderFile.exists()) {
            boolean created = destinyFolderFile.mkdir();
            if (!created) {
                throw new RuntimeException();
            }
        }

        var a = new File(destinyFolderFile + File.separator + "data.json");
        if (!(a.exists())) {
            try {
                boolean created = a.createNewFile();
                if (!created)
                    throw new IOException();
                this.questions = new Questions();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (FileReader reader = new FileReader(destinyFolderFile + File.separator + "data.json")) {
                BufferedReader bufferedReader = new BufferedReader(reader);
                questions = gson.fromJson(bufferedReader, Questions.class);
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo JSON", e);
            }
        }
    }

}
