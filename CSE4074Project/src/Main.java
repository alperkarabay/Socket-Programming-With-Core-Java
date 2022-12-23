
import java.io.IOException;
import java.sql.SQLException;

import static database.PostgreSql.connectDatabase;
import static servers.ActivityServer.createActivityServer;
import static servers.RoomServer.createRoomServer;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        connectDatabase();
        createRoomServer();
        createActivityServer();
    }
}