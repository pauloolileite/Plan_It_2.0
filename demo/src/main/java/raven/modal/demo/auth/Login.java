package raven.modal.demo.auth;

import Controller.UsuarioController; // Importar seu Controller
import Model.TipoUsuario; // Importar seu Enum
import Model.Usuario; // Importar seu Model
import Utils.DatabaseConnection; // Importar sua classe de conexão
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast; // Import correto do Toast
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

    private UsuarioController usuarioController;
    private boolean databaseConnectionFailed = false; // Flag para erro de conexão

    public Login() {
        init();
    }

    private void init() {
        try {
            Connection conexao = DatabaseConnection.getConnection(); // Usar o método estático correto
            if (conexao == null) {
                throw new SQLException("Falha ao obter conexão com o banco de dados (retornou null).");
            }
            this.usuarioController = new UsuarioController(conexao);
            System.out.println("Conexão com banco de dados estabelecida para Login."); // Log de sucesso
        } catch (SQLException e) {
            databaseConnectionFailed = true; // Marca que a conexão falhou
            showDatabaseConnectionError("Erro SQL: " + e.getMessage()); // Exibe erro detalhado
            e.printStackTrace(); // Loga o stack trace completo no console
        } catch (Exception e) {
            databaseConnectionFailed = true; // Marca que a conexão falhou
            showUnexpectedError("Erro Inesperado: " + e.getMessage()); // Exibe erro detalhado
            e.printStackTrace(); // Loga o stack trace completo no console
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
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +12;");

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

        // Estilos...
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        panelLogin.putClientProperty(FlatClientProperties.STYLE, "[dark]background:tint($Panel.background,1%);");
        loginContent.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        txtUsername.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;arc:12;");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;arc:12;showRevealButton:true;");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "margin:4,10,4,10;arc:12;");

        loginContent.add(new JLabel("Username"), "gapy 25");
        loginContent.add(txtUsername);
        loginContent.add(new JLabel("Password"), "gapy 10");
        loginContent.add(txtPassword);
        loginContent.add(chRememberMe);
        loginContent.add(cmdLogin, "gapy 20");
        loginContent.add(createInfo());

        panelLogin.add(loginContent);
        add(panelLogin);

        // Ação do botão Login
        cmdLogin.addActionListener(e -> {
            if (databaseConnectionFailed || this.usuarioController == null) {
                showControllerNotInitializedWarning();
                return;
            }

            String userName = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());

            try {
                Usuario usuarioAutenticado = usuarioController.autenticarUsuario(userName, password);

                ModelUser.Role role = (usuarioAutenticado.getTipo() == TipoUsuario.ADMINISTRADOR) ? ModelUser.Role.ADMIN : ModelUser.Role.STAFF;
                String nomeUsuario = usuarioAutenticado.getUsername();
                String emailUsuario = "email@placeholder.com"; // Placeholder

                ModelUser userParaUI = new ModelUser(nomeUsuario, emailUsuario, role);

                MyDrawerBuilder.getInstance().setUser(userParaUI);
                FormManager.login();

            } catch (SQLException ex) {
                showDatabaseOperationError("Erro no banco ao autenticar: " + ex.getMessage());
                ex.printStackTrace();
                txtPassword.setText("");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(Login.this, ex.getMessage(), "Erro de Login", JOptionPane.WARNING_MESSAGE);
                txtPassword.setText("");
            } catch (Exception ex) {
                showUnexpectedError("Erro inesperado ao autenticar: " + ex.getMessage());
                ex.printStackTrace();
                txtPassword.setText("");
            }
        });

        // Desabilita campos se conexão falhou na inicialização
        if (databaseConnectionFailed) {
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
            cmdLogin.setEnabled(false);
        }
    }

    private JPanel createInfo() {
        JPanel panelInfo = new JPanel(new MigLayout("wrap,al center", "[center]"));
        panelInfo.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        panelInfo.add(new JLabel("Don't remember your account details?"));
        panelInfo.add(new JLabel("Contact us at"), "split 2");
        LabelButton lbLink = new LabelButton("help@info.com");
        panelInfo.add(lbLink);
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

    // --- Métodos de Feedback ---
    private void showDatabaseConnectionError(String message) {
        JOptionPane.showMessageDialog(this,
                "Não foi possível conectar ao banco de dados na inicialização.\n" + message +
                        "\nVerifique se o servidor MySQL está rodando e as configurações (db.properties) estão corretas.",
                "Erro Crítico de Conexão", JOptionPane.ERROR_MESSAGE);
    }
    private void showDatabaseOperationError(String message) {
        if(getRootPane() != null) Toast.show(this.getRootPane(), Toast.Type.ERROR, message);
        else System.err.println("Erro DB: " + message);
    }
    private void showUnexpectedError(String message) {
        JOptionPane.showMessageDialog(this,
                "Ocorreu um erro inesperado:\n" + message,
                "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
    }
    private void showControllerNotInitializedWarning() {
        JOptionPane.showMessageDialog(this,
                "A conexão com o banco de dados falhou na inicialização.\nNão é possível realizar o login.",
                "Falha na Conexão", JOptionPane.WARNING_MESSAGE);
    }
}