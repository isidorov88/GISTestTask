package com.example.service;

import com.example.domain.User;

import javax.sql.DataSource;
import java.sql.*;

public class UserService {
    private DataSource dataSource;

    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean createUser(User user) {
        if (user == null ||
                user.getProfileName() == null ||
                user.getProfileName().isEmpty() ||
                user.getName() == null ||
                user.getName().isEmpty() ||
                !user.getName().chars().allMatch(Character::isLetter) ||
                user.getSurname() == null ||
                user.getSurname().isEmpty() ||
                !user.getSurname().chars().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException();
        }

        User userFromDB = findByProfileName(user.getProfileName());

        String query = "INSERT INTO users (profileName, name, surname) VALUES (?, ?, ?)";

        if (userFromDB == null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, user.getProfileName().trim());
                preparedStatement.setString(2, user.getName().trim());
                preparedStatement.setString(3, user.getSurname().trim());
                preparedStatement.execute();
                System.out.println("New user is created");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("User already exists");
            return false;
        }
    }

    public User findByProfileName(String profileName) {
        if (profileName == null || profileName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        User user = null;
        String query = "SELECT * FROM users WHERE profileName=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

            preparedStatement.setString(1, profileName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.first()) {
                    user = new User();
                    user.setProfileName(resultSet.getString(1));
                    user.setName(resultSet.getString(2));
                    user.setSurname(resultSet.getString(3));
                    return user;
                } else {
                    System.out.println("There's no such user");
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return user;
    }

    public boolean updateUserSurname(String profileName, String newSurname) {

        if (profileName == null || profileName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (newSurname == null || newSurname.isEmpty() || !newSurname.chars().allMatch(Character::isLetter)) {
            throw new IllegalArgumentException();
        }

        String update = "UPDATE users SET surname=? WHERE profileName=?";

        User user = findByProfileName(profileName);

        if (user != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(update)) {
                preparedStatement.setString(1, newSurname.trim());
                preparedStatement.setString(2, profileName);
                preparedStatement.executeUpdate();
                System.out.println("Surname is updated");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("There's no such user to update surname");
            return false;
        }
    }

    public boolean deleteByProfileName(String profileName) {
        if (profileName == null || profileName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String delete = "DELETE FROM users WHERE profileName=?";

        User user = findByProfileName(profileName);
        if (user != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
                preparedStatement.setString(1, profileName);
                preparedStatement.executeUpdate();
                System.out.println("User is deleted");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("There's no such user to delete");
        return false;
    }
}
