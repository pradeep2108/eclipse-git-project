package com.libraryManagement.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
	  private static Connection con;

	    private DAO() {
	        // Private constructor to prevent instantiation
	    }

	    public static Connection getConnection() throws SQLException, ClassNotFoundException {
	        if (con == null || con.isClosed()) {
	            try {
	                Class.forName("org.postgresql.Driver");
	                con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/LibrarySystem", "postgres", "root");
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	                throw e;
	            } catch (SQLException e) {
	                e.printStackTrace();
	                throw e;
	            }
	        }
	        return con;
	    }
	    
}
