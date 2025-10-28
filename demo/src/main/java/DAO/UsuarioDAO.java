package DAO;

import Model.Usuario;
import Model.TipoUsuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection conexao;

    public UsuarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public Connection getConexao() {
        return conexao;
    }

    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (username, senha, tipo) VALUES (?, ?, ?)";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, usuario.getUsername());
        stmt.setString(2, usuario.getSenha());
        stmt.setString(3, usuario.getTipo().toString());
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        Statement stmt = conexao.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Usuario u = new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("senha"),
                    TipoUsuario.fromString(rs.getString("tipo"))
            );
            lista.add(u);
        }
        rs.close();
        stmt.close();
        return lista;
    }

    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET username = ?, senha = ?, tipo = ? WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, usuario.getUsername());
        stmt.setString(2, usuario.getSenha());
        stmt.setString(3, usuario.getTipo().toString());
        stmt.setInt(4, usuario.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }

    public Usuario autenticar(String username, String senha) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE username = ? AND senha = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, senha);
        ResultSet rs = stmt.executeQuery();
        Usuario u = null;
        if (rs.next()) {
            u = new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("senha"),
                    TipoUsuario.fromString(rs.getString("tipo"))
            );
        }
        rs.close();
        stmt.close();
        return u;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        Usuario u = null;
        if (rs.next()) {
            u = new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("senha"),
                    TipoUsuario.fromString(rs.getString("tipo"))
            );
        }
        rs.close();
        stmt.close();
        return u;
    }

    public boolean existePorUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

}
