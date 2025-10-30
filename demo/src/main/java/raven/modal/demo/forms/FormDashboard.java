package raven.modal.demo.forms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.component.dashboard.CardBox;
import raven.modal.demo.system.Form;
import raven.modal.demo.utils.SystemForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

// === Seus Controllers/Utils/Models ===
import Utils.DatabaseConnection;
import Utils.DataUtils;

import Controller.AgendamentoController;
import Controller.ClienteController;

import Model.Agendamento;

@SystemForm(name = "Dashboard", description = "dashboard form display some details")
public class FormDashboard extends Form {

    // ====== Infra do dashboard (mesma linha do seu layout) ======
    private JPanel panelLayout;      // container com DashboardLayout
    private CardBox cardBox;         // topo com 3 cards

    // ====== Painéis centrais ======
    private JPanel panelMiddle;      // container com 2 colunas (AgendaDia | Proximos)
    private JPanel panelAgendaDia;
    private JPanel panelProximos;

    // ====== Painel base ======
    private JPanel panelServicosRecentes;

    // ====== Tabelas ======
    private JTable tableAgendaDia;
    private DefaultTableModel modelAgendaDia;

    private JTable tableProximos;
    private DefaultTableModel modelProximos;

    private JTable tableServicos;
    private DefaultTableModel modelServicos;

    // ====== Controllers ======
    private AgendamentoController agendamentoController;
    private ClienteController clienteController;

    public FormDashboard() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fill", "[fill]", "[grow 0][fill]"));
        createTitle();
        createPanelLayout();
        createTopCards();         // TOPO (3 cards)
        createMiddlePanels();     // MEIO (Agenda do Dia | Próximos Agendamentos)
        createBottomServices();   // BASE (Serviços Recentes)
    }

    @Override
    public void formInit() {
        loadData();
    }

    @Override
    public void formRefresh() {
        loadData();
    }

    // =============================================================================================
    // TÍTULO
    // =============================================================================================
    private void createTitle() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[]push[]", "[]"));
        JLabel title = new JLabel("Dashboard");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +3");
        panel.add(title);
        add(panel);
    }

    // =============================================================================================
    // CONCHA (SCROLL + LAYOUT VERTICAL)
    // =============================================================================================
    private void createPanelLayout() {
        panelLayout = new JPanel(new DashboardLayout());
        JScrollPane scrollPane = new JScrollPane(panelLayout);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "width:5; trackArc:$ScrollBar.thumbArc; trackInsets:0,0,0,0; thumbInsets:0,0,0,0;");
        add(scrollPane);
    }

    // =============================================================================================
    // TOPO: 3 CARDS (Total de Clientes | Agendamentos Hoje | Faturamento do Mês)
    // =============================================================================================
    private void createTopCards() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[fill]", "[]"));
        cardBox = new CardBox();
        // Ícones existentes do seu projeto demo (ajuste se quiser trocar)
        cardBox.addCardItem(createIcon("raven/modal/demo/icons/dashboard/customer.svg", UIManager.getColor("Component.accentColor")), "Total de Clientes");
        cardBox.addCardItem(createIcon("raven/modal/demo/icons/dashboard/income.svg", UIManager.getColor("Actions.Blue")), "Agendamentos Hoje");
        cardBox.addCardItem(createIcon("raven/modal/demo/icons/dashboard/profit.svg", UIManager.getColor("Actions.Green")), "Faturamento do Mês");
        panel.add(cardBox);
        panelLayout.add(panel);
    }

    // =============================================================================================
    // MEIO: ESQUERDA (Agenda do Dia) | DIREITA (Próximos Agendamentos)
    // =============================================================================================
    private void createMiddlePanels() {
        panelMiddle = new JPanel(new MigLayout("fillx,gap 14", "[grow,fill][grow,fill]", "[350]"));

        // ——— Agenda do Dia (Esquerda)
        panelAgendaDia = new JPanel(new MigLayout("fill, insets 12", "[grow,fill]", "[][grow]"));
        panelAgendaDia.putClientProperty(FlatClientProperties.STYLE, "arc:12;");
        JLabel t1 = new JLabel("Agenda do Dia");
        t1.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
        panelAgendaDia.add(t1, "wrap, gapbottom 6");

        modelAgendaDia = new DefaultTableModel(
                new Object[]{"Hora", "Cliente", "Serviço", "Profissional", "Obs."}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableAgendaDia = new JTable(modelAgendaDia);
        stylizeTable(tableAgendaDia);
        JScrollPane sp1 = new JScrollPane(tableAgendaDia);
        stylizeScroll(sp1);
        panelAgendaDia.add(sp1, "grow");

        // ——— Próximos Agendamentos (Direita)
        panelProximos = new JPanel(new MigLayout("fill, insets 12", "[grow,fill]", "[][grow]"));
        panelProximos.putClientProperty(FlatClientProperties.STYLE, "arc:12;");
        JLabel t2 = new JLabel("Próximos Agendamentos");
        t2.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
        panelProximos.add(t2, "wrap, gapbottom 6");

        modelProximos = new DefaultTableModel(
                new Object[]{"Hora", "Cliente", "Serviço", "Profissional"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableProximos = new JTable(modelProximos);
        stylizeTable(tableProximos);
        JScrollPane sp2 = new JScrollPane(tableProximos);
        stylizeScroll(sp2);
        panelProximos.add(sp2, "grow");

        // adiciona lado a lado
        panelMiddle.add(panelAgendaDia, "grow");
        panelMiddle.add(panelProximos, "grow");

        panelLayout.add(panelMiddle);
    }

    // =============================================================================================
    // BASE: SERVIÇOS RECENTES (largura total)
    // =============================================================================================
    private void createBottomServices() {
        panelServicosRecentes = new JPanel(new MigLayout("fill, insets 12", "[grow,fill]", "[][grow]"));
        panelServicosRecentes.putClientProperty(FlatClientProperties.STYLE, "arc:12;");

        JLabel t3 = new JLabel("Serviços Recentes");
        t3.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
        panelServicosRecentes.add(t3, "wrap, gapbottom 6");

        modelServicos = new DefaultTableModel(
                new Object[]{"Data", "Hora", "Cliente", "Serviço", "Profissional"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableServicos = new JTable(modelServicos);
        stylizeTable(tableServicos);
        JScrollPane sp = new JScrollPane(tableServicos);
        stylizeScroll(sp);
        panelServicosRecentes.add(sp, "grow");

        panelLayout.add(panelServicosRecentes);
    }

    // =============================================================================================
    // CARREGAMENTO DE DADOS (cards + tabelas)
    // =============================================================================================
    private void loadData() {
        // Inicia controllers (1 conexão compartilhada)
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (agendamentoController == null) agendamentoController = new AgendamentoController(conn);
            if (clienteController == null) clienteController = new ClienteController(conn);
        } catch (SQLException ex) {
            showError("Falha ao conectar ao banco de dados.", ex);
            return;
        }

        // ---------- CARDS ----------
        String totalClientesStr = "—";
        String agHojeStr = "—";
        String faturamentoMesStr = "—";

        try {
            // Total de Clientes
            // Se não existir um método count() no seu controller, mantenho “—”
            Integer totalClientes = null;
            try {
                // tente adequar para o que você tem (ex.: clienteController.contarClientes())
                totalClientes = invokeSafeCountClientes();
            } catch (Exception ignore) {}

            if (totalClientes != null) totalClientesStr = String.valueOf(totalClientes);

            // Agendamentos de Hoje
            String hojeISO = DataUtils.formatarParaISO(new Date());
            List<Agendamento> agHoje = agendamentoController.consultarPorFiltros("", "", "", hojeISO);
            if (agHoje != null) agHojeStr = String.valueOf(agHoje.size());

            // Faturamento do Mês (placeholder seguro)
            // Ajuste aqui quando tiver método que some preço dos serviços do mês.
            Double totalMes = null;
            try {
                totalMes = invokeSafeFaturamentoMes();
            } catch (Exception ignore) {}
            if (totalMes != null) {
                faturamentoMesStr = "R$ " + new DecimalFormat("#,##0.00").format(totalMes);
            }

        } catch (Exception ex) {
            // se algo falhar, mantém valores “—”
            ex.printStackTrace();
        }

        // Aplica nos cards (subtítulos sutis como no demo)
        cardBox.setValueAt(0, totalClientesStr, "clientes cadastrados", "", true);
        cardBox.setValueAt(1, agHojeStr, "agendamentos hoje", "", true);
        cardBox.setValueAt(2, faturamentoMesStr, "mês corrente", "", true);

        // ---------- TABELAS ----------
        carregarAgendaDoDia();
        carregarProximosHoje();
        carregarServicosRecentesHoje();
    }

    // Agenda do Dia
    private void carregarAgendaDoDia() {
        if (agendamentoController == null) return;
        try {
            String hojeISO = DataUtils.formatarParaISO(new Date());
            List<Agendamento> lista = agendamentoController.consultarPorFiltros("", "", "", hojeISO);

            modelAgendaDia.setRowCount(0);
            if (lista != null) {
                for (Agendamento a : lista) {
                    String hora = safe(a.getHora());
                    String cliente = (a.getCliente() != null) ? safe(a.getCliente().getNome()) : "";
                    String servico = safe(String.valueOf(a.getServico())); // getServico retorna String
                    String funcionario = (a.getFuncionario() != null) ? safe(a.getFuncionario().getNome()) : "";
                    String obs = safe(a.getObservacoes());
                    modelAgendaDia.addRow(new Object[]{hora, cliente, servico, funcionario, obs});
                }
            }

            if (tableAgendaDia.getColumnCount() >= 5) {
                tableAgendaDia.getColumnModel().getColumn(0).setMaxWidth(UIScale.scale(90));
                tableAgendaDia.getColumnModel().getColumn(4).setPreferredWidth(UIScale.scale(220));
            }
        } catch (Exception ex) {
            showError("Falha ao carregar Agenda do Dia.", ex);
        }
    }

    // Próximos (usa hoje; se sua consulta já ordena por hora, ótimo)
    private void carregarProximosHoje() {
        if (agendamentoController == null) return;
        try {
            String hojeISO = DataUtils.formatarParaISO(new Date());
            List<Agendamento> lista = agendamentoController.consultarPorFiltros("", "", "", hojeISO);

            modelProximos.setRowCount(0);
            if (lista != null) {
                for (Agendamento a : lista) {
                    String hora = safe(a.getHora());
                    String cliente = (a.getCliente() != null) ? safe(a.getCliente().getNome()) : "";
                    String servico = safe(String.valueOf(a.getServico()));
                    String funcionario = (a.getFuncionario() != null) ? safe(a.getFuncionario().getNome()) : "";
                    modelProximos.addRow(new Object[]{hora, cliente, servico, funcionario});
                }
            }
            if (tableProximos.getColumnCount() > 0) {
                tableProximos.getColumnModel().getColumn(0).setMaxWidth(UIScale.scale(90));
            }
        } catch (Exception ex) {
            showError("Falha ao carregar Próximos Agendamentos.", ex);
        }
    }

    // Serviços Recentes (hoje)
    private void carregarServicosRecentesHoje() {
        if (agendamentoController == null) return;
        try {
            String hojeISO = DataUtils.formatarParaISO(new Date());
            List<Agendamento> lista = agendamentoController.consultarPorFiltros("", "", "", hojeISO);

            modelServicos.setRowCount(0);
            if (lista != null) {
                for (Agendamento a : lista) {
                    String data = hojeISO;
                    String hora = safe(a.getHora());
                    String cliente = (a.getCliente() != null) ? safe(a.getCliente().getNome()) : "";
                    String servico = safe(String.valueOf(a.getServico()));
                    String funcionario = (a.getFuncionario() != null) ? safe(a.getFuncionario().getNome()) : "";
                    modelServicos.addRow(new Object[]{data, hora, cliente, servico, funcionario});
                }
            }

            if (tableServicos.getColumnCount() >= 2) {
                tableServicos.getColumnModel().getColumn(0).setMaxWidth(UIScale.scale(120));
                tableServicos.getColumnModel().getColumn(1).setMaxWidth(UIScale.scale(90));
            }
        } catch (Exception ex) {
            showError("Falha ao carregar Serviços Recentes.", ex);
        }
    }

    // =============================================================================================
    // HELPERS VISUAIS E UTILITÁRIOS
    // =============================================================================================
    private Icon createIcon(String icon, Color color) {
        return new FlatSVGIcon(icon, 0.4f).setColorFilter(new FlatSVGIcon.ColorFilter(c -> color));
    }

    private void stylizeTable(JTable table) {
        table.setRowHeight(UIScale.scale(32));
        table.putClientProperty(FlatClientProperties.STYLE,
                "showHorizontalLines:true; showVerticalLines:false; intercellSpacing:0,1; selectionBackground:$Component.focusColor;");
        table.setAutoCreateRowSorter(true);
    }

    private void stylizeScroll(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
        if (sp.getVerticalScrollBar() != null) {
            sp.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "width:8;thumbArc:$ScrollBar.thumbArc;");
        }
        if (sp.getHorizontalScrollBar() != null) {
            sp.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "height:8;thumbArc:$ScrollBar.thumbArc;");
        }
    }

    private String safe(String s) { return (s == null) ? "" : s; }

    private void showError(String title, Exception ex) {
        ex.printStackTrace();
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, title + "\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE));
    }

    // =============================================================================================
    // CHAMADAS “SAFES” PARA CONTADORES (opcionais, não quebram se não existir)
    // =============================================================================================
    private Integer invokeSafeCountClientes() {
        try {
            // Ajuste para o método real que você tiver no ClienteController/DAO
            // Exemplo comum:
            // return clienteController.contarClientes();
            return null; // placeholder silencioso
        } catch (Throwable t) {
            return null;
        }
    }

    private Double invokeSafeFaturamentoMes() {
        try {
            // Ajuste para o método real que você tiver (somatório de valores do mês)
            // Exemplo:
            // return agendamentoController.totalFaturamentoMesAtual();
            return null; // placeholder silencioso
        } catch (Throwable t) {
            return null;
        }
    }

    // =============================================================================================
    // MESMO LAYOUT CONCEITUAL DO SEU DASHBOARD (empilha os blocos verticalmente)
    // =============================================================================================
    private class DashboardLayout implements LayoutManager {
        private int gap = 0;

        @Override public void addLayoutComponent(String name, Component comp) {}
        @Override public void removeLayoutComponent(Component comp) {}

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int width = (insets.left + insets.right);
                int height = insets.top + insets.bottom;
                int g = UIScale.scale(gap);
                int count = parent.getComponentCount();
                for (int i = 0; i < count; i++) {
                    Component com = parent.getComponent(i);
                    Dimension size = com.getPreferredSize();
                    height += size.height;
                }
                if (count > 1) height += (count - 1) * g;
                return new Dimension(width, height);
            }
        }

        @Override public Dimension minimumLayoutSize(Container parent) { return new Dimension(10, 10); }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int g = UIScale.scale(gap);
                int count = parent.getComponentCount();
                for (int i = 0; i < count; i++) {
                    Component com = parent.getComponent(i);
                    Dimension size = com.getPreferredSize();
                    com.setBounds(x, y, width, size.height);
                    y += size.height + g;
                }
            }
        }
    }
}
