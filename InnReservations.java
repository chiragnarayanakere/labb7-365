// testing branch -thinh

import java.sql.*;

public class InnReservations {

    String url = "jdbc:mysql://db.labthreesixfive.com/tpluu?autoReconnect=true&useSSL=false";
    String name = "tpluu";
    String pass = "CSC365-F2019_010053260";

    public static void main(String[] args) {

        try {
            InnReservations hp = new InnReservations();
            hp.demo2();
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
            
            String sql = "SELECT * FROM lab7_rooms";
            String sql2 = "SELECT * FROM lab7_reservations where firstname = "Thinh"";

            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

                // Step 4: Send SQL statement to DBMS
                boolean exRes = stmt.execute(sql);
                
                // Step 5: Handle results
                System.out.format("Result: %b %n", exRes);
            }

            try (Statement stmt2 = conn.createStatement()) {

                // Step 4: Send SQL statement to DBMS
                boolean exRes2 = stmt2.execute(sql2);
                
                // Step 5: Handle results
                System.out.format("Result: %b %n", exRes2);
            }

            
        } finally{} 
    }

    

}
