package Controller;

import DAO.UsuarioDAO;
import Model.Usuario;
import Model.TipoUsuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UsuarioController {
    private final UsuarioDAO usuarioDAO;

    public UsuarioController(Connection conexao) {
        this.usuarioDAO = new UsuarioDAO(conexao);
    }


    public void cadastrarUsuario(String username, String senha, TipoUsuario tipo) throws SQLException {
        validarCampos(username, senha, tipo);
        verificarDuplicidade(username);

        Usuario u = new Usuario(0, username, senha, tipo);
        usuarioDAO.inserir(u);
    }

    private void validarCampos(String username, String senha, TipoUsuario tipo) {
        if (username == null || username.isBlank() ||
                senha == null || senha.isBlank() ||
                tipo == null) {
            throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
        }
    }

    private void verificarDuplicidade(String username) throws SQLException {
        if (usuarioDAO.existePorUsername(username)) {
            throw new IllegalArgumentException("Nome de usuário já está em uso.");
        }
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public void atualizarUsuario(Usuario usuario) throws SQLException {
        usuarioDAO.atualizar(usuario);
    }

    public void excluirUsuario(int id) throws SQLException {
        Usuario u = usuarioDAO.buscarPorId(id);
        if (u != null && u.getTipo() == TipoUsuario.ADMINISTRADOR && "admin".equalsIgnoreCase(u.getUsername())) {
            throw new IllegalArgumentException("O usuário administrador principal não pode ser excluído.");
        }
        usuarioDAO.excluir(id);
    }

    public Usuario autenticarUsuario(String username, String senha) throws Exception {
        validarAutenticacao(username, senha);

        Usuario usuario = usuarioDAO.autenticar(username, senha);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário ou senha inválidos.");
        }

        return usuario;
    }
    public Connection getConexao() {
        return usuarioDAO.getConexao();
    }

    private void validarAutenticacao(String username, String senha) {
        if (username == null || username.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }
    }

    public static boolean isAdmin(Usuario usuario) {
        return usuario != null && usuario.getTipo() == TipoUsuario.ADMINISTRADOR;
    }
}
