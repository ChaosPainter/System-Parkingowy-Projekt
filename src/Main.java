import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBConnector db = new DBConnector();
        db.connect();

        Scanner scin = new Scanner(System.in);
        int fl = -1;
        do
        {
            System.out.println("Menu:");
            System.out.println("0.Zamknij program.");
            System.out.println("1.Zaloguj się.");
            fl= scin.nextInt();
            if (fl==1)
            {
                System.out.println("Podaj login.");
                String login = scin.next();
                System.out.println("Podaj hasło.");
                String pass = scin.next();

                Logowanie lg = new Logowanie();
                try {
                    lg.loguj(login, pass);
                    //lg.loguj("AC","AC"); //tymczasowe------------------------------------------------------------------1
//                    lg.loguj("admin1","admin1");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        while (fl!=0);
    }
}