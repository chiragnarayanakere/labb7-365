import java.sql.*;

public class InnReservations {

    String url = "jdbc:mysql://db.labthreesixfive.com/cnarayan?autoReconnect=true&useSSL=false";
    String name = "cnarayan";
    String pass = "CSC365-F2019_011277717";

    public static void main(String[] args) {

        try {
            InnReservations hp = new InnReservations();
            System.out.println("hi!");
            hp.demo2();
            System.out.println("hey!");
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Erro: " + e.getErrorCode());
            System.err.println("StackTrace: " + e.getStackTrace());
        }
    }

    private void demo2() throws SQLException {

        try {
            Connection conn = DriverManager.getConnection(url, name, pass);
            System.out.println("worked!");
            
            String sql = "SELECT * FROM INN.Reservations";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

                // Step 4: Send SQL statement to DBMS
                boolean exRes = stmt.execute(sql);
                
                // Step 5: Handle results
                System.out.format("Result: %b %n", exRes);
            }

            
        } finally{} 
    }

}
