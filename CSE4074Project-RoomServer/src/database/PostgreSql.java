package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class PostgreSql {

    public static void connectDatabase() throws SQLException{
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "admin", "admin");

            System.out.println("Opened postgresql database successfully");
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public static void createDatabase() throws SQLException {
        Connection c = null;
        Statement stmt = null;
        try {
            connectDatabase();

        stmt = c.createStatement();
        //lines below create tables so they must be executed once so we turned them into comment
       /* String roomTableSql = "CREATE TABLE SALLALLALALA " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL )";
        stmt.executeUpdate(roomTableSql);*/
     /*   String reservationTableSql = "CREATE TABLE RESERVATIONS " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                "ROOM INT NOT NULL," +
                " DAY  INT NOT NULL," +
                "HOUR INT NOT NULL," +
                "DURATION INT NOT NULL )";*/
      //  stmt.executeUpdate(roomTableSql);
    //    stmt.executeUpdate(reservationTableSql);
        stmt.close();
        c.close();

        } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        System.exit(0);
    }
      System.out.println("Tables created successfully");
    }

}
