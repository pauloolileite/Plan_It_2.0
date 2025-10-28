package raven.modal.demo.menu;

import raven.modal.demo.forms.*; // Importa todos os forms do pacote
import raven.modal.demo.model.ModelUser;
import raven.modal.demo.system.FormManager;
import raven.modal.drawer.menu.MenuAction; // <<< --- Importar MenuAction ---
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.SimpleMenuAction;
import raven.modal.drawer.simple.SimpleMenuItem;
import raven.modal.drawer.simple.SimpleSubmenuMenuItem;

import javax.swing.*;

public class MyDrawerBuilder extends SimpleDrawerBuilder {

    private static MyDrawerBuilder instance;
    private ModelUser user;

    public static MyDrawerBuilder getInstance() {
        if (instance == null) {
            instance = new MyDrawerBuilder();
        }
        return instance;
    }

    public void setUser(ModelUser user) {
        this.user = user;
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        String avatarPath = "/raven/modal/demo/drawer/image/avatar_male.svg"; // Padrão
        return new SimpleHeaderData()
                .setIcon(new ImageIcon(getClass().getResource(avatarPath)))
                .setTitle(user != null ? user.getUserName() : "Usuário")
                .setDescription(user != null ? user.getEmail() : "email@exemplo.com");
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .add(new SimpleMenuAction("Logout", new MenuAction() { // Ação de Logout (exemplo correto)
                    @Override
                    public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                        FormManager.logout();
                    }
                }));
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {

        // --- CORREÇÃO: Usar implementação de MenuAction para cada item ---

        SimpleMenuItem itemDashboard = new SimpleMenuItem("Dashboard", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormDashboard.class); // Chama FormManager para mostrar o Form
            }
        }), "dashboard.svg");

        SimpleMenuItem itemAgendamentos = new SimpleMenuItem("Agendamentos", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormAgendamentos.class);
            }
        }), "calendar.svg");

        SimpleMenuItem itemClientes = new SimpleMenuItem("Clientes", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormClientes.class);
            }
        }), "customer.svg"); // Verifique o ícone

        SimpleMenuItem itemFuncionarios = new SimpleMenuItem("Funcionários", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormFuncionarios.class);
            }
        }), "user.svg"); // Verifique o ícone

        SimpleMenuItem itemServicos = new SimpleMenuItem("Serviços", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormServicos.class);
            }
        }), "page.svg"); // Verifique o ícone

        // Itens do Submenu Sistema
        SimpleMenuItem itemUsuarios = new SimpleMenuItem("Gerenciar Usuários", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormUsuarios.class);
            }
        }), "setting.svg");

        SimpleMenuItem itemRelatorios = new SimpleMenuItem("Relatórios", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormRelatorios.class);
            }
        }), "chart.svg");

        // Submenu Sistema
        SimpleSubmenuMenuItem itemSistema = new SimpleSubmenuMenuItem("Sistema", "setting.svg")
                .addMenuItem(itemUsuarios)
                .addMenuItem(itemRelatorios);

        // Opção de Configurações (exemplo final)
        SimpleMenuItem itemConfiguracoes = new SimpleMenuItem("Configurações", new SimpleMenuAction(new MenuAction() {
            @Override
            public void menuSelected(SimpleMenuAction action, int index, int subIndex) {
                FormManager.showForm(FormSetting.class);
            }
        }), "setting.svg");


        // Monta o Menu
        SimpleMenuOption menuOption = new SimpleMenuOption()
                .setMenuValidation(MyMenuValidation.getInstance()) // Mantém validação, se houver
                .add(itemDashboard)
                .add(itemAgendamentos)
                .add(itemClientes)
                .add(itemFuncionarios)
                .add(itemServicos);

        // Adiciona Sistema se for ADMIN
        if (user != null && user.getRole() == ModelUser.Role.ADMIN) {
            menuOption.add(itemSistema);
        }

        // Adiciona Configurações
        menuOption.add(itemConfiguracoes);

        return menuOption;
    }
}