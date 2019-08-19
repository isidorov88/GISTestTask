package com.example;

import com.example.domain.User;
import com.example.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private DataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;


    private UserService userService;

    private User user;

    @Before
    public void setUp() throws SQLException {
        assertNotNull(mockDataSource);

        user = new User();
        user.setProfileName("test");
        user.setName("testName");
        user.setSurname("testSurname");

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerName(App.SERVER_NAME);
        dataSource.setDatabaseName(App.DATABASE_NAME);
        dataSource.setUser(App.DB_USERNAME);
        dataSource.setPassword(App.DB_PASSWORD);

        userService = new UserService(dataSource);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockResultSet.first()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn(user.getProfileName());
        when(mockResultSet.getString(2)).thenReturn(user.getName());
        when(mockResultSet.getString(3)).thenReturn(user.getSurname());
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.execute()).thenReturn(true);
    }

    @Test
    public void createUserWithMockDataSource() throws SQLException {
        when(mockResultSet.first()).thenReturn(false);

        new UserService(mockDataSource).createUser(user);

        verify(mockDataSource, times(2)).getConnection();
        verify(mockConnection, times(1)).prepareStatement(anyString(), anyInt(), anyInt());
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockConnection, times(2)).close();
        verify(mockPreparedStatement, times(4)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockPreparedStatement, times(1)).execute();
        verify(mockPreparedStatement, times(2)).close();
        verify(mockResultSet, times(1)).first();
        verify(mockResultSet, times(1)).close();
    }

    @Test
    public void findByProfileNameWithMockDataSource() throws SQLException {
        new UserService(mockDataSource).findByProfileName(user.getProfileName());

        verify(mockDataSource, times(1)).getConnection();
        verify(mockConnection, times(1)).prepareStatement(anyString(), anyInt(), anyInt());
        verify(mockConnection, times(1)).close();
        verify(mockPreparedStatement, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockPreparedStatement, times(1)).close();
        verify(mockResultSet, times(1)).first();
        verify(mockResultSet, times(3)).getString(anyInt());
        verify(mockResultSet, times(1)).close();
    }

    @Test
    public void deleteByProfileNameWithMockDataSource() throws SQLException {
        when(mockResultSet.first()).thenReturn(false).thenReturn(true);

        UserService userService = new UserService(mockDataSource);
        userService.createUser(user);
        userService.deleteByProfileName(user.getProfileName());

        verify(mockDataSource, times(4)).getConnection();
        verify(mockConnection, times(2)).prepareStatement(anyString(), anyInt(), anyInt());
        verify(mockConnection, times(2)).prepareStatement(anyString());
        verify(mockConnection, times(4)).close();
        verify(mockPreparedStatement, times(6)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(2)).executeQuery();
        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockPreparedStatement, times(1)).execute();
        verify(mockPreparedStatement, times(4)).close();
        verify(mockResultSet, times(2)).first();
        verify(mockResultSet, times(3)).getString(anyInt());
        verify(mockResultSet, times(2)).close();
    }

    @Test
    public void updateSurnameByProfileNameWithMockDataSource() throws SQLException {
        UserService userService = new UserService(mockDataSource);
        userService.updateUserSurname(user.getProfileName(), "newTestName");

        verify(mockDataSource, times(2)).getConnection();
        verify(mockConnection, times(1)).prepareStatement(anyString(), anyInt(), anyInt());
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockConnection, times(2)).close();
        verify(mockPreparedStatement, times(3)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockPreparedStatement, times(2)).close();
        verify(mockResultSet, times(1)).first();
        verify(mockResultSet, times(3)).getString(anyInt());
        verify(mockResultSet, times(1)).close();
    }

    @Test
    public void getNullWithMockDataSource() throws SQLException {
        when(mockResultSet.first()).thenReturn(false);
        UserService userService = new UserService(mockDataSource);
        User userFromDB = userService.findByProfileName("nullUser");
        assertNull(userFromDB);
    }



    @Test(expected = IllegalArgumentException.class)
    public void nullDeleteThrowsException() {
        UserService userService = new UserService(mockDataSource);
        userService.deleteByProfileName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyDeleteThrowsException() {
        UserService userService = new UserService(mockDataSource);
        userService.deleteByProfileName("");
    }




    @Test(expected = IllegalArgumentException.class)
    public void nullCreateThrowsException() {
        UserService userService = new UserService(mockDataSource);
        userService.createUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyProfileNameCreateThrowsException() {
        user.setProfileName("");
        new UserService(mockDataSource).createUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullProfileNameCreateThrowsException() {
        user.setProfileName(null);
        new UserService(mockDataSource).createUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyNameCreateThrowsException() {
        user.setName("");
        new UserService(mockDataSource).createUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullNameCreateThrowsException() {
        user.setName(null);
        new UserService(mockDataSource).createUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notLettersNameCreateThrowsException() {
        user.setName("12321");
        new UserService(mockDataSource).createUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptySurnameCreateThrowsException() {
        user.setSurname("");
        new UserService(mockDataSource).createUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSurnameCreateThrowsException() {
        user.setSurname(null);
        new UserService(mockDataSource).createUser(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notLettersSurnameCreateThrowsException() {
        user.setSurname("12321");
        new UserService(mockDataSource).createUser(user);
    }



    @Test(expected = IllegalArgumentException.class)
    public void nullProfileNameUpdateSurnameThrowsException(){
        new UserService(mockDataSource).updateUserSurname(null, "newTestSurname");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyProfileNameUpdateSurnameThrowsException(){
        new UserService(mockDataSource).updateUserSurname("", "newTestSurname");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUpdateSurnameThrowsException() {
        new UserService(mockDataSource).updateUserSurname("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyUpdateSurnameThrowsException() {
        new UserService(mockDataSource).updateUserSurname("test", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notLettersUpdateSurnameThrowsException() {
        new UserService(mockDataSource).updateUserSurname("test", "12321");
    }



    @Test
    public void createUser() {
        boolean isUserCreated = userService.createUser(user);
        User userFromDB = userService.findByProfileName(user.getProfileName());

        assertTrue(isUserCreated);
        assertEquals(user, userFromDB);

        userService.deleteByProfileName(user.getProfileName());
    }

    @Test
    public void updateUserSurname() {
        userService.createUser(user);
        boolean isSurnameChanged = userService.updateUserSurname(user.getProfileName(), "newTestSurname");
        User userFromDB = userService.findByProfileName(user.getProfileName());

        assertTrue(isSurnameChanged);
        assertNotEquals(user.getSurname(), userFromDB.getSurname());

        userService.deleteByProfileName(user.getProfileName());
    }

    @Test
    public void deleteByProfileName() {
        userService.createUser(user);
        boolean isUserDeleted = userService.deleteByProfileName(user.getProfileName());
        User userFromDB = userService.findByProfileName(user.getProfileName());

        assertTrue(isUserDeleted);
        assertNull(userFromDB);
    }

    @Test
    public void findByProfileName(){
        userService.createUser(user);
        User userFromDB = userService.findByProfileName(user.getProfileName());

        assertNotNull(userFromDB);
        assertEquals(user, userFromDB);

        userService.deleteByProfileName(user.getProfileName());
    }

    @Test
    public void getNull(){
        User userFromDB = userService.findByProfileName("nullUser");

        assertNull(userFromDB);
    }


}
