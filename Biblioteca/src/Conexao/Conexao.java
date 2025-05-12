package Conexao;

import java.sql.*;

public class Conexao {
    public static Connection conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/biblioteca"; // Endereço do banco de dados
        String user = "root"; // Usuário do banco de dados
        String password = ""; // Senha do banco de dados (deixe em branco ou altere conforme necessário)
        
        // Estabelece a conexão com o banco de dados e retorna o objeto Connection
        return DriverManager.getConnection(url, user, password);
    }
}
