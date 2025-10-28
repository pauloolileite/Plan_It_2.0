package raven.modal.demo.auth;

// Removidos imports do Controller, Model e Utils que não serão usados temporariamente
// import Controller.UsuarioController;
// import Model.TipoUsuario;
// import Model.Usuario;
// import Utils.DatabaseConnection;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.component.DropShadowBorder;
import raven.modal.demo.component.LabelButton;
import raven.modal.demo.menu.MyDrawerBuilder;
import raven.modal.demo.model.ModelUser; // ModelUser ainda é necessário para a UI
import raven.modal.demo.system.Form;
import raven.modal.demo.system.FormManager;

import javax.swing.*;
import java.awt.*;
// Removidos imports de SQL que não serão usados temporariamente
// import java.sql.Connection;
// import java.sql.SQLException;

public class Login extends Form {

    // Comentado o UsuarioController
    // private UsuarioController usuarioController;

    public Login() {
        init();
    }

    private void init() {
        // Comentada a inicialização do Controller e a conexão com o banco
        /*
        try {
            Connection conexao = DatabaseConnection.getConnection();
            if (conexao == null) {
                throw new SQLException("Falha ao obter conexão com o banco de dados.");
            }
            this.usuarioController = new UsuarioController(conexao);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado na inicialização: " + e.getMessage(), "Erro Geral", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        */

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

        // --- LÓGICA DE LOGIN TEMPORÁRIA (SEM AUTENTICAÇÃO) ---
        cmdLogin.addActionListener(e -> {
            System.out.println("Autenticação pulada para desenvolvimento."); // Mensagem no console

            // Cria um usuário FALSO (ADMIN) para permitir acesso
            // (Você pode mudar para STAFF se quiser testar permissões de funcionário)
            ModelUser userFalso = new ModelUser("Usuário Teste", "teste@dev.com", ModelUser.Role.ADMIN);

            // Continua o fluxo da interface com o usuário falso
            MyDrawerBuilder.getInstance().setUser(userFalso);
            FormManager.login();

            // O código original de autenticação foi comentado abaixo:
            /*
            if (this.usuarioController == null) {
                JOptionPane.showMessageDialog(Login.this, "Controlador de usuário não inicializado. Verifique a conexão com o banco.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String userName = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());

            try {
                Usuario usuarioAutenticado = usuarioController.autenticarUsuario(userName, password);

                ModelUser.Role role;
                if (usuarioAutenticado.getTipo() == TipoUsuario.ADMINISTRADOR) {
                    role = ModelUser.Role.ADMIN;
                } else {
                    role = ModelUser.Role.STAFF;
                }

                String nomeUsuario = usuarioAutenticado.getUsername();
                String emailUsuario = "email@placeholder.com";

                ModelUser userParaUI = new ModelUser(nomeUsuario, emailUsuario, role);

                MyDrawerBuilder.getInstance().setUser(userParaUI);
                FormManager.login();

            } catch (SQLException ex) {
                 JOptionPane.showMessageDialog(Login.this, "Erro no banco de dados: " + ex.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
                 txtPassword.setText("");
            } catch (IllegalArgumentException ex) {
                 JOptionPane.showMessageDialog(Login.this, ex.getMessage(), "Erro de Login", JOptionPane.WARNING_MESSAGE);
                 txtPassword.setText("");
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(Login.this, "Ocorreu um erro inesperado: " + ex.getMessage(), "Erro Desconhecido", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace();
                 txtPassword.setText("");
             }
             */
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
            JOptionPane.showMessageDialog(Login.this, "Funcionalidade de recuperação de conta ainda não implementada.");
        });
        return panelInfo;
    }

    private void applyShadowBorder(JPanel panel) {
        if (panel != null) {
            panel.setBorder(new DropShadowBorder(new Insets(5, 8, 12, 8), 1, 25));
        }
    }
}