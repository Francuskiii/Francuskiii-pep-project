package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.util.*;
import java.sql.*;


public class SocialMediaDAO {
    public Account insertAccount(Account acc) {
        Connection connection = ConnectionUtil.getConnection();
        try {

            //Check if username is a dupe
            String checkUser = "SELECT COUNT(*) FROM Account WHERE username = ?;";
            PreparedStatement checkStatement = connection.prepareStatement(checkUser);
            checkStatement.setString(1, acc.getUsername());
            ResultSet checkResult = checkStatement.executeQuery();

            if(checkResult.next() && checkResult.getInt(1) > 0) {
                return null;
            }

            //Insert if not dupe
            String sql = "INSERT INTO Account (username, password) VALUES (?, ?);";
            PreparedStatement pStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pStatement.setString(1, acc.getUsername());
            pStatement.setString(2, acc.getPassword());
            pStatement.executeUpdate();

            ResultSet primaryKeyResultSet = pStatement.getGeneratedKeys();
            if(primaryKeyResultSet.next()) {
                int generatedAccountKey = (int) primaryKeyResultSet.getLong(1);
                return new Account(generatedAccountKey, acc.getUsername(), acc.getPassword());
            }
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public Account getAccountInfo(Account acc) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT account_id, username, password FROM Account WHERE username = ? AND password = ?;";
            
            PreparedStatement pStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, acc.getUsername());
            pStatement.setString(2, acc.getPassword());

            ResultSet rs = pStatement.executeQuery();
            if (rs.next()) {
                int accountid = rs.getInt(1);
                return new Account(accountid , acc.getUsername(), acc.getPassword());
            } else {
                return null;
            }
        }catch(SQLException e){
            System.out.println("Error: " + e);
        }
        return null;
    }


    public Message createMessage(Message msg) {
        Connection connection = ConnectionUtil.getConnection();
        try {

            //Check if user exists
            String checkAcc = "SELECT COUNT(*) FROM Account WHERE account_id = ?;";
            PreparedStatement checkStatement = connection.prepareStatement(checkAcc);
            checkStatement.setInt(1, msg.getPosted_by());
            ResultSet checkResult = checkStatement.executeQuery();

            if(checkResult.next() && checkResult.getInt(1) == 0) {
                return null;
            }

            //Insert if not dupe
            String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";
            PreparedStatement pStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pStatement.setInt(1, msg.getPosted_by());
            pStatement.setString(2, msg.getMessage_text());
            pStatement.setLong(3, msg.getTime_posted_epoch());
            pStatement.executeUpdate();

            ResultSet primaryKeyResultSet = pStatement.getGeneratedKeys();
            if(primaryKeyResultSet.next()) {
                int generatedAccountKey = (int) primaryKeyResultSet.getLong(1);
                return new Message(generatedAccountKey, msg.getPosted_by(), msg.getMessage_text(), msg.getTime_posted_epoch());
            }
        } catch(SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public List<Message> getAllMessages() {
        Connection connection = new ConnectionUtil().getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Message msg = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(msg);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return messages;
    }

    public Message getMessageById(int id) {
        Connection connection = new ConnectionUtil().getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?;";
            PreparedStatement pStatement = connection.prepareStatement(sql);
            pStatement.setInt(1, id);
            ResultSet rs = pStatement.executeQuery();
            if (rs.next()) {
                return new Message(
                    rs.getInt("message_id"), 
                    rs.getInt("posted_by"), 
                    rs.getString("message_text"), 
                    rs.getLong("time_posted_epoch")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public Message deleteMessageById(int id) {
        Connection connection = new ConnectionUtil().getConnection();
        Message deletedMessage = null;
    
        try {
            //Retrieve the message first before deleting
            String selectSql = "SELECT * FROM message WHERE message_id = ?;";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
    
            if (rs.next()) {
                deletedMessage = new Message(
                    rs.getInt("message_id"), 
                    rs.getInt("posted_by"), 
                    rs.getString("message_text"), 
                    rs.getLong("time_posted_epoch")
                );
    
                //Delete message
                String deleteSql = "DELETE FROM message WHERE message_id = ?;";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                deleteStatement.setInt(1, id);
                deleteStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    
        return deletedMessage;
    }

    public Message updateMessageById(int id, String newMessageText) {
        Connection connection = new ConnectionUtil().getConnection();
        Message updatedMessage = null;
    
        try {
            //Check if exists
            String selectSql = "SELECT * FROM message WHERE message_id = ?;";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, id);
            ResultSet rs = selectStatement.executeQuery();
    
            //Update the message
            if (rs.next()) {
                String updateSql = "UPDATE message SET message_text = ? WHERE message_id = ?;";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, newMessageText);
                updateStatement.setInt(2, id);
                int rowsAffected = updateStatement.executeUpdate();
    
                if (rowsAffected > 0) {
                    updatedMessage = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        newMessageText,
                        rs.getLong("time_posted_epoch")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    
        return updatedMessage;
    }
    
    public List<Message> getMessagesByAccountId(int accountId) {
        Connection connection = new ConnectionUtil().getConnection();
        List<Message> messages = new ArrayList<>();
    
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?;";
            PreparedStatement pStatement = connection.prepareStatement(sql);
            pStatement.setInt(1, accountId);
            ResultSet rs = pStatement.executeQuery();
    
            while (rs.next()) {
                Message msg = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
                messages.add(msg);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    
        return messages;
    }
    


}
