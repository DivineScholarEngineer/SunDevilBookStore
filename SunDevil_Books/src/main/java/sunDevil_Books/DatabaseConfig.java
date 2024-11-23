package sunDevil_Books; // Replace with your actual package name

public class DatabaseConfig {
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // This URL connects to the MySQL server without specifying a database
    public static final String DB_URL_NO_DB = "jdbc:mysql://localhost:3306/";
    // This URL connects to the sunDevilBooks database
    public static final String DB_URL = "jdbc:mysql://localhost:3306/sunDevilBooks";
    public static final String DB_USER = "root";     // Replace with your MySQL username
    public static final String DB_PASSWORD = "Sql-Protect_24-2-2023-EF25"; // Replace with your MySQL password
}
