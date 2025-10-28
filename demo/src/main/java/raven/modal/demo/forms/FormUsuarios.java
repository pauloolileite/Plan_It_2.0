package raven.modal.demo.forms; // Ou o pacote que você escolheu

import Controller.UsuarioController; // Importa o Controller
import Model.Usuario; // Importa o Model
import Model.TipoUsuario; // Importa o Enum
import Utils.DatabaseConnection; // Importa a classe de conexão
import com.formdev.flatlaf.FlatClientProperties;
import raven.modal.demo.system.Form;
import raven.modal.ModalDialog; // Para futuros diálogos de cadastro/edição
import raven.modal.Toast; // Para mensagens de feedback

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FormUsuarios extends Form {

    private JTable table;
    private DefaultTableModel tableModel;
    private UsuarioController usuarioController; // Controller para a lógica de negócio
    private JButton cmdAdicionar;
    private JButton cmdEditar;
    private JButton cmdExcluir;

    public FormUsuarios() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        // --- Inicialização do Controller (COMENTADO POR ENQUANTO) ---
        /*
        try {
            Connection conexao = DatabaseConnection.getConnection();
            if (conexao == null) {
                throw new SQLException("Falha ao obter conexão com o banco de dados.");
            }
            this.usuarioController = new UsuarioController(conexao);
        } catch (SQLException e) {
            showDatabaseConnectionError(e.getMessage());
        } catch (Exception e) {
            showUnexpectedError(e.getMessage());
            e.printStackTrace();
        }
        */

        // Cria o painel principal com padding
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(panel, BorderLayout.CENTER);

        // Cria o painel de botões
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cmdAdicionar = new JButton("Adicionar Novo");
        cmdEditar = new JButton("Editar Selecionado");
        cmdExcluir = new JButton("Excluir Selecionado");

        // Estilo moderno para botões
        cmdAdicionar.putClientProperty(FlatClientProperties.STYLE, "[light]background:tint(@background,50%);[dark]background:tint(@background,50%);borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:10;");
        cmdEditar.putClientProperty(FlatClientProperties.STYLE, "borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:10;");
        cmdExcluir.putClientProperty(FlatClientProperties.STYLE, "[light]background:tint(#f44336, 90%);foreground:tint(#f44336, 10%);[dark]background:tint(#f44336, 30%);foreground:tint(#f44336, 90%);borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:10;");

        commandPanel.add(cmdAdicionar);
        commandPanel.add(cmdEditar);
        commandPanel.add(cmdExcluir);

        // Adiciona painel de botões no topo
        panel.add(commandPanel, BorderLayout.NORTH);

        // Cria a tabela
        tableModel = new DefaultTableModel(
                new Object[][]{}, // Dados iniciais vazios
                // Colunas baseadas no Model.Usuario (NÃO exibir senha!)
                new String[]{"ID", "Username", "Tipo"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição direta
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Adiciona a tabela com barra de rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Carrega os dados na tabela
        loadUsuarios();

        // --- Ações dos Botões (Placeholder) ---
        cmdAdicionar.addActionListener(e -> adicionarNovoUsuario());
        cmdEditar.addActionListener(e -> editarUsuarioSelecionado());
        cmdExcluir.addActionListener(e -> excluirUsuarioSelecionado());

        // Desabilita botões de edição/exclusão inicialmente
        cmdEditar.setEnabled(false);
        cmdExcluir.setEnabled(false);

        // Habilita/desabilita botões Editar/Excluir ao selecionar linha
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = table.getSelectedRow() != -1;
            cmdEditar.setEnabled(rowSelected);
            cmdExcluir.setEnabled(rowSelected);
        });
    }

    // --- Métodos de Ação ---

    private void loadUsuarios() {
        tableModel.setRowCount(0);

        // --- LÓGICA REAL (COMENTADA POR ENQUANTO) ---
        /*
        if (usuarioController != null) {
            try {
                List<Usuario> usuarios = usuarioController.listarUsuarios();
                if (usuarios != null) {
                    for (Usuario user : usuarios) {
                        tableModel.addRow(new Object[]{
                                user.getId(),
                                user.getUsername(),
                                user.getTipo() // Mostra o Enum diretamente (ou user.getTipo().name())
                        });
                    }
                }
            } catch (SQLException e) {
                showDatabaseOperationError("Erro ao carregar usuários: " + e.getMessage());
            } catch (Exception e) {
                showUnexpectedError("Erro inesperado ao carregar usuários: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
             if (isVisible()) {
                showControllerNotInitializedWarning();
             }
            addSampleData();
        }
        */

        // --- LÓGICA TEMPORÁRIA COM DADOS DE EXEMPLO ---
        System.out.println("Usando dados de exemplo para usuários.");
        addSampleData();
        if (usuarioController == null && isVisible()) {
            showControllerNotInitializedWarning();
        }
        // --- FIM DA LÓGICA TEMPORÁRIA ---
    }


    private void adicionarNovoUsuario() {
        // TODO: Abrir um ModalDialog com um JPanelCadastroUsuario (campos: username, password, confirm password, tipo JComboBox)
        System.out.println("Ação: Adicionar Novo Usuário");
        // Exemplo:
        /*
        JPanelCadastroUsuario panelCadastro = new JPanelCadastroUsuario();
        boolean ok = ModalDialog.showModal(this, panelCadastro, "Cadastrar Novo Usuário");
        if (ok) {
            Usuario novoUsuario = panelCadastro.getUsuario(); // Método para pegar o usuário do painel
            if (usuarioController != null) {
                try {
                    // !!! IMPORTANTE: Implementar HASH de senha antes de chamar o controller !!!
                    // String senhaComHash = hashSenha(novoUsuario.getSenha());
                    // novoUsuario.setSenha(senhaComHash);

                    usuarioController.cadastrarUsuario(novoUsuario);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Usuário cadastrado com sucesso!");
                    loadUsuarios(); // Recarrega
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao cadastrar usuário: " + ex.getMessage());
                } catch (Exception ex) { // Capturar erro de Hashing também
                    showUnexpectedError("Erro inesperado ao cadastrar: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showControllerNotInitializedWarning();
            }
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Adicionar Usuário ainda não implementada.");
    }

    private void editarUsuarioSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um usuário para editar.");
            return;
        }

        int usuarioId = (int) tableModel.getValueAt(selectedRow, 0);
        System.out.println("Ação: Editar Usuário ID: " + usuarioId);

        // TODO: Buscar o usuário completo pelo ID (usuarioController.buscarUsuarioPorId)
        // TODO: Criar e preencher um JPanelCadastroUsuario (sem mostrar senha atual, talvez campos para nova senha opcional)
        // TODO: Abrir o ModalDialog com o painel preenchido
        // TODO: No "Salvar", chamar usuarioController.atualizarUsuario (lembrar de aplicar HASH se a senha foi alterada)
        // TODO: Recarregar a tabela

        // Exemplo:
        /*
        if (usuarioController != null) {
            try {
                Usuario userParaEditar = usuarioController.buscarUsuarioPorId(usuarioId);
                if (userParaEditar != null) {
                    JPanelCadastroUsuario panelEdicao = new JPanelCadastroUsuario();
                    panelEdicao.setUsuarioParaEdicao(userParaEditar); // Método para preencher (sem senha)

                    boolean ok = ModalDialog.showModal(this, panelEdicao, "Editar Usuário");
                    if (ok) {
                        Usuario userEditado = panelEdicao.getUsuarioEditado(userParaEditar.getId()); // Pega dados atualizados

                        // !!! IMPORTANTE: Verificar se a senha foi alterada e aplicar HASH !!!
                        // if (panelEdicao.senhaFoiAlterada()) {
                        //     String novaSenhaComHash = hashSenha(userEditado.getSenha());
                        //     userEditado.setSenha(novaSenhaComHash);
                        // } else {
                        //     userEditado.setSenha(null); // Ou não setar para não atualizar
                        // }

                        usuarioController.atualizarUsuario(userEditado);
                        Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Usuário atualizado com sucesso!");
                        loadUsuarios();
                    }
                } else {
                     Toast.show(this.getRootPane(), Toast.Type.ERROR, "Usuário não encontrado para edição.");
                }
            } catch (SQLException | IllegalArgumentException ex) {
                showDatabaseOperationError("Erro ao buscar ou atualizar usuário: " + ex.getMessage());
            } catch (Exception ex) {
                 showUnexpectedError("Erro inesperado ao editar usuário: " + ex.getMessage());
                 ex.printStackTrace();
            }
        } else {
            showControllerNotInitializedWarning();
        }
        */
        Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Editar Usuário ainda não implementada.");
    }

    private void excluirUsuarioSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Toast.show(this.getRootPane(), Toast.Type.WARNING, "Selecione um usuário para excluir.");
            return;
        }

        int usuarioId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        System.out.println("Ação: Excluir Usuário ID: " + usuarioId);

        // TODO: Adicionar verificação de segurança (não excluir a si mesmo, não excluir último admin)

        // Mostra confirmação
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o usuário '" + username + "' (ID: " + usuarioId + ")?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Chamar usuarioController.excluirUsuario(usuarioId)
            // TODO: Recarregar a tabela

            // Exemplo:
             /*
             if (usuarioController != null) {
                try {
                    // Adicionar verificações de segurança ANTES de excluir
                    // Usuario usuarioLogado = ... // Obter usuário logado
                    // if (usuarioId == usuarioLogado.getId()) throw new IllegalArgumentException("Não pode excluir a si mesmo.");
                    // if (usuarioController.isUltimoAdmin(usuarioId)) throw new IllegalArgumentException("Não pode excluir o último administrador.");

                    usuarioController.excluirUsuario(usuarioId);
                    Toast.show(this.getRootPane(), Toast.Type.SUCCESS, "Usuário excluído com sucesso!");
                    loadUsuarios();
                } catch (SQLException | IllegalArgumentException ex) {
                    showDatabaseOperationError("Erro ao excluir usuário: " + ex.getMessage());
                } catch (Exception ex) {
                    showUnexpectedError("Erro inesperado ao excluir usuário: " + ex.getMessage());
                    ex.printStackTrace();
                }
             } else {
                showControllerNotInitializedWarning();
             }
             */
            Toast.show(this.getRootPane(), Toast.Type.INFO, "Funcionalidade Excluir Usuário (lógica do backend) ainda não implementada.");
        }
    }

    // --- Métodos Auxiliares de Feedback (Iguais aos outros Forms) ---
    // (showDatabaseConnectionError, showDatabaseOperationError, showUnexpectedError, showControllerNotInitializedWarning)
    // ... (copiar métodos de outro Form) ...

    private void showDatabaseConnectionError(String message) { // Copiado
        JOptionPane.showMessageDialog(this, "Não foi possível conectar ao banco de dados.\n" + message + "\nVerifique as configurações e o servidor.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        cmdAdicionar.setEnabled(false);
        cmdEditar.setEnabled(false);
        cmdExcluir.setEnabled(false);
    }
    private void showDatabaseOperationError(String message) { // Copiado
        Toast.show(this.getRootPane(), Toast.Type.ERROR, message);
    }
    private void showUnexpectedError(String message) { // Copiado
        JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado:\n" + message, "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
    }
    private void showControllerNotInitializedWarning() { // Copiado
        Toast.show(this.getRootPane(), Toast.Type.WARNING, "Operação não disponível (sem conexão com banco). Usando dados de exemplo.");
    }


    // --- Método para Dados de Exemplo (Temporário) ---
    private void addSampleData() {
        tableModel.addRow(new Object[]{1, "admin", TipoUsuario.ADMINISTRADOR});
        tableModel.addRow(new Object[]{2, "paulo", TipoUsuario.FUNCIONARIO});
        tableModel.addRow(new Object[]{3, "rodrigo", TipoUsuario.FUNCIONARIO});
    }
}