package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static Properties properties = new Properties();

    // Carrega as propriedades do banco de dados ao inicializar a classe
    static {
        try (FileInputStream fis = new FileInputStream("src/main/resources/db.properties")) {
            properties.load(fis);
            // Carrega o driver JDBC (necessário para versões mais antigas do JDBC,
            // mas boa prática incluir para garantir compatibilidade)
            Class.forName(properties.getProperty("db.driver"));
            logger.info("Propriedades do banco de dados carregadas e driver JDBC registrado com sucesso.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar o arquivo de propriedades do banco de dados (db.properties).", e);
            // Considerar lançar uma RuntimeException aqui para impedir a inicialização incorreta
            // throw new RuntimeException("Não foi possível carregar as propriedades do banco de dados.", e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Driver JDBC não encontrado. Verifique se o driver está no classpath.", e);
            // throw new RuntimeException("Driver JDBC não encontrado.", e);
        }
    }

    /**
     * Obtém uma nova conexão com o banco de dados.
     * @return Uma instância de Connection ou null se a conexão falhar.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
            logger.fine("Conexão com o banco de dados estabelecida com sucesso."); // Usando fine para não poluir logs normais
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao estabelecer conexão com o banco de dados.", e);
            // É crucial retornar null aqui ou lançar exceção para sinalizar a falha.
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados de forma segura.
     * @param connection A conexão a ser fechada.
     */
    public static void closeConnection(Connection connection) {
        // --- CORREÇÃO AQUI ---
        // Verifica se a conexão não é nula ANTES de tentar usá-la
        if (connection != null) {
            try {
                // Verifica se já não está fechada antes de tentar fechar novamente
                if (!connection.isClosed()) {
                    connection.close();
                    logger.fine("Conexão com o banco de dados fechada com sucesso."); // Usando fine
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao fechar a conexão com o banco de dados.", e);
            }
        } else {
            logger.log(Level.WARNING, "Tentativa de fechar uma conexão nula foi ignorada."); // Loga se tentar fechar null
        }
        // --- FIM DA CORREÇÃO ---
    }
}