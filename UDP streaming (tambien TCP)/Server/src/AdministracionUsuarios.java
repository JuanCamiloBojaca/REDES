import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdministracionUsuarios {
	private Connection conn;

	/**
	 * Connect to a sample database
	 * 
	 * @throws SQLException
	 */
	public void connect() throws SQLException {
		System.out.println("conexion a la base de datos");
		String url = "jdbc:sqlite:./data/db/data.db";
		String sql = "CREATE TABLE IF NOT EXISTS USERS (NAME TEXT NOT NULL PRIMARY KEY, CON TEXT NOT NULL);";
		conn = DriverManager.getConnection(url);
		try (Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public AdministracionUsuarios() throws SQLException {
		connect();
	}

	/**
	 * @param name
	 * @param password
	 * @return if the user and password is in USERS
	 */
	public synchronized boolean login(String name, String password) {
		String sql = "SELECT * FROM USERS WHERE NAME=? AND CON=?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			pstmt.setString(2, password);

			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Add in the table the user
	 * 
	 * @param name
	 * @param password
	 * @return if is possible
	 */
	public synchronized boolean register(String name, String password) {
		String sql = "INSERT INTO USERS (NAME,CON) VALUES (?,?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			pstmt.setString(2, password);

			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}
