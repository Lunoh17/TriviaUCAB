package triviaucab.tui.models;
/**
 * Clase que representa un usuario del sistema de trivia.
 * Contiene el nombre de usuario y la contraseña (almacenada como hash SHA-256).
 */
public class Usuario {
    /**
     * Nombre de usuario del usuario.
     */
    public String userName;

    /**
     * Contraseña del usuario, almacenada como hash SHA-256.
     */
    private String password;

    private int victory;

    public int getVictory() {
        return this.victory;
    }
    public void setVictory(int victory) {
        this.victory= victory;
    }

    /**
     * Constructor de la clase Usuario.
     *
     * @param userName userName El nombre de usuario.
     * @param password password La contraseña en texto plano, que será convertida a SHA-256.
     */
    public Usuario(String userName, String password) {
        this.userName = userName;
        this.password = Validator.calcularSha256(password);
    }

    private int victories;

    /**
     * Obtiene la contraseña del usuario (almacenada como hash SHA-256).
     *
     * @return La contraseña hasheada.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Obtiene el nombre de usuario.
     *
     * @return El nombre de usuario.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Establece un nuevo nombre de usuario.
     *
     * @param userName El nuevo nombre de usuario.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Establece una nueva contraseña. La contraseña se guarda como hash SHA-256.
     *
     * @param password La nueva contraseña en texto plano.
     */
    public void setPassword(String password) {
        this.password = Validator.calcularSha256(password);
    }

}

