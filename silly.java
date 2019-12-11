import java.sql.*;
import java.util.*;

public class silly {

    String url = "jdbc:mysql://db.labthreesixfive.com/cnarayan?autoReconnect=true&useSSL=false";
    String name = "cnarayan";
    String pass = "CSC365-F2019_011277717";

    //check to see what we need to output
    int flag;

    public static void main(String[] args) {

        try {
            silly hp = new silly();
         
            while (true) {
            
               hp.connect_to_DB();
            }
         
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Erro: " + e.getErrorCode());
            System.err.println("StackTrace: " + e.getStackTrace());
        }
    }

    private String func_req_1() {
   
      System.out.println("Rooms and Rates");

      //create sql statement, pass to function
      String sql = "select RoomName, round(sum(DATEDIFF(CheckOut, CheckIn))/180, 2) as popu"
                    + " from cnarayan.lab7_rooms r, cnarayan.lab7_reservations re"
                    + " where r.RoomCode = re.Room"
                    + " and CheckIn >= DATE_ADD(NOW(), INTERVAL -180 DAY)"
                    + " group by RoomName"
                    + " order by popu desc";

      flag = 1;

      return sql;
    }

    private String func_req_2() {
   
      System.out.println("Make a Reservation");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

      flag = 2;

      return sql;
    }

    private String func_req_3() {
   
      System.out.println("Edit Reservation");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

      flag = 3;

      return sql;
    }

    private String  func_req_4() {
   
      System.out.println("Cancel Reservation");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

      flag = 4;

      return sql;
    }


   private String func_req_5() {
   
      System.out.println("Detailed Reservation Information");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

      flag = 5;

      return sql;
   }

   private String func_req_6() {
   
      System.out.println("Revenue Overview");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

      flag = 6;

      return sql;
   }

    private String display_prompt() {

      int choice;
      String statement = "";
      Scanner sc = new Scanner(System.in);

      //options menu
      System.out.println("Press 1 to view Rooms and Rates");
      System.out.println("Press 2 to make a Reservation");
      System.out.println("Press 3 to edit an existing Reservation");
      System.out.println("Press 4 to cancel a Reservation");
      System.out.println("Press 5 to see Detailed Reservation Information");
      System.out.println("Press 6 to view an Overview of Revenue");
      System.out.println("Press 0 to quit");

      
      //user selection
      System.out.print("How can we help you: ");

      //get user input
      while (!sc.hasNextInt()) {

         System.out.println("Please enter a valid command!");
         sc.next();
      }

      choice = sc.nextInt();

     
      //validate user input + run respective function
      switch (choice) {
      
         case 0: System.out.println("Goodbye!");
                 System.exit(1);
                 break;
         case 1: statement = func_req_1();
                 break;
         case 2: statement = func_req_2();
                 break;
         case 3: statement = func_req_3();
                 break;
         case 4: statement = func_req_4();
                 break;
         case 5: statement = func_req_5();
                 break;
         case 6: statement = func_req_6();
                 break;
         default: System.out.println("Please enter a valid command!");
      }
      return statement;

   }

   private void connect_to_DB() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, name, pass);
            
            String sql = display_prompt();

            System.out.println("SQL: " + sql);
            System.out.println("flag: " + flag);
            
            // Step 3: (omitted in this example) Start transaction

            try (Statement stmt = conn.createStatement()) {

                // Step 4: Send SQL statement to DBMS
                //boolean exRes = stmt.execute(sql);
                boolean exRes = true;
                
                // Step 5: Handle results
                switch (flag) {
      
                  case 0: exRes = stmt.execute(sql);
                          System.out.format("Result: %b %n", exRes);
                          System.out.println(""); 
                          break;
                  case 1: exRes = stmt.execute(sql);
                          System.out.format("Result: %b %n", exRes);
                          System.out.println(""); 
                          break;
                  case 2: exRes = stmt.execute(sql);
                          System.out.format("Result: %b %n", exRes);
                          System.out.println(""); 
                          break;
                  case 3: exRes = stmt.execute(sql);
                          System.out.format("Result: %b %n", exRes);
                          System.out.println(""); 
                          break;
                  case 4: exRes = stmt.execute(sql);
                          System.out.format("Result: %b %n", exRes);
                          System.out.println(""); 
                          break;
                  case 5: exRes = stmt.execute(sql);
                          System.out.format("Result: %b %n", exRes);
                          System.out.println(""); 
                          break;
                  case 6: exRes = stmt.execute(sql);
                          System.out.format("Result: %b %n", exRes);
                          System.out.println(""); 
                          break;
                  default: System.out.println("Figure this out");
               }

                System.out.format("Result: %b %n", exRes);
                System.out.println("");
            }

            /*try (Statement stmt2 = conn.createStatement()) {

                // Step 4: Send SQL statement to DBMS
                boolean exRes2 = stmt2.execute(sql2);
                
                // Step 5: Handle results
                System.out.format("Result: %b %n", exRes2);
            }*/

            
        } finally{} 
    }

}
