package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.IOException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String result = null;
		try {
			connection = getConnection();
			stmt = connection.prepareStatement("SELECT keyword, response FROM matching where keyword like concat('%', ?, '%')");
			stmt.setString(1, text);
			rs = stmt.executeQuery();
			while (result == null && rs.next()) {
				result = rs.getString(2);
			}
		}
		catch (Exception e) {
			log.info("Exception: {}", e.toString());
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			} catch (Exception ex) {
				log.info("Exception while closing: {}", ex.toString());
			}
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
