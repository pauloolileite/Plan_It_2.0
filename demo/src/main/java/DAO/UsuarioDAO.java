package DAO;

import Model.TipoUsuario; // Certifique-se que este import está correto
import Model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAO {
    private Connection conexao;
    private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());

    public UsuarioDAO(Connection conexao) {
        if (conexao == null) {
            // Log e exceção se conexão for nula
            logger.log(Level.SEVERE, "Conexão com o banco de dados não pode ser nula ao criar UsuarioDAO.");
            throw new IllegalArgumentException("Conexão com o banco de dados não pode ser nula.");
        }
        this.conexao = conexao;
    }

    public Connection getConexao() {
        return conexao;
    }

    // --- MÉTODOS CRUD ---

    public void inserir(Usuario usuario) throws SQLException {
        // Usa tabela 'usuario'
        String sql = "INSERT INTO usuario (username, senha, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getSenha()); // CUIDADO: Senha texto plano
            stmt.setString(3, usuario.getTipo().name()); // Usar .name() ou .toString()
            stmt.executeUpdate();
            logger.info("Usuário inserido: " + usuario.getUsername());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao inserir usuário: " + usuario.getUsername(), e);
            throw e;
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        // Usa tabela 'usuario'
        String sql = "SELECT id, username, senha, tipo FROM usuario";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // ... (código para criar objeto Usuario, verificar TipoUsuario) ...
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String senha = rs.getString("senha");
                TipoUsuario tipo = null;
                String tipoStr = rs.getString("tipo");
                try {
                    tipo = TipoUsuario.valueOf(tipoStr.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException e) {
                    logger.log(Level.WARNING, "Tipo de usuário inválido ('" + tipoStr + "') no BD para ID: " + id + ". Usando FUNCIONARIO.", e);
                    tipo = TipoUsuario.FUNCIONARIO; // Padrão seguro
                }
                Usuario u = new Usuario(id, username, senha, tipo);
                lista.add(u);
            }
            logger.info("Listagem de usuários concluída. Total: " + lista.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar todos os usuários", e);
            throw e;
        }
        return lista;
    }

    public void atualizar(Usuario usuario) throws SQLException {
        // *** CORREÇÃO APLICADA AQUI: Usa tabela 'usuario' ***
        String sql = "UPDATE usuario SET username = ?, senha = ?, tipo = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getSenha()); // CUIDADO
            stmt.setString(3, usuario.getTipo().name());
            stmt.setInt(4, usuario.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Usuário atualizado com sucesso ID: " + usuario.getId());
            } else {
                logger.warning("Nenhuma linha afetada ao atualizar usuário ID: " + usuario.getId());
            }
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Erro ao atualizar usuário ID: " + usuario.getId(), e);
            throw e;
        }
    }

    public void excluir(int id) throws SQLException {
        // *** CORREÇÃO APLICADA AQUI: Usa tabela 'usuario' ***
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Usuário excluído com sucesso ID: " + id);
            } else {
                logger.warning("Nenhuma linha afetada ao excluir usuário ID: " + id);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao excluir usuário ID: " + id, e);
            throw e;
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        // *** CORREÇÃO APLICADA AQUI: Usa tabela 'usuario' ***
        String sql = "SELECT id, username, senha, tipo FROM usuario WHERE id = ?";
        Usuario usuario = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // ... (código para criar objeto Usuario, verificar TipoUsuario) ...
                    int dbId = rs.getInt("id");
                    String username = rs.getString("username");
                    String senha = rs.getString("senha");
                    TipoUsuario tipo = null;
                    String tipoStr = rs.getString("tipo");
                    try {
                        tipo = TipoUsuario.valueOf(tipoStr.toUpperCase());
                    } catch (IllegalArgumentException | NullPointerException e) {
                        logger.log(Level.WARNING, "Tipo de usuário inválido ('" + tipoStr + "') no BD ao buscar ID: " + id, e);
                        tipo = TipoUsuario.FUNCIONARIO;
                    }
                    usuario = new Usuario(dbId, username, senha, tipo);
                    logger.info("Usuário encontrado por ID: " + id);
                } else {
                    logger.warning("Nenhum usuário encontrado com ID: " + id);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar usuário por ID: " + id, e);
            throw e;
        }
        return usuario;
    }


    // --- MÉTODOS DE AUTENTICAÇÃO E VERIFICAÇÃO ---

    public Usuario autenticar(String usernameInput, String senhaInput) throws SQLException {
        // Usa tabela 'usuario'
        String sql = "SELECT id, username, senha, tipo FROM usuario WHERE username = ? AND senha = ?";
        Usuario u = null;
        logger.info("Tentando autenticar usuário: " + usernameInput);
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usernameInput);
            stmt.setString(2, senhaInput); // Comparação insegura!

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // ... (código para criar objeto Usuario, verificar TipoUsuario) ...
                    int id = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    String dbSenha = rs.getString("senha");
                    TipoUsuario tipo = null;
                    String tipoStr = rs.getString("tipo");
                    try {
                        tipo = TipoUsuario.valueOf(tipoStr.toUpperCase());
                    } catch (IllegalArgumentException | NullPointerException e) {
                        logger.log(Level.WARNING, "Tipo inválido ('" + tipoStr + "') BD auth: " + usernameInput, e);
                        return null; // Falha se tipo inválido
                    }
                    u = new Usuario(id, dbUsername, dbSenha, tipo);
                    logger.info("Usuário autenticado: " + usernameInput);
                } else {
                    logger.warning("Falha auth (não encontrado): " + usernameInput);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao autenticar '" + usernameInput + "'", e);
            throw e;
        }
        return u;
    }

    public boolean existePorUsername(String username) throws SQLException {
        // Usa tabela 'usuario'
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean existe = rs.getInt(1) > 0;
                    logger.fine("Verificação username '" + username + "': " + existe);
                    return existe;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao verificar username: " + username, e);
            throw e;
        }
        return false;
    }

    public boolean existeOutroUsuarioComUsername(String username, int idAtual) throws SQLException {
        // Usa tabela 'usuario'
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ? AND id != ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, idAtual);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean existeOutro = rs.getInt(1) > 0;
                    if(existeOutro) logger.warning("Duplicidade username: Encontrado OUTRO com '"+username+"' (excluindo ID "+idAtual+")");
                    return existeOutro;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao verificar duplicidade username: " + username + " (excl. ID: " + idAtual + ")", e);
            throw e;
        }
        return false;
    }

    public void setConexao(Connection conexao) {
        this.conexao = conexao;
    }
}