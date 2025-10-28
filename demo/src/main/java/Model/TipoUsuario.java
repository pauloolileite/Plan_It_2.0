package Model;

public enum TipoUsuario {
    ADMINISTRADOR("Administrador"),
    FUNCIONARIO("Funcionário");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoUsuario fromString(String texto) {
        if (texto != null) {
            for (TipoUsuario t : values()) {
                if (t.descricao.equalsIgnoreCase(texto)) {
                    return t;
                }
            }
        }
        throw new IllegalArgumentException("Tipo de usuario invalido: " + texto);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
