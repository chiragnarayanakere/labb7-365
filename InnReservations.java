import java.sql.*;
import java.util.*;
import java.time.LocalDate;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;


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

    private void func_req_2() throws SQLException {
   
      System.out.println("Make a Reservation");
      try {

         connect_to_DB_fr2();

      } catch (SQLException e) {
            throw new SQLException(e);
         }

    }

   // FR2 Connecting to DB 
   private void connect_to_DB_fr2() throws SQLException {
       
         // Maps room options to option numbers
         HashMap<String, String> map
                        = new HashMap<>();
         HashMap<String, String> baseprices
                        = new HashMap<>();
         try {
            Connection conn = DriverManager.getConnection(url, name, pass);
            
            // Collect user input for prefered rooms
            Scanner sc2 = new Scanner(System.in);
            System.out.println("Usage: [First Name] [Last Name] [Prefered Room Code | Any]"
                              + "[Prefered Bed Type | Any] [CheckIn] [CheckOut] [Num Children] [Num Adults] ");
            System.out.println("Please enter the dates in the form 'YYYY-MM-DD' Thank you");
            String FN = sc2.next(); // FirstName
            String LN = sc2.next(); // LastName
            String RC = sc2.next(); // Room Code **
            String BT = sc2.next(); // Bed Type **
            String CI = sc2.next(); // CheckIn
            String CO = sc2.next(); // CheckOut
            int NC = sc2.nextInt(); // Num Children
            int NA = sc2.nextInt(); // Num Adults

            if (NC + NA > 4) {
               System.out.println("Unfortunately, the maximum number of people in an INN room is 4. Please split your party into a smaller groups. Thank you.\n");
               return;
            }
            // checking for exact matches
            String sql = 
            " SELECT RoomCode, RoomName, Beds, bedType, " +
                   " maxOcc, basePrice, decor, " +
                   " ROW_NUMBER() OVER () as Opt " +
                      " FROM cnarayan.lab7_rooms AS Rooms " +
                      " WHERE RoomCode NOT IN ( " +
                            " SELECT DISTINCT R.RoomCode " +
                            " FROM cnarayan.lab7_rooms AS R " +
                            " INNER " +
                            " JOIN cnarayan.lab7_reservations AS RE " +
                            " ON RE.Room = R.RoomCode " +
                            " WHERE (RE.CheckIn <= ? " +
                            " AND ? < RE.Checkout) " +
                            " OR (RE.CheckIn < ? " +
                            " AND ? <= RE.Checkout) " +
                            " OR (RE.CheckIn >= ? " +
                            " AND Checkout <= ?) " +
                            " OR (RE.CheckIn <= ? " +
                            " AND Checkout >= ?)) " +
                      " AND (maxOcc >= ?) ";

               // Specified Bed type
               if (!(BT.equals("Any"))) {
                  sql = sql + " AND bedType = ? ";
               } 
               // Specified Room Code
               if (!(RC.equals("Any"))) {
                  sql = sql + " AND RoomCode = ? ";
               }

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

               // values 
               ps.setString(1, CI);
               ps.setString(2, CI);
               ps.setString(3, CO);
               ps.setString(4, CO);

               ps.setString(5, CI);
               ps.setString(6, CO);
               ps.setString(7, CI);
               ps.setString(8, CO);

               ps.setInt(9,(NA) + (NC));


               // Specified Bed type
               if (RC.equals("Any") && !(BT.equals("Any"))) {
                  ps.setString(10, BT);
               } else if (BT.equals("Any") && !(RC.equals("Any"))) {
                  ps.setString(10, RC);
               } else if (!(BT.equals("Any")) && !(RC.equals("Any"))) {
                  ps.setString(10, BT);
                  ps.setString(11, RC);
               }

               
               try (ResultSet rs = ps.executeQuery()) {
                  int option = 0;
                  String x, y, z;
                  String listing;

                  System.out.println("\nHere are the option(s) that meet all of your criteria: ");
                    System.out.println(" | Option | Room Code | Room Name               | Beds | Bed Type | Occupancy | Base Price | Decor     |");
                    System.out.println("---------------------------------------------------------------------------------------------------------");
                  while (rs.next()) {
                     x = rs.getString("RoomCode");
                     y = rs.getString("Opt");
                     z = rs.getString("basePrice");

                     listing = String.format(" |%-8s|%-11s|%-25s|%-6s|%-10s|%-11s|%-12s|%-11s|", 
                           y, x, rs.getString("RoomName"), 
                           rs.getString("Beds"), rs.getString("bedType"), 
                           rs.getString("maxOcc"), z, 
                           rs.getString("decor"));
                     System.out.println(listing);
                     map.put(y, listing);
                     baseprices.put(y, z);
                           option += 1;
                  }
                  // If nothing matched, do another query
                  if (option == 0) {
                     System.out.println("Unfortunately we do not have any " +
                           "exact matches for your search. " +
                           "Pleases consider these alternatives.");

                     String backup = 
                     " SELECT RoomCode, RoomName, Beds, bedType, " +
                            " maxOcc, basePrice, decor, " +
                            " ROW_NUMBER() OVER () as Opt " +
                               " FROM cnarayan.lab7_rooms AS Rooms " +
                               " WHERE RoomCode NOT IN ( " +
                                     " SELECT DISTINCT R.RoomCode " +
                                     " FROM cnarayan.lab7_rooms AS R " +
                                     " INNER " +
                                     " JOIN cnarayan.lab7_reservations AS RE " +
                                     " ON RE.Room = R.RoomCode " +
                                     " WHERE (RE.CheckIn <= ? " +
                                     " AND ? < RE.Checkout) " +
                                     " OR (RE.CheckIn < ? " +
                                     " AND ? <= RE.Checkout) " +
                                     " OR (RE.CheckIn >= ? " +
                                     " AND Checkout <= ?) " +
                                     " OR (RE.CheckIn <= ? " +
                                     " AND Checkout >= ?)) " +
                               " AND (maxOcc >= ?) ";
                     try (PreparedStatement pps = conn.prepareStatement(backup)) {
                         // values 
                         pps.setString(1, CI);
                         pps.setString(2, CI);
                         pps.setString(3, CO);
                         pps.setString(4, CO);

                         pps.setString(5, CI);
                         pps.setString(6, CO);
                         pps.setString(7, CI);
                         pps.setString(8, CO);

                         pps.setInt(9,(NA) + (NC));

                      try (ResultSet rs2 = pps.executeQuery()) {
                         option = 0;

                         System.out.println("\nHere are the option(s) that meet all of your date and occupancy criteria: ");
                           System.out.println(" | Option | Room Code | Room Name               | Beds | Bed Type | Occupancy | Base Price | Decor     |");
                           System.out.println("---------------------------------------------------------------------------------------------------------");
                         while (rs2.next() && option < 5) {
                            x = rs2.getString("RoomCode");
                            y = rs2.getString("Opt");
                            z = rs2.getString("basePrice");

                            listing = String.format(" |%-8s|%-11s|%-25s|%-6s|%-10s|%-11s|%-12s|%-11s|", 
                                  y, x, rs2.getString("RoomName"), 
                                  rs2.getString("Beds"), rs2.getString("bedType"), 
                                  rs2.getString("maxOcc"), z, 
                                  rs2.getString("decor"));
                            System.out.println(listing);
                            map.put(y, listing);
                            baseprices.put(y, z);
                                  option += 1;
                        }
                        if (option == 0) {
                           System.out.println("Sorry. We do not have any availability for those dates " +
                                 "with that many people. Please try another reservation.\n");
                           return;
                        }

                      }
                    } finally{}
                  }

                  System.out.println("\nPlease enter the option number for " +
                        "the room you would like ot reserve. To cancel, press 'C'.");
                  
                  sc2 = new Scanner(System.in);
                  String nxt = sc2.next();
                  if (nxt.equals("C")) {
                     return;
                  }
                  String confirmation  = map.get(nxt);
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                  Date ci, co;
                  
                  // Figuring out the date business
                  String BP = baseprices.get(nxt);
                  double total = 0.0;
                  double daily_rate = Double.parseDouble(BP);
                  try {
                     ci = sdf.parse(CI);
                     co = sdf.parse(CO);
                     //System.out.println(ci);
                     //System.out.println(co);
                     Calendar c = Calendar.getInstance();
                     c.setTime(ci);
                     int total_nights = 0;
                     while (c.getTime().compareTo(co) < 0) {
                        int daynum = c.get(Calendar.DAY_OF_WEEK);
                        // if it's a weekend
                        if (daynum == 1 || daynum == 7) {
                           total = total + (daily_rate * 1.1);
                        } else {
                           total = total + daily_rate;
                        }
                        total_nights += 1;
                        c.add(Calendar.DAY_OF_MONTH, 1);
                     }
                     total = total * 1.18; // tourist tax
                                                      
                     System.out.println("\nThank you for booking a room at the Inn!\n");
                     System.out.println("Confirmation:\n" + "Name: " + FN + " " + LN + 
                           "\nCheckIn: " + 
                           (ci).toString().substring(0,10) +  ", " +
                           (ci).toString().substring(24, 28) + " Checkout: " + 
                           (co).toString().substring(0,10) + ", " + 
                           (co).toString().substring(24,28) + 
                           "\nRoom Code: " + confirmation.substring(11,15) + 
                           "\nRoom Name: " + confirmation.substring(23, 48) +
                           "\nBed Type: " + confirmation.substring(56, 63) +
                           "\nAdults: " + NA + " Children: " + NC  + "\nTotal Fees: " 
                           + total + "\n");
                  
                     System.out.println("\nPress 'S' to confirm. Press 'C' to cancel.\n");
                     sc2 = new Scanner(System.in);
                     nxt = sc2.next();
                     if (nxt.equals("C")) {
                        return;
                     }
                     String code = "SELECT MAX(CODE) AS M FROM cnarayan.lab7_reservations";
                     String mc = "10201";
                     try (Statement stmt3 = conn.createStatement()) {
                        ResultSet rs3 =  stmt3.executeQuery(code); 
                        while (rs3.next()) {
                           mc = rs3.getString("M");
                        }

                     }
                     int newcode = Integer.parseInt(mc) + 1;
                     String insert = "INSERT INTO cnarayan.lab7_reservations " +
                        " (CODE, Room, CheckIn, Checkout, Rate, " +
                        "LastName, FirstName, Adults, Kids) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     // Adding the reservation to the table
                     try (PreparedStatement ppps = conn.prepareStatement(insert)) {
                        ppps.setInt(1, newcode);
                        ppps.setString(2, confirmation.substring(11, 15));
                        ppps.setString(3, CI);
                        ppps.setString(4, CO);
                        ppps.setDouble(5, (total / total_nights));
                        ppps.setString(6, LN);
                        ppps.setString(7, FN);
                        ppps.setInt(8, NA);
                        ppps.setInt(9, NC);
                        ppps.executeUpdate();
                        conn.commit();
                        System.out.println("Yor reservation has been booked.\n");
                     } catch (SQLException se){
                          // log exception
                          throw se;
                      }
                     
                     
                     } catch (ParseException e) {
                     e.printStackTrace();
                     }

        } finally{}

      }

    } finally{}

   }
    // connecting to db for fr3
    private void func_req_3() throws SQLException {
        int code = 0;
        String room = null;
        boolean validIn = true;
        boolean validOut = true;
        boolean validBoth = true;
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
                validIn = validCheckin(inDate,room,code);

                if(validIn){
                    if(params.size() > 0){
                        sb.append(", CheckIn = ?");
                    } else {
                        sb.append("CheckIn = ?");
                    }
                    params.add(inDate); 
                } else {
                    System.out.println("Cannot process, date conflict");
                    return;
                }
            } else if("n".equals(checkIn) && !"n".equals(checkOut)) { //only checkout change
                LocalDate outDate = LocalDate.parse(checkOut);
                validOut = validCheckOut(outDate,room,code);

                if(validOut){
                    if(params.size() > 0){
                        sb.append(", Checkout = ?");
                    } else {
                        sb.append("Checkout = ?");
                    }
                    params.add(outDate); 
                } else {
                    System.out.println("Cannot process, date conflict");
                    return;
                }
            } else if(!"n".equals(checkIn) && !"n".equals(checkOut)) { //both changed
                LocalDate inDate = LocalDate.parse(checkIn);
                LocalDate outDate = LocalDate.parse(checkOut);

                validBoth = validBoth(inDate, outDate, room, code);

                if(validBoth) {
                    if(params.size() > 0){
                        sb.append(", Checkout = ?, Checkout = ?");
                    } else {
                        sb.append("Checkout = ?, Checkout = ?");
                    }
                    params.add(inDate);
                    params.add(outDate);
                } else {
                    System.out.println("Error: Dates entered have conflicts, please try again");
                    return;
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
    private boolean validCheckin(LocalDate date, String room, int code) throws SQLException{
        boolean valid = false;
        String check = "select * from lab7_reservations where " +
                        "(checkin >= ? and checkin < (select checkout from lab7_reservations " +
                        "where code = ?)) and room = ? and code <> ?";

        try (Connection conn = DriverManager.getConnection(url, name, pass)){              
            try (PreparedStatement pstmt = conn.prepareStatement(check)) {
                 pstmt.setDate(1, java.sql.Date.valueOf(date));
                 pstmt.setInt(2, code);
                 pstmt.setString(3, room);
                 pstmt.setInt(4, code);

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
    private boolean validCheckOut(LocalDate date, String room, int code) throws SQLException{
        boolean valid = false;
        String check = "select * from lab7_reservations where " + 
                        "(checkin > (select checkin from lab7_reservations " +
                        "where code = ?) and checkin < ?) and room = ? and code <> ?";

        try (Connection conn = DriverManager.getConnection(url, name, pass)){              
            try (PreparedStatement pstmt = conn.prepareStatement(check)) {
                pstmt.setInt(1, code);
                pstmt.setDate(2, java.sql.Date.valueOf(date));
                pstmt.setString(3, room);
                pstmt.setInt(4, code);

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
    private boolean validBoth(LocalDate dateIn, LocalDate dateOut, String room, int code) throws SQLException{
        boolean valid = false;
        String check = "select * from lab7_reservations where " + 
                        "(checkin >= ? and checkout < ?) and room = ? and code <> ?";
        try (Connection conn = DriverManager.getConnection(url, name, pass)){              
            try (PreparedStatement pstmt = conn.prepareStatement(check)) {
                 pstmt.setDate(1, java.sql.Date.valueOf(dateIn));
                 pstmt.setDate(2, java.sql.Date.valueOf(dateOut));
                 pstmt.setString(3, room);
                 pstmt.setInt(4, code);

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
