import java.sql.ResultSet;
import java.sql.SQLException;

public class Logowanie
{
    public void loguj(String login, String pass) throws SQLException
    {
        boolean fl_log=true;

        ResultSet res_adm = DBExecutor.executeSelect("Select pass, id From admini where login='"+login+"'");
        while(res_adm.next())
        {
            String db_pass = res_adm.getString("pass");
            int db_id= res_adm.getInt("id");
            if (pass.equals(db_pass))
            {
                System.out.println("Twoje id to "+String.valueOf(db_id));
                Admin.admin_menu(db_id);
            }
            fl_log=false;

        }
        if (fl_log) {
            ResultSet res_user = DBExecutor.executeSelect(("Select pass, id From klienci Where login='" + login + "'"));
            while (res_user.next()) {
                String db_pass = res_user.getString("pass");
                int db_id = res_user.getInt("id");
                if (pass.equals(db_pass)) {
                    User.user_menu(db_id);
                }
            }
        }
    }
}
