package triviaucab.tui;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Collectors;

import triviaucab.tui.models.*;
import com.google.gson.reflect.TypeToken;

public class AppConfig {

    Scanner scanner = new Scanner(System.in);
    ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    Questions questions = new Questions();
    Usuario usuarioActual;
    String homeFolder = System.getProperty("user.home");


    public static void main(String[] args) {

        System.out.println("Bienvenido a la trivia UCAB configuration!");
        int opcion = 1;
        AppConfig aplicacion = new AppConfig();
        aplicacion.loadJson();
        aplicacion.loadUsuariosJson();
        do {
            opcion = aplicacion.principalMenu();
        } while (opcion != 0);
        aplicacion.saveJson();
        try {
            aplicacion.saveUsersJson();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Permite registrar múltiples usuarios nuevos.
     * Valida que la cantidad sea adecuada y almacena los usuarios en la lista.
     */
    public void addUsers() {
        int cantidadUsuarios = Validator.validarInt("Cuantos  usuario deseas registrar: ", scanner);
        while (cantidadUsuarios > 9999 - listaUsuarios.size() || cantidadUsuarios <= 0) {
            cantidadUsuarios = Validator.validarInt("Exidiste la cantidad de usuarios, El maximos de usuario es " + (6 - listaUsuarios.size()), scanner);
        }
        for (int i = 0; i < cantidadUsuarios; i++) {
            Usuario usuarioNuevo = new Usuario(askName(), crearPassword());
            listaUsuarios.add(usuarioNuevo);
        }
    }

    /**
     * Pregunta al usuario si desea continuar con la acción actual.
     *
     * @return true si desea continuar, false en caso contrario.
     */
    private boolean doContinue() {
        int option = -1;
        while (option != 0 && option != 1) {
            option = Validator.validarInt("Desea continuar 0=No , 1=Si (0/1): ", scanner);
        }
        return (option == 1);
    }


    /**
     * Permite al usuario ingresar una nueva pregunta, su respuesta y categoria.
     * La pregunta queda en espera de aprobación.
     *
     * @param usuario Usuario que esta registrando la pregunta.
     */
    public void addQuestion(Usuario usuario) {
        String question;
        String answer;
        Category categoriaActual;
        int cont = 1;

        do {
            cont = 1;
            System.out.println("Ingrese la pregunta: ");
            question = scanner.nextLine();
            System.out.println("Ingrese la respuesta: ");
            answer = scanner.nextLine();
            System.out.println("Ingrese la categoria: ");
            for (Category category : Category.values()) {
                System.out.println(cont + ") " + category);
                cont++;
            }
            int num = Validator.validarInt("", scanner);

            categoriaActual = Category.values()[num - 1];
            questions.addWaitApproved(new Question(question, answer, usuario.userName, categoriaActual));
        } while (doContinue());
    }


    /**
     * Solicita al usuario ingresar un nombre válido (correo electrónico).
     *
     * @return Nombre de usuario ingresado.
     */
    public String askName() {
        System.out.println("Ingrese su correo: ");
        String usuario = scanner.nextLine();
        while (!Validator.validorCorreo(usuario)) {      //no toma el "@" corregir
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
    public String crearPassword() {
        String password = verifyPassword();
        String repeatPassword;
        System.out.println("Repita su password");
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
     * @return -1 si el inicio de sesión fue exitoso, 1 para reintentar, 0 para salir.
     */
    public int joinSesion() {
        int opcion = 0;
        System.out.println("Inciar sesión");
        String searchName = askName();
        ArrayList<Usuario> usuariosFiltrados = this.listaUsuarios.stream()
                .filter(usuario -> searchName.equals(usuario.getUserName()))
                .collect(Collectors.toCollection(ArrayList<Usuario>::new));
        if (!usuariosFiltrados.isEmpty()) {
            String password = this.verifyPassword();
            for (Usuario usuario : usuariosFiltrados) {
                if (usuario.getPassword().equals(Validator.calcularSha256(password))) {
                    this.usuarioActual = usuario;
                    opcion = -1;
                } else {
                    do {
                        System.out.println("La contraseña ingresada no es correcta");
                        opcion = Validator.validarInt("1) Desea iniciar sesion\n0) Salir", scanner);
                    } while (opcion != 0 && opcion != 1);
                }
            }

        } else {
            do {
                System.out.println("El usuario no se encuentra registrado");
                opcion = Validator.validarInt("1) Desea iniciar sesion\n0) Salir", scanner);
            } while (opcion != 0 && opcion != 1);
        }
        return opcion;
    }

    /**
     * Muestra el menú principal de la aplicación.
     * Permite registrar usuarios, iniciar sesión o salir.
     *
     * @return opción elegida (0 para salir).
     */
    public int principalMenu() {
        int opcion = Validator.validarInt("Desea iniciar sesion o registrar un usuario?\n1) Registrar Usuario.\n2) Iniciar Sesión.\n0) Salir", scanner);

        switch (opcion) {
            case 1:
                System.out.println("Registro de Usuario");
                usuarioActual = new Usuario(askName(), crearPassword());
                listaUsuarios.add(usuarioActual);
                opcion = this.questionsMenu();

                break;
            case 2:
                do {
                    opcion = joinSesion();
                } while (0 != opcion && opcion != -1 && opcion != 1);
                if (opcion == -1)
                    opcion = this.questionsMenu();
                break;

            case 0:
                System.out.println("Hasta la próxima");
                return opcion;

            default:
                System.out.println("Opcion no valida");
                break;

        }

        return opcion;
    }

    /**
     * Muestra una lista de preguntas y permite al usuario seleccionar una por índice.
     *
     * @param option Tipo de lista (1 = espera, 2 = aprobadas, 3 = rechazadas, 4 = aprobadas con visual diferente).
     * @return Índice seleccionado por el usuario (ajustado a base 0).
     */
    public int questionSelector(int option) {
        int selected = -1;
        do {
            if (option != 4) questions.visualQuestions(option, usuarioActual);
            else questions.visualAproved(usuarioActual);
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
     * Muestra el menú de preguntas una vez iniciada sesión.
     * Permite realizar operaciones como agregar, aprobar, modificar o eliminar preguntas.
     *
     * @return -1 al cerrar sesión.
     */
    public int questionsMenu() {
        int option;
        do {
            option = Validator.validarInt("""
                    \\n--- Menú de Preguntas ---
                    1. Aprobar pregunta
                    2. Rechazar pregunta
                    3. Ver todas las preguntas
                    4. Modificar preguntas
                    5. Eliminar preguntas
                    6. Modicar tiempo de la pregunta
                    7. Agregar preguntas
                    0. Cerrar sesion
                    """, scanner);
            switch (option) {
                case 1:
                    System.out.println("Aprobar una pregunta.");
                    int selected = questionSelector(4);
                    questions.addApproved(selected, usuarioActual);
                    break;
                case 2:
                    System.out.println(" Rechazar una pregunta.");
                    questions.addRejeter(questionSelector(1));
                    break;
                case 3:
                    System.out.println("Visualizacion de todas las preguntas.");

                    System.out.println("Preguntas en espera");
                    questions.visualQuestions(1, usuarioActual);
                    System.out.println("Preguntas aprobadas");
                    questions.visualQuestions(2, usuarioActual);
                    System.out.println("Preguntas rechazadas");
                    questions.visualQuestions(3, usuarioActual);

                    System.out.println("Enter para continuar");
                    scanner.nextLine();
                    // Aquí podríamos agregar la lógica para agregar las  preguntas del JSON o base de datos
                    break;
                case 4:
                    int listNumber;
                    do {
                        System.out.println("De que lista desea modificar la pregunta.");
                        listNumber = (Validator.validarInt("1) En espera.\n2) Rechazadas", scanner));
                        if (listNumber > 2 || listNumber < 1)
                            System.out.println("Ingrese un numero valido del 1 al 2");
                    } while (listNumber > 2 || listNumber < 1);
                    int position = waitingOrDeleted(listNumber);
                    System.out.println("Modificar una pregunta.");
                    int modificationNumber = Validator.validarInt("1)Desea modificar las preguntas\n,2)Desea modificar las categorias\n,3)Desea Cambiar las Respuestas", scanner);
                    String modifyString;
                    switch (modificationNumber) {
                        case 1:
                            System.out.println("Escriba su modificacion.");
                            modifyString = scanner.nextLine();
                            questions.modifyQuestion(listNumber, position, modifyString);
                            break;
                        case 2:
                            Category categoriaActual;
                            int num = 0;
                            do {
                                int cont = 0;
                                System.out.println("Ingrese la categoria de la pregunta: ");
                                for (Category category : Category.values()) {
                                    System.out.println(cont + ") " + category);
                                    cont++;
                                }

                                num = Validator.validarInt("", scanner);
                                if (num > 5 || num < 0) {
                                    System.out.println("ERROR, Ingrese una opcion validada del 0 al 5");
                                }
                            } while (num > 5 || num < 0);
                            categoriaActual = Category.values()[num];
                            questions.modifyCategory(listNumber, position, categoriaActual);
                            break;
                        case 3:
                            System.out.println("Escriba su modificacion.");
                            modifyString = scanner.nextLine();
                            questions.modifyAnswer(listNumber, position, modifyString);
                            break;
                        default:
                            System.out.println("Opcion no valida");
                            break;
                    }
                    break;

                case 5:
                    int listaEliminar = 0;
                    do {
                        System.out.println("De que lista desea eliminar la pregunta.");
                        listaEliminar = Validator.validarInt("1) En espera.\n2) Aprovadas.\n3) Rechazadas", scanner);
                        if (listaEliminar > 3 || listaEliminar < 1)
                            System.out.println("ERROR, Ingrese una opcion validada del 0 al 3");
                    } while (listaEliminar > 3 || listaEliminar < 1);
                    questions.delete(questionSelector(listaEliminar), listaEliminar);
                    break;
                case 6:
                    questions.setTime(scanner);
                    break;
                case 7:
                    addQuestion(usuarioActual);
                    break;

                case 0:
                    System.out.println("Saliendo del menú...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }

        } while (option != 0);
        return -1;
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
            if (!created) throw new IOException();

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
                if (!created) throw new IOException();
                this.listaUsuarios = new ArrayList<Usuario>();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (FileReader r = new FileReader(destinyFolderFile + File.separator + "users.json")) {
                // Se recomienda usar BufferedReader para mejorar el rendimiento según un post en stack overflow
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
                if (!created) throw new IOException();
                this.questions = new Questions();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (FileReader reader = new FileReader(destinyFolderFile + File.separator + "data.json")) {
                // Se recomienda usar BufferedReader para mejorar el rendimiento según un post en stack overflow
                BufferedReader bufferedReader = new BufferedReader(reader);
                questions = gson.fromJson(bufferedReader, Questions.class);
            } catch (IOException e) {
                throw new RuntimeException("Error al leer el archivo JSON", e);
            }
        }
    }
}