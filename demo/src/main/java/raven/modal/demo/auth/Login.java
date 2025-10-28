package raven.modal.demo.auth;

import Controller.UsuarioController; // Importar seu Controller
import Model.TipoUsuario; // Importar seu Enum
import Model.Usuario; // Importar seu Model
import Utils.DatabaseConnection; // Importar sua classe de conexão
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.component.DropShadowBorder;
import raven.modal.demo.component.LabelButton;
import raven.modal.demo.menu.MyDrawerBuilder;
import raven.modal.demo.model.ModelUser;
import raven.modal.demo.system.Form;
import raven.modal.demo.system.FormManager;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection; // Importar Connection
import java.sql.SQLException; // Importar SQLException

public class Login extends Form {

    // 1. Declarar o UsuarioController como variável da classe
    private UsuarioController usuarioController;

    public Login() {
        init();
    }

    private void init() {
        // 2. Inicializar o Controller (assumindo que DatabaseConnection.getConexao() funciona)
        try {
            Connection conexao = DatabaseConnection.getConnection();
            this.usuarioController = new UsuarioController(conexao);
        } catch (Exception e) {
            // Em caso de falha na conexão, exibe o erro e impede o login
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            // Poderia desabilitar os campos ou o botão aqui
        }

        setLayout(new MigLayout("al center center"));
        createLogin();
    }

    private void createLogin() {
        JPanel panelLogin = new JPanel(new BorderLayout()) {
            @Override
            public void updateUI() {
                super.updateUI();
                applyShadowBorder(this);
            }
        };
        panelLogin.setOpaque(false);
        applyShadowBorder(panelLogin);

        JPanel loginContent = new JPanel(new MigLayout("fillx,wrap,insets 35 35 25 35", "[fill,300]"));

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel lbDescription = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +12;");

        loginContent.add(lbTitle);
        loginContent.add(lbDescription);

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JCheckBox chRememberMe = new JCheckBox("Remember Me");
        JButton cmdLogin = new JButton("Login") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };

        // style
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        panelLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "[dark]background:tint($Panel.background,1%);");

        loginContent.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");

        txtUsername.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;" +
                "showRevealButton:true;");

        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;");

        loginContent.add(new JLabel("Username"), "gapy 25");
        loginContent.add(txtUsername);

        loginContent.add(new JLabel("Password"), "gapy 10");
        loginContent.add(txtPassword);
        loginContent.add(chRememberMe);
        loginContent.add(cmdLogin, "gapy 20");
        loginContent.add(createInfo());

        panelLogin.add(loginContent);
        add(panelLogin);

        // --- 3. LÓGICA DE LOGIN MODIFICADA ---
        cmdLogin.addActionListener(e -> {
            // Verifica se o controller foi inicializado
            if (this.usuarioController == null) {
                JOptionPane.showMessageDialog(Login.this, "Controlador de usuário não inicializado. Verifique a conexão com o banco.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userName = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());

            try {
                // Chama o backend real para autenticar
                Usuario usuarioAutenticado = usuarioController.autenticarUsuario(userName, password);

                // Se chegou aqui, o login foi bem-sucedido

                // 4. Adaptar Model.Usuario (Backend) para ModelUser (Frontend)
                // O ModelUser da interface espera (nome, email, role)
                // O Model.Usuario do backend tem (username, tipo)
                // Vamos adaptar:

                // Mapeia o TipoUsuario (backend) para ModelUser.Role (frontend)
                ModelUser.Role role;
                if (usuarioAutenticado.getTipo() == TipoUsuario.ADMINISTRADOR) {
                    role = ModelUser.Role.ADMIN;
                } else {
                    role = ModelUser.Role.STAFF; // Assume STAFF para FUNCIONARIO ou outros
                }

                // ATENÇÃO: Seu Model.Usuario não tem 'email'. Usaremos um placeholder.
                // O ideal seria adicionar 'nome' e 'email' ao Model.Usuario no backend.
                String nomeUsuario = usuarioAutenticado.getUsername(); // Usando username como nome
                String emailUsuario = "email@placeholder.com"; // Email não existe no backend

                ModelUser userParaUI = new ModelUser(nomeUsuario, emailUsuario, role);

                // Continua o fluxo da interface
                MyDrawerBuilder.getInstance().setUser(userParaUI);
                FormManager.login();

            } catch (SQLException | IllegalArgumentException ex) {
                // Captura erros de "Usuário ou senha inválidos" ou erros de SQL
                JOptionPane.showMessageDialog(Login.this, ex.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
                // Limpa a senha por segurança
                txtPassword.setText("");
            }
        });
    }

    private JPanel createInfo() {
        JPanel panelInfo = new JPanel(new MigLayout("wrap,al center", "[center]"));
        panelInfo.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");

        panelInfo.add(new JLabel("Don't remember your account details?"));
        panelInfo.add(new JLabel("Contact us at"), "split 2");
        LabelButton lbLink = new LabelButton("help@info.com");

        panelInfo.add(lbLink);

        // event
        lbLink.addOnClick(e -> {
            // Ação futura
        });
        return panelInfo;
    }

    private void applyShadowBorder(JPanel panel) {
        if (panel != null) {
            panel.setBorder(new DropShadowBorder(new Insets(5, 8, 12, 8), 1, 25));
        }
    }

    // 5. O MÉTODO DE TESTE (getUser) FOI REMOVIDO.
    // private ModelUser getUser(String user, String password) { ... }
}