package com.example;

import com.example.domain.User;
import com.example.service.UserService;
import org.postgresql.ds.PGSimpleDataSource;


public class App
{
    final public static String SERVER_NAME = "localhost";
    final public static String DATABASE_NAME = "testtask";
    final public static String DB_USERNAME = "postgres";
    final public static String DB_PASSWORD = "";

    public static void main( String[] args )
    {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(SERVER_NAME);
        dataSource.setDatabaseName(DATABASE_NAME);
        dataSource.setUser(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);

        UserService userService = new UserService(dataSource);

        User user = new User();
        user.setProfileName("test");
        user.setName("testName");
        user.setSurname("testSurname");

        userService.createUser(user);
        System.out.println("New user: "+userService.findByProfileName("test").toString());

        userService.updateUserSurname("test", "newTestSurname");
        System.out.println("Updated user: " + userService.findByProfileName("test").toString());

        userService.deleteByProfileName("test");
        System.out.println("User from the table = "+userService.findByProfileName("test"));
    }
}
