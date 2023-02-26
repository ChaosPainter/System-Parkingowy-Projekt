import javax.xml.xpath.XPathEvaluationResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class User {

    public static void user_menu(int id) throws SQLException {
        Scanner scin = new Scanner(System.in);
        int fl=-1;
        while (fl!=0) {
            System.out.println("Menu użytkownika:");
            System.out.println("0.Wyloguj się.");
            System.out.println("1.Sprzwdź wolne miejsca parkingowe.");
            System.out.println("2.Zaparkuj samochód.");
            System.out.println("3.Opuść parking.");
            System.out.println("4.Znajdź swój samochód.");
            System.out.println("5.Sprawdź stan swojego rachunku.");
            fl= scin.nextInt();
            
            switch (fl)
            {
                case 1: wolne_miejsca();break;
                case 2: zaparkuj(id);break;
                case 3: wyparkuj(id);break;
                case 4: znajdz(id);break;
                case 5: sprawdz_balance(id);break;
            }
        }

    }

    private static void sprawdz_balance(int id) throws SQLException {

        //nie trzeba sprawdzać czy użytkownik istnieje bo id nie jest wprowadzane ręcznie
        ResultSet res1=DBExecutor.executeSelect("Select balance from Klienci where id="+String.valueOf(id));
        double balance=-1;
        while(res1.next())
        {
             balance = res1.getDouble("balance");
        }

        System.out.println("Twoja należność to "+String.valueOf(balance)+" pln");

    }

    private static void znajdz(int id) throws SQLException {
        System.out.println("Podaj numer rejscracyjny pojazdu");
        Scanner scin = new Scanner(System.in);
        //scin.nextLine();
        String rej = scin.nextLine().trim();

        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from samochody where id_klienta="+String.valueOf(id)+" and rejstracja='"+rej+"';");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Samochód o podanym numerze rejstracyjnym nie należy do ciebie");
            return;
        }

        int id_sam = -1;
        ResultSet res_sam = DBExecutor.executeSelect("Select id from Samochody where rejstracja='"+rej+"'");
        while (res_sam.next()) {
            id_sam = res_sam.getInt("id");
        }
        if (id_sam == -1) {
            System.out.println("samochód nie istnieje");
            return;
        }

        ResultSet res_parkowania = DBExecutor.executeSelect("Select miejsca.id, pietro from Parkowania inner join miejsca on miejsca.id=parkowania.id_miejsca where id_samochodu=" + String.valueOf(id_sam) + " AND leave_date is NULL");
        int pietro = -1;
        int id_miejsca = -1;

        while (res_parkowania.next()) {
            id_miejsca = res_parkowania.getInt("id");
            pietro = res_parkowania.getInt("pietro");
        }

        if (id_miejsca==-1)
        {
            System.out.println("Samochód nie jest zaparkowany");
            return;
        }
        System.out.println("Samochód stoi na miejscu "+String.valueOf(id_miejsca)+" na poziomie "+String.valueOf(pietro));


    }

    private static void wyparkuj(int id) throws SQLException {
        System.out.println("Podaj numer rejstracyjny pojazdu");
        Scanner scin = new Scanner(System.in);
        //scin.nextLine();
        String rej = scin.nextLine().trim();



        int id_sam=-1;
        ResultSet res_sam=DBExecutor.executeSelect("Select id from Samochody where rejstracja='"+rej+"'");
        while(res_sam.next())
        {
            id_sam=res_sam.getInt("id");
        }
        if (id_sam==-1)
        {
            System.out.println("Samochód nie istnieje.");
            return;
        }

        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from samochody where id_klienta="+String.valueOf(id)+" and rejstracja='"+rej+"';");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Samochód o podanym numerze rejstracyjnym nie należy do ciebie");
            return;
        }

        ResultSet res_parkowania = DBExecutor.executeSelect("Select id, id_miejsca, enter_date from Parkowania where id_samochodu="+String.valueOf(id_sam)+" AND leave_date is NULL");
        int id_park=-1;
        int id_miejsca=-1;
        String enter_date="";

        while(res_parkowania.next())
        {
            id_miejsca= res_parkowania.getInt("id_miejsca");
            id_park = res_parkowania.getInt("id");
            enter_date=res_parkowania.getString("enter_date");
        }
        if (id_miejsca==-1)
        {
            System.out.println("Samochód nie jest zaparkowany");
            return;
        }
         DBExecutor.executeQuery("Update miejsca set stan=1 where id="+String.valueOf(id_miejsca));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String data = now.format(dtf);
        DBExecutor.executeQuery("Update Parkowania set leave_date='"+data+"' where id="+String.valueOf(id_park));
        LocalDateTime then = LocalDateTime.parse(enter_date,dtf);
        Duration diff = Duration.between(then, now);
        int hours= (int)diff.toHours();
        int id_kli=-1;
        ResultSet res_kli = DBExecutor.executeSelect("Select id_klienta from Samochody Where id="+String.valueOf(id_sam));
        while(res_kli.next()){
            id_kli=res_kli.getInt("id_klienta");
        }
        if (id_kli==-1)
        {
            System.out.println("coś poszło nie tak");
            return;
        }

        DBExecutor.executeQuery("update Klienci set balance=balance+"+String.valueOf(hours*3)+" Where id="+String.valueOf(id_kli));
    }

    private static void zaparkuj(int id) throws SQLException {
        System.out.println("Podaj numer rejscracyjny pojazdu");
        Scanner scin = new Scanner(System.in);
        //scin.nextLine();
        String rej = scin.nextLine().trim();

        int id_sam=-1;
        ResultSet res_sam=DBExecutor.executeSelect("Select id from Samochody where rejstracja='"+rej+"'");
        while(res_sam.next())
        {
            id_sam=res_sam.getInt("id");
        }
        if (id_sam==-1)
        {
            System.out.println("Taki samochód nie istnieje");
            return;
        }

        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from samochody where id_klienta="+String.valueOf(id)+" and rejstracja='"+rej+"';");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Samochód o podanym numerze rejstracyjnym nie należy do ciebie");
            return;
        }

        int fl2=-1;
        ResultSet res_join = DBExecutor.executeSelect("Select from parkowania where id_samochodu="+String.valueOf(id_sam)+" and leave_date is null");
        while (res_join.next())
        {
            fl2=1;
        }
        if (fl2==1)
        {
            System.out.println("Samochód jest już zaparkowany.");
            return;
        }

        ResultSet res2= DBExecutor.executeSelect("Select id , pietro From Miejsca Where stan=1 order by id limit 1");
        int pietro=-1;
        int id_miejsca=-1;

        while(res2.next())
        {
           id_miejsca= res2.getInt("id");
           pietro=res2.getInt("pietro");
        }
        if (id_miejsca!=-1){
            System.out.println("Zaparkuj samochód na miejscu "+String.valueOf(id_miejsca)+" na poziomie "+String.valueOf(pietro));
            DBExecutor.executeQuery("Update Miejsca set stan=0 where id="+String.valueOf(id_miejsca));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String data = now.format(dtf);
            DBExecutor.executeQuery("Insert into Parkowania(id_samochodu,enter_date, id_miejsca) values("+String.valueOf(id_sam)+",'"+data+"',"+String.valueOf(id_miejsca)+");");
        }
        else {
            System.out.println("brak wolnych miejsc");
            return;
        }
    }
    private static void wolne_miejsca() throws SQLException {
        ResultSet res1= DBExecutor.executeSelect("Select id , pietro From Miejsca Where stan=1");
        while(res1.next())
        {
            System.out.println("Miejsce "+res1.getString("id")+", poziom "+res1.getString("pietro"));
        }

    }
}
