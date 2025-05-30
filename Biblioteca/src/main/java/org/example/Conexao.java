package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BIBLIOTECA";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "2052";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
