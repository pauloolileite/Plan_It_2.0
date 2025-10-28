package Model;

public class Usuario {
    private int id;
    private String username;
    private String senha;
    private TipoUsuario tipo;

    public Usuario(int id, String username, String senha, TipoUsuario tipo) {
        this.id = id;
        this.username = username;
        this.senha = senha;
        this.tipo = tipo;
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getSenha() {
        return senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return username + " (" + tipo + ")";
    }
}
