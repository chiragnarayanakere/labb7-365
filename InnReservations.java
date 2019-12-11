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
            try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql1)) {

            System.out.println("");

            String r = "RoomName";
            String pop = "popularity";

            System.out.format("%-30s %-25s", r, pop);
            System.out.println("");

               while (rs.next()) {
                    String RoomName = rs.getString("RoomName");
                    float popu = rs.getFloat("popu");
                    System.out.format("%-30s%5.2f\n", RoomName, popu);
                }

               System.out.println("");
            }
               
            try (Statement stmt2 = conn.createStatement()) {

                boolean exRes2 = stmt2.execute(sql2);
                System.out.format("Result: %b %n", exRes2);
                System.out.println("");
            }

            try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql3)) {

               String room = "RoomName";
               String stay = "Stay";
               String co = "Recent Checkout";

               System.out.format("%-10s %21s %19s", room, stay, co);
               System.out.println("");

               while (rs.next()) {
                    String RoomName = rs.getString("RoomName");
                    int rsl = rs.getInt("recent_stay_length");
                    String date = rs.getString("recent_checkout");
                    System.out.format("%-30s %-2d %15s\n", RoomName, rsl, date);
                }

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

   private void func_req_6() throws SQLException{
   
      System.out.println("");

      int[][] rev = new int[10][12];
      String[] names = new String[10];

      //create sql statement, pass to function
      for (int i = 0; i < 12; i++) {

         fill_table(i, rev);
      }
      
      get_names(names);

      print_table(rev, names);

   }

   private void get_names(String[] names) throws SQLException {

      int count = 0;

      try {
            Connection conn = DriverManager.getConnection(url, name, pass);
            
            String sql = "select RoomName"
                         + " from cnarayan.lab7_rooms r, cnarayan.lab7_reservations re"
                         + " where r.RoomCode = re.Room"
                         + " group by Room"
                         + " order by Room";

            //create new statement per sql query
            try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql)) {

               //System.out.println("");

               while (rs.next()) {
                    String RoomName = rs.getString("RoomName");
                    //int month = rs.int("month");
                    //String n = rs.getString("Monthly_Revenue");
                    names[count++] = RoomName;
                }

              // System.out.println("");
            }

      } finally {}
   }

   private void print_table(int[][] rev, String[] names) throws SQLException {

      System.out.format("%-30s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s%-5s\n", "RoomName", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Total");

      for (int i = 0; i < 10; i++) {

         System.out.format("%-30s%-5d%-5d%-5d%-5d%-5d%-5d%-5d%-5d%-5d%-5d%-5d%-5d%-5d", 
            names[i], rev[i][0], rev[i][1], rev[i][2], rev[i][3], rev[i][4], rev[i][5], rev[i][6],
            rev[i][7], rev[i][8], rev[i][9], rev[i][10], rev[i][11], 
            rev[i][0]+rev[i][1]+rev[i][2]+rev[i][3]+rev[i][4]+rev[i][5]+rev[i][6]+rev[i][7]+rev[i][8]+rev[i][9]+ rev[i][10]+rev[i][11]);

         System.out.println("");
      }

      System.out.println("");
   }

   private void fill_table(int i, int[][] rev) throws SQLException {

      int roomName_counter = 0;

      try {
            Connection conn = DriverManager.getConnection(url, name, pass);
            
            String sql = "select RoomName, MONTH(CheckOut) as month,"
                         + " round(SUM((DATEDIFF(CheckOut, CheckIn) * Rate)), 0) as Monthly_Revenue"
                         + " from cnarayan.lab7_rooms r, cnarayan.lab7_reservations re"
                         + " where r.RoomCode = re.Room"
                         + " and MONTH(CheckOut) = " + (i + 1) 
                         + " group by Room, month"
                         + " order by Room";

            //create new statement per sql query
            try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql)) {

               //System.out.println("");

               while (rs.next()) {
                    //String RoomName = rs.getString("RoomName");
                    //int month = rs.int("month");
                    int mr = rs.getInt("Monthly_Revenue");
                    rev[roomName_counter++][i] = mr;
                }

              // System.out.println("");
            }

      } finally {}

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
