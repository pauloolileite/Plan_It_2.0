package Controller;

import DAO.UsuarioDAO;
import Model.Usuario;
import Model.TipoUsuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level; // Import Logger
import java.util.logging.Logger; // Import Logger

public class UsuarioController {
    private final UsuarioDAO usuarioDAO;
    // Adiciona um logger
    private static final Logger logger = Logger.getLogger(UsuarioController.class.getName());

    public UsuarioController(Connection conexao) {
        this.usuarioDAO = new UsuarioDAO(conexao);
    }

    public void cadastrarUsuario(Usuario usuario) throws SQLException, IllegalArgumentException { // Garante que ambas exceções são declaradas
        logger.info("Tentando cadastrar usuário: " + (usuario != null ? usuario.getUsername() : "null"));
        if (usuario == null) {
            throw new IllegalArgumentException("Dados do usuário inválidos.");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome de usuário não pode estar vazio.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode estar vazia.");
        }
        if (usuario.getTipo() == null) {
            throw new IllegalArgumentException("O tipo de usuário deve ser selecionado.");
        }

        // TODO: Implementar Hashing de Senha AQUI antes de verificar/inserir

        // --- CORREÇÃO: Chamada direta, sem try-catch desnecessário ---
        // Verifica se o username já existe (lança SQLException ou IllegalArgumentException)
        verificarDuplicidade(usuario.getUsername());
        // --- FIM DA CORREÇÃO ---

        // Se não lançou exceção, insere
        usuarioDAO.inserir(usuario);
        logger.info("Usuário cadastrado com sucesso: " + usuario.getUsername());
    }

    // Método 'validarCampos' não parece estar sendo usado. Pode remover.
    // private void validarCampos(...) { ... }

    // Método verificarDuplicidade permanece o mesmo (declarando throws SQLException)
    private void verificarDuplicidade(String username) throws SQLException, IllegalArgumentException {
        logger.fine("Verificando duplicidade para username: " + username);
        if (usuarioDAO.existePorUsername(username)) {
            logger.warning("Username '" + username + "' já existe.");
            throw new IllegalArgumentException("Nome de usuário já está em uso.");
        }
        logger.fine("Username '" + username + "' disponível.");
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        logger.info("Listando todos os usuários...");
        return usuarioDAO.listarTodos();
    }

    public void atualizarUsuario(Usuario usuario) throws SQLException, IllegalArgumentException {
        logger.info("Tentando atualizar usuário ID: " + (usuario != null ? usuario.getId() : "null"));
        if (usuario == null || usuario.getId() <= 0) { // Verifica ID também
            throw new IllegalArgumentException("Dados do usuário inválidos para atualização.");
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome de usuário não pode estar vazio.");
        }
        // Não validamos senha vazia aqui, pois pode não ter sido alterada
        if (usuario.getTipo() == null) {
            throw new IllegalArgumentException("O tipo de usuário deve ser selecionado.");
        }

        // TODO: Validação de duplicidade ANTES de atualizar, se username mudou
        // try {
        //    Usuario original = usuarioDAO.buscarPorId(usuario.getId());
        //    if (!original.getUsername().equals(usuario.getUsername())) {
        //        verificarDuplicidade(usuario.getUsername()); // Verifica novo username
        //    }
        // } catch (SQLException e) { ... }

        // TODO: Implementar Hashing de Senha AQUI se a senha foi alterada

        usuarioDAO.atualizar(usuario);
        logger.info("Usuário ID " + usuario.getId() + " atualizado.");
    }

    public void excluirUsuario(int id) throws SQLException, IllegalArgumentException {
        logger.info("Tentando excluir usuário ID: " + id);
        if (id <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido para exclusão.");
        }
        // TODO: Verificações de segurança (self-delete, last admin)
        usuarioDAO.excluir(id);
        logger.info("Usuário ID " + id + " excluído (se existia).");
    }

    // AutenticarUsuario não precisa de try-catch aqui se DAO já trata e relança
    public Usuario autenticarUsuario(String username, String senha) throws SQLException, IllegalArgumentException {
        logger.info("Tentando autenticar usuário: " + username);
        validarAutenticacao(username, senha); // Valida inputs

        // DAO.autenticar agora lança SQLException diretamente se houver erro SQL
        Usuario usuario = usuarioDAO.autenticar(username, senha);

        if (usuario == null) {
            logger.warning("Falha na autenticação para: " + username);
            throw new IllegalArgumentException("Usuário ou senha inválidos."); // Lança se não encontrado
        }
        logger.info("Autenticação bem-sucedida para: " + username);
        return usuario;
    }

    // Este método é necessário e está correto
    public Connection getConexao() {
        //logger.fine("Retornando conexão do DAO"); // Log opcional
        return usuarioDAO.getConexao();
    }

    private void validarAutenticacao(String username, String senha) throws IllegalArgumentException {
        if (username == null || username.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }
    }

    public static boolean isAdmin(Usuario usuario) {
        return usuario != null && usuario.getTipo() != null && usuario.getTipo() == TipoUsuario.ADMINISTRADOR;
    }

    public Usuario buscarUsuarioPorId(int id) throws SQLException {
        logger.info("Buscando usuário por ID: " + id);
        return usuarioDAO.buscarPorId(id);
    }
}