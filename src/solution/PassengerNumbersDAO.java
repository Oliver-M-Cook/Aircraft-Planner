package solution;
import java.nio.file.Path;
import java.time.LocalDate;
import org.sqlite.*;
import java.sql.*;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {
	ResultSet rs = null;
	Connection conn = null;
	Path path = null;

	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
			
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM PassengerNumbers;");
			
			int numEnt = rs.getInt(1);
			conn.close();
			return numEnt;
			
		}
		catch(SQLException e) {
			System.err.println(e);
		}
		return 0;
		
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		try {
			loadPassengerNumbersData(path);
			
			while(rs.next()) {
				if(rs.getInt("FlightNumber") == flightNumber && date.toString().equals(rs.getString("Date"))) {
					return rs.getInt("LoadEstimate");
				}
			}
		} 
		catch (DataLoadingException | SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {
		path = p;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + p);
			
			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT * FROM PassengerNumbers;");
			
		}
		catch(SQLException se) {
			throw new DataLoadingException(se);
		}

	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		path = null;
	}

}
