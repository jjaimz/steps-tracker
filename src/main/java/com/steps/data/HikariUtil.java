package com.steps.data;

import com.steps.business.Steps;
import com.steps.business.User;
import com.zaxxer.hikari.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.steps.security.PasswordEncrypterService.encryptPassword;

public class HikariUtil {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl( "jdbc:mysql://localhost:3306/steps");
        config.setUsername( "shubh" );
        config.setPassword( "password" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    // READ operation
    public static <T> List<T> fetch(String query) throws SQLException {
        List<T> result = new ArrayList<T>();
        try {
            Connection connection = HikariUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
//            System.out.println(rs.getMetaData());

            if(query.contains("FROM users")) {
                User user = null;

                while (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setAdmin(rs.getInt("admin"));

                    if (user != null) {
                        result.add((T) user);
                    }
                }
            } else if (query.contains("FROM steps")) {
                Steps steps = null;

                while (rs.next()) {
                    steps = new Steps();
                    steps.setId(rs.getInt("id"));
                    steps.setUsers_id(rs.getInt("users_id"));
                    steps.setDate(rs.getString("date"));
                    steps.setSteps(rs.getInt("steps"));
                    steps.setImage(rs.getString("image"));

                    if (steps != null) {
                        result.add((T) steps);
                    }
                }
            }


        }

        catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    // CREATE operation
    public static <T> void insertEntity(T item) {
        try {
            Connection connection = HikariUtil.getConnection();
            PreparedStatement statement = null;
            if (item instanceof User) {

                statement = connection.prepareStatement(
                        "INSERT INTO users (name, email, password, admin) VALUES (?, ?, ?, ?)"
                );

                statement.setString(1, ((User) item).getName());
                statement.setString(2, ((User) item).getEmail());
                statement.setString(3, encryptPassword(((User) item).getPassword()));
                statement.setInt(4, ((User) item).getAdmin());

            } else if (item instanceof Steps) {
                statement = connection.prepareStatement(
                        "INSERT INTO steps (users_id, date, steps, image) VALUES (?, ?, ?, ?)"
                );

                statement.setInt(1, ((Steps) item).getUsers_id());
                statement.setString(2, ((Steps) item).getDate());
                statement.setInt(3, ((Steps) item).getSteps());
                statement.setString(4, ((Steps) item).getImage());
            }

            assert statement != null;
            statement.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // UPDATE operation
    public static <T> void updateEntity(T item) {
        try {
            Connection connection = HikariUtil.getConnection();

            PreparedStatement statement = null;
            if (item instanceof User) {
                statement = connection.prepareStatement(
                        "UPDATE users SET name=?, email=?, password=?, admin=? WHERE id=?"
                );

                statement.setString(1, ((User) item).getName());
                statement.setString(2, ((User) item).getEmail());
                statement.setString(3, ((User) item).getPassword());
                statement.setInt(4, ((User) item).getAdmin());
                statement.setInt(5, ((User) item).getId());

            } else if (item instanceof Steps) {
                statement = connection.prepareStatement(
                        "UPDATE steps SET date=?, steps=?, image=? WHERE id=?"
                );

                statement.setString(1, ((Steps) item).getDate());
                statement.setInt(2, ((Steps) item).getSteps());
                statement.setString(3, ((Steps) item).getImage());
                statement.setInt(4, ((Steps) item).getId());
            }
            assert statement != null;
            statement.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // DELETE users operation
    public static <T> void removeEntity(T item) throws SQLException {
        try {
            Connection connection = HikariUtil.getConnection();

            PreparedStatement statement = null;
            if (item instanceof User) {
                statement = connection.prepareStatement(
                        "DELETE FROM users WHERE id=?"
                );
                statement.setInt(1, ((User) item).getId());

            } else if (item instanceof Steps) {
                statement = connection.prepareStatement(
                        "DELETE FROM steps WHERE id=?"
                );
                statement.setInt(1, ((Steps) item).getId());
            }

            assert statement != null;
            statement.executeUpdate();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static <T> void printList(List<T> records) {
        for (T i : records) {
            System.out.println(i);
        }
    }

    public static boolean userExistsById(int id) {
        String query = "SELECT 1 FROM users WHERE id = ?";
        try (Connection connection = HikariUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }



}
