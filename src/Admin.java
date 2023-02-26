import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Admin {
    public static void admin_menu(int id) throws SQLException{
        Scanner scin = new Scanner(System.in);
        int fl=-1;

        while (fl!=0) {
            System.out.println("\nMenu Administratora:");
            System.out.println("0.Wyloguj się.");
            System.out.println("1.Dodaj klienta.");//
            System.out.println("2.Usuń klienta.");//
            System.out.println("3.Edytuj dane klienta.");//
            System.out.println("4.Dodaj samochód.");//
            System.out.println("5.Usuń samochód.");
            System.out.println("6.Zaparkuj samochód.");
            System.out.println("7.Usuń samochód z parkingu.");
            System.out.println("8.Wyświetl zaparkowane samochody.");
            System.out.println("9.Wyświetl informacje o klientach.");
            System.out.println("10.Przyjmij opłatę.");
            System.out.println("11.Wyświetl wszystkie samochody.");
            System.out.println("12.Wyświetl miejsca parkingowe.");
            fl=scin.nextInt();
            switch (fl)
            {
                case 1:
                    add_client();
                    break;
                case 2:
                    System.out.println("Podaj id usuwanego klienta.");
                    int id_del= scin.nextInt();
                    remove_client(id_del);
                    break;
                case 3:
                    System.out.println("Podaj id edytowanego klienta.");
                    int id_up= scin.nextInt();
                    update_client(id_up);
                    break;
                case 4:
                    add_car();
                    break;
                case 5:
                    System.out.println("Podaj id usuwanego pojazdu");
                    int id_c= scin.nextInt();
                    delete_car(id_c);
                    break;
                case 6:
                    parkuj();
                    break;
                case 7:
                    wyparkuj();
                    break;

                case 8:
                    print_samochody_park();
                    break;

                case 9:
                    print_klienci();
                    break;

                case 10:
                    opłata();
                    break;
                case 11:
                    print_samochody();
                    break;
                case 12:
                    print_miejsca();
                    break;
            }


        }
    }

    private static void print_miejsca() throws SQLException {
        ResultSet res = DBExecutor.executeSelect("Select * from miejsca order by id");
        System.out.println("id || stan || poziom ");
        while(res.next())
        {
            String id = res.getString("id");
            String stan="???";
            int st=res.getInt("stan");
            if (st==0)
            {
                stan="zajęte";
            }
            else {
                if (st == 1) {
                    stan = "wolne";
                }
            }
            String poziom = res.getString("pietro");

            System.out.println(id+" || "+stan+" || "+poziom);
        }
    }

    private static void print_samochody() throws SQLException {
        ResultSet res8=DBExecutor.executeSelect("Select * From samochody");
        System.out.println("Lista ");
        System.out.println("id || id_klienta || marka || model || rejstracja");
        while (res8.next()){
            System.out.println(String.valueOf(res8.getInt("id"))+" || "+String.valueOf(res8.getInt("id_klienta")+" || "+res8.getString("marka")+" || "+res8.getString("model")+" || "+res8.getString("rejstracja")));
        }
    }

    private static void opłata() throws SQLException {
        Scanner scin = new Scanner(System.in);
        System.out.println("Podaj id klienta");
        int id_kl= scin.nextInt();

        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from klienci where id="+String.valueOf(id_kl)+";");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Użytkownik o podanym id nie istnieje.");
            return;
        }

        System.out.println("Podaj kwotę");
        double kwota= scin.nextDouble();
        kwota=kwota*(-1);
        DBExecutor.executeQuery("Update Klienci Set balance=balance+"+String.valueOf(kwota)+" Where id="+String.valueOf(id_kl));
    }

    private static void print_klienci() throws SQLException {
        ResultSet res9 = DBExecutor.executeSelect("Select * From Klienci");
        System.out.println("Lista klientów");
        System.out.println("id || imię || nazwisko || login || hasło || do zapłaty ");
        while (res9.next())
        {
            String id_klienta = String.valueOf(res9.getInt("id"));
            String imie = res9.getString("imie");
            String nazwisko=res9.getString("nazwisko");
            String login = res9.getString("login");
            String password = res9.getString("pass");
            String balance = String.valueOf(res9.getDouble("balance"));
            System.out.println(id_klienta+" || "+imie+" || "+nazwisko+" || "+login+" || "+password+" || "+balance);
        }
    }

    private static void print_samochody_park() throws SQLException {
        ResultSet res8=DBExecutor.executeSelect("Select * from Samochody inner join Parkowania on Parkowania.id_samochodu=Samochody.id where leave_date is null");
        System.out.println("id || id_klienta || marka || model || rejstracja || miejsce || data parkowania");
        while (res8.next()){
            System.out.println(String.valueOf(res8.getInt("id"))+" || "+String.valueOf(res8.getInt("id_klienta")+" || "+res8.getString("marka")+" || "+res8.getString("model")+" || "+res8.getString("rejstracja")+" || "+res8.getString("id_miejsca")+" || "+res8.getString("enter_date")));
        }
    }

    private static void wyparkuj() throws SQLException {
        Scanner scin = new Scanner(System.in);
        System.out.println("Podaj id pojazdu");
        int id_poj=scin.nextInt();

        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from samochody where id="+String.valueOf(id_poj)+";");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Samochód o podanym id nie istnieje.");
            return;
        }

        int fl2=-1;
        ResultSet res2=DBExecutor.executeSelect("Select * from parkowania where id_samochodu="+String.valueOf(id_poj)+" AND leave_date is null;");
        while (res2.next())
        {
            fl2=1;
        }
        if (fl2==-1)
        {
            System.out.println("Samochód o podanym id nie jest zaparkowany.");
            return;
        }

        ResultSet res7 = DBExecutor.executeSelect("Select id ,id_miejsca from Parkowania where id_samochodu='"+String.valueOf(id_poj)
                +"' AND leave_date is NULL");
        int id_miejsca=-1;
        int id_parkowania=-1;
        while (res7.next())
        {
            id_parkowania=res7.getInt("id");
            id_miejsca=res7.getInt("id_miejsca");
        }
        DBExecutor.executeQuery("Update Miejsca set stan=1 where id="+String.valueOf(id_parkowania));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String data = now.format(dtf);
        DBExecutor.executeQuery("Update Parkowania set leave_date='"+data+"' where id="+String.valueOf(id_parkowania));
    }

    private static void parkuj() throws SQLException {
        Scanner scin = new Scanner(System.in);
        System.out.println("Podaj id samochodu");
        int car_id= scin.nextInt();

        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from samochody where id="+String.valueOf(car_id)+";");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Samochód o podanym id nie istnieje.");
            return;
        }

        int fl2=-1;
        ResultSet res2=DBExecutor.executeSelect("Select * from parkowania where id_samochodu="+String.valueOf(car_id)+" AND leave_date is null;");
        while (res2.next())
        {
            fl2=1;
        }
        if (fl2==1)
        {
            System.out.println("Samochód o podanym id jest już zaparkowany.");
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String data = now.format(dtf);
        String leave_date ="null";
        System.out.println("Podaj id miejsca parkingowego");
        int id_park= scin.nextInt();
        DBExecutor.executeQuery("Insert Into Parkowania (id_samochodu,enter_date,id_miejsca) Values ("
                +car_id+",'"+data+"',"+id_park+");");
        DBExecutor.executeQuery("Update Miejsca set stan = 0 Where id="+String.valueOf(id_park)+";");
    }

    private static void delete_car(int id) throws SQLException { //powinno usuwać zaparkowanie
        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from samochody where id="+String.valueOf(id)+";");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Samochód o podanym id nie istnieje.");
            return;
        }
        DBExecutor.executeQuery("Delete From Samochody Where id="+String.valueOf(id));
    }

    private static void update_client(int id) throws SQLException {
        Scanner scin = new Scanner(System.in);
        System.out.println("Podaj Imie");
        String imie =  scin.next();
        System.out.println("Podaj Nazwisko");
        String nazwisko = scin.next();
        System.out.println("Podaj login");
        String login = scin.next();
        System.out.println("Podaj hasło");
        String password = scin.next();

        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from klienci where id="+String.valueOf(id)+";");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Użytkownik o podanym id nie istnieje.");
            return;
        }

        int fl2=-1;
        ResultSet res2=DBExecutor.executeSelect("Select * from klienci where login='"+login+"';");
        while (res2.next())
        {
            fl2=1;
        }
        if (fl2==1)
        {
            System.out.println("Użytkownik o podanym loginie już istnieje.");
            return;
        }

        DBExecutor.executeQuery("Update Klienci Set imie='"+imie+"',nazwisko='"+nazwisko+"',login='"+login+"',pass='"+password+"' Where id="+String.valueOf(id));

    }

    private static void add_car() throws SQLException {
        Scanner scin = new Scanner(System.in);
        System.out.println("Podaj id właściciela");
        int own_id =  scin.nextInt();
        System.out.println("Podaj rejstracje");
        scin.nextLine();
        String rejstracja = scin.nextLine().trim();
        System.out.println("Podaj markę");
        String marka = scin.next();
        System.out.println("Podaj model");
        String model = scin.next();


        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from samochody where rejstracja='"+rejstracja+"';");
        while (res.next())
        {
            fl=1;
        }
        if (fl==1)
        {
            System.out.println("Samochód o podanym numerze rejstracyjnym już istnieje");
            return;
        }

        DBExecutor.executeQuery("Insert Into Samochody (id_klienta, marka, model, rejstracja) Values " +
                "("+String.valueOf(own_id)+",'"+marka+"','"+model+"','"+rejstracja+"')");
    }


    public static void add_client() throws SQLException {
        Scanner scin = new Scanner(System.in);
        System.out.println("Podaj Imie");
        String imie =  scin.next();
        System.out.println("Podaj Nazwisko");
        String nazwisko = scin.next();
        System.out.println("Podaj login");
        String login = scin.next().trim();
        System.out.println("Podaj hasło");
        String password = scin.next().trim();

//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        String data = now.format(dtf);
        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from klienci where login='"+login+"';");
        while (res.next())
        {
            fl=1;
        }
        if (fl==1)
        {
            System.out.println("Użytkownik o podanym loginie już istnieje.");
            return;
        }

        DBExecutor.executeQuery("Insert Into Klienci (imie,nazwisko, login,pass,balance) " +
                "Values('"+imie+"','"+nazwisko+"','"+login+"','"+password+"',0)");
        System.out.println("Dodano użytkownika.");

    }
    public static void remove_client(int id) throws SQLException { //powinno jeszcze usuwać samochód i zaparkowanie.
        int fl=-1;
        ResultSet res=DBExecutor.executeSelect("Select * from klienci where id="+String.valueOf(id)+";");
        while (res.next())
        {
            fl=1;
        }
        if (fl==-1)
        {
            System.out.println("Użytkownik o podanym id nie istnieje.");
            return;
        }
        String id_string = String.valueOf(id);
        DBExecutor.executeQuery("Delete From Klienci Where id="+id_string);

        System.out.println("Usunięto uzytkownika.");
    }


}
