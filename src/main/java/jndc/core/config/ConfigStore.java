package jndc.core.config;

import jndc.utils.ApplicationExit;
import jndc.utils.LogPrint;


import java.sql.*;

public class ConfigStore {
    public Connection getConnection(String filePath){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(filePath);
        } catch ( Exception e ) {
            e.printStackTrace();
            LogPrint.err(e);
            ApplicationExit.exit();
        }
        return c;
    }


    public void query(){
        Connection connection = getConnection("jdbc:sqlite:C:\\Users\\刘启威\\Desktop\\sqlite-dll-win64-x64-3330000\\tt.db");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from userInfo");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                LogPrint.info(id+":"+name);


            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LogPrint.err(throwables);
            ApplicationExit.exit();
        }
    }

    public static void main(String[] args) {
        new ConfigStore().query();
    }
}
