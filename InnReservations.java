import java.sql.*;
import java.util.*;
import java.time.LocalDate;


public class InnReservations {

    String url = "jdbc:mysql://db.labthreesixfive.com/tpluu?autoReconnect=true&useSSL=false";
    String name = "tpluu";
    String pass = "CSC365-F2019_010053260";

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

    // connecting to db for fr3
    private void func_req_3() throws SQLException {
        int code = 0;
        String room = null;
        boolean validIn = false;
        boolean validOut = false;
        List<Object> params = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder("UPDATE lab7_reservations SET ");
                         
        Scanner scanner = new Scanner(System.in);        
                              
        try (Connection conn = DriverManager.getConnection(url, name, pass)){
            
            System.out.print("Please enter your reservation number: ");
            code = scanner.nextInt();

            String selectReservation = "SELECT * FROM lab7_reservations WHERE code = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(selectReservation)) {
                 pstmt.setInt(1, code);

                 // executing the prepared statement
                try(ResultSet rs = pstmt.executeQuery()) {
                    // query result is empty set, reservation code not valid
                    if (!rs.isBeforeFirst() ) {    
                        System.out.println("Error: Invalid Reservation Code"); 
                        System.out.println("===============================");
                    }
                    while(rs.next()) {
                        room = rs.getString("Room"); 
                    }
                }
            }
            // reservation code is valid, continuing update
            System.out.println("Please enter new values for the following fields, or \"n\" for no change");

            // first name change
            System.out.print("First Name: ");
            String firstName = scanner.next();
            if(!"n".equals(firstName)) {
                params.add(firstName);
                sb.append("FirstName = ?");
            }
            
            // last name change
            System.out.print("Last Name: ");
            String lastName = scanner.next();
            if(!"n".equals(lastName)) {
                if(params.size() > 0){
                    sb.append(", LastName = ?");
                } else {
                    sb.append("LastName = ?");
                }
                params.add(lastName);
            }

            // number of adults change
            System.out.print("Number of Adults: ");
            String adults = scanner.next();
            int num_adults = 0;
            if(!"n".equals(adults)){
                num_adults = Integer.parseInt(adults);

                if(params.size() > 0){
                    sb.append(", Adults = ?");
                } else {
                    sb.append("Adults = ?");
                }
                params.add(num_adults);    
            }

            // number of kids change
            System.out.print("Number of Kids: ");
            String kids = scanner.next();
            int num_kids = 0;
            if(!"n".equals(kids)){
                num_kids = Integer.parseInt(kids);

                if(params.size() > 0){
                    sb.append(", Kids = ?");
                } else {
                    sb.append("Kids = ?");
                }
                params.add(num_kids);    
            }

            // check in, check out dates
            System.out.print("Check In (YYYY-MM-DD): ");
            String checkIn = scanner.next();
            System.out.print("Check Out (YYYY-MM-DD): ");
            String checkOut = scanner.next();

            if(!"n".equals(checkIn) && "n".equals(checkOut)) { // only check in changed
                LocalDate inDate = LocalDate.parse(checkIn);
                validIn = validCheckin(inDate,room);

                if(!validIn){
                    if(params.size() > 0){
                        sb.append(", CheckIn = ?");
                    } else {
                        sb.append("CheckIn = ?");
                    }
                    params.add(inDate); 
                }
            } else if("n".equals(checkIn) && !"n".equals(checkOut)) { //only checkout change
                LocalDate outDate = LocalDate.parse(checkOut);
                validOut = validCheckin(outDate,room);

                if(!validOut){
                    if(params.size() > 0){
                        sb.append(", Checkout = ?");
                    } else {
                        sb.append("Checkout = ?");
                    }
                    params.add(outDate); 
                }
            } else if(!"n".equals(checkIn) && !"n".equals(checkOut)) { //both changed
                LocalDate inDate = LocalDate.parse(checkIn);
                LocalDate outDate = LocalDate.parse(checkOut);

                validIn = validCheckin(inDate,room);
                validOut = validCheckin(outDate,room);

                if(!validIn || !validOut) {
                    if(params.size() > 0){
                        sb.append(", Checkout = ?, Checkout = ?");
                    } else {
                        sb.append("Checkout = ?, Checkout = ?");
                    }
                    params.add(inDate);
                    params.add(outDate);
                } else {
                    System.out.println("Error: Dates entered have conflicts, please try again");
                }
            }

            if(params.size() > 0) {
                params.add(code);
                sb.append(" WHERE code = ?");

                try (PreparedStatement pstmt2 = conn.prepareStatement(sb.toString())) {
                    int i = 1;
                    for (Object p : params) {
                        pstmt2.setObject(i++, p);
                    }
                    int rowsUpdated = pstmt2.executeUpdate();

                    System.out.format("Updated %d records in reservation%n", rowsUpdated);
                }  
            } else {
                System.out.println("No records updated");
            }
        }
    }
    // helper function for req3: checking valid check in date
    private boolean validCheckin(LocalDate date, String room) throws SQLException{
        boolean valid = false;
        String check = "select * from lab7_reservations " +
                       "where (? >= checkin and ? < checkout) and room = ?";
        try (Connection conn = DriverManager.getConnection(url, name, pass)){              
            try (PreparedStatement pstmt = conn.prepareStatement(check)) {
                 pstmt.setDate(1, java.sql.Date.valueOf(date));
                 pstmt.setDate(2, java.sql.Date.valueOf(date));
                 pstmt.setString(3, room);

                 // executing the prepared statement
                try(ResultSet rs = pstmt.executeQuery()) {
                    // query result is empty set, there is no conflict with checkin
                    if (!rs.isBeforeFirst()) {    
                        valid = true;
                    } 
                }
            }
        }
        return valid;
    }

    // helper function for req3: checking valid checkout date
    private boolean validCheckOut(LocalDate date, String room) throws SQLException{
        boolean valid = false;
        String check = "select * from lab7_reservations " +
                       "where (? > checkin and ? <= checkout) and room = ?";
        try (Connection conn = DriverManager.getConnection(url, name, pass)){              
            try (PreparedStatement pstmt = conn.prepareStatement(check)) {
                 pstmt.setDate(1, java.sql.Date.valueOf(date));
                 pstmt.setDate(2, java.sql.Date.valueOf(date));
                 pstmt.setString(3, room);

                 // executing the prepared statement
                try(ResultSet rs = pstmt.executeQuery()) {
                    // query result is empty set, there is no conflict with checkin
                    if (!rs.isBeforeFirst()) {    
                        valid = true;
                    } 
                }
            }
        }
        return valid;
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