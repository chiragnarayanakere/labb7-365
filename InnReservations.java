import java.sql.*;
import java.util.*;

public class InnReservations {

    String url = "jdbc:mysql://db.labthreesixfive.com/cnarayan?autoReconnect=true&useSSL=false";
    String name = "cnarayan";
    String pass = "CSC365-F2019_011277717";

    public static void main(String[] args) throws SQLException{

        try {
            InnReservations hp = new InnReservations();

            while (true) {

               hp.display_prompt();
            }


        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Erro: " + e.getErrorCode());
            System.err.println("StackTrace: " + e.getStackTrace());
        }
    }

    private void func_req_1() throws SQLException{

      System.out.println("Rooms and Rates");

      //create sql statements, pass to function
      String sql1 = "select RoomName, round(sum(DATEDIFF(CheckOut, CheckIn))/180, 2) as popu"
                    + " from cnarayan.lab7_rooms r, cnarayan.lab7_reservations re"
                    + " where r.RoomCode = re.Room"
                    + " and CheckIn >= DATE_ADD(NOW(), INTERVAL -180 DAY)"
                    + " group by RoomName"
                    + " order by popu desc";

      String sql2 = "select * from cnarayan.lab7_rooms";

      String sql3 = "select RoomName, diff as recent_stay_length, Checkout as recent_checkout from"
                  + " (select RoomName, DATEDIFF(CheckOut, CheckIn) as diff, Checkout,"
                  + " RANK() over (partition by RoomName order by DATEDIFF(CheckOut, Now()) desc) as RANKING"
                  + " from cnarayan.lab7_rooms r, cnarayan.lab7_reservations re"
                  + " where r.RoomCode = re.Room"
                  + " and DATEDIFF(CheckOut, Now()) < 0) as t"
                  + " where RANKING = 1";

      //connect to the database, pass sql statements to function
      try {

         connect_to_DB_fr1(sql1, sql2, sql3);

      } catch (SQLException e) {
            throw new SQLException(e);
         }
    }

   //create new connect to DB function for each Function Requirement
   private void connect_to_DB_fr1(String s1, String s2, String s3) throws SQLException {

         try {
            Connection conn = DriverManager.getConnection(url, name, pass);

            String sql1 = s1;
            String sql2 = s2;
            String sql3 = s3;

            //create new statement per sql query
            try (Statement stmt = conn.createStatement()) {

               //need to figure out how to output the right values
                boolean exRes = stmt.execute(sql1);
                System.out.format("Result: %b %n", exRes);
                System.out.println("");
            }

            try (Statement stmt2 = conn.createStatement()) {

                boolean exRes2 = stmt2.execute(sql2);
                System.out.format("Result: %b %n", exRes2);
                System.out.println("");
            }

            try (Statement stmt3 = conn.createStatement()) {

                boolean exRes3 = stmt3.execute(sql3);
                System.out.format("Result: %b %n", exRes3);
                System.out.println("");
            }

        } finally{}
    }

    private void func_req_2() {

      System.out.println("Make a Reservation");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

    }

    private void func_req_3() {

      System.out.println("Edit Reservation");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

    }

    private void func_req_4() {

      System.out.println("Cancel Reservation");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

    }

   private void func_req_5() {

      System.out.println("Detailed Reservation Information");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

   }

   private void func_req_6() {

      System.out.println("Revenue Overview");

      //create sql statement, pass to function
      String sql = "SELECT * FROM lab7_rooms";

   }

    private void display_prompt() throws SQLException {

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
         case 1: func_req_1();
                 break;
         case 2: func_req_2();
                 break;
         case 3: func_req_3();
                 break;
         case 4: func_req_4();
                 break;
         case 5: func_req_5();
                 break;
         case 6: func_req_6();
                 break;
         default: System.out.println("Please enter a valid command!");
      }
   }
}
