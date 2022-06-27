package solution;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.xml.sax.*;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {
	
	List<Route> routeList = new ArrayList<>();
	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		List<Route> filteredRoutes = new ArrayList<>();
		for(Route r : routeList) {
			if(r.getDayOfWeek().equals(dayOfWeek)) {
				filteredRoutes.add(r);
			}
		}
		return filteredRoutes;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to searh for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		List<Route> filteredRoutes = new ArrayList<>();
		for(Route r : routeList) {
			if(r.getDepartureAirportCode().equals(airportCode) && r.getDayOfWeek().equals(dayOfWeek)) {
				filteredRoutes.add(r);
			}
		}
		return filteredRoutes;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		List<Route> filteredRoutes = new ArrayList<>();
		for(Route r : routeList) {
			if(r.getDepartureAirportCode().equals(airportCode)) {
				filteredRoutes.add(r);
			}
		}
		return filteredRoutes;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		String day = date.getDayOfWeek().toString().substring(0, 3);
		List<Route> filteredRoutes = new ArrayList<>();
		for(Route r : routeList) {
			if(r.getDayOfWeek().toUpperCase().equals(day)) {
				filteredRoutes.add(r);
			}
		}
		return filteredRoutes;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		List<Route> safeRouteList = routeList;
		return safeRouteList;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		return routeList.size();
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path arg0) throws DataLoadingException {
		
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(arg0.toString());
			
			Element root = doc.getDocumentElement();
			NodeList children = root.getChildNodes();
			
			for(int i = 0; i < children.getLength(); i++) {
				Node c = children.item(i);
				if(c.getNodeName().equals("Route")) {
					Route route = new Route();
					NodeList grandchildren = c.getChildNodes();
					for(int j = 0; j < grandchildren.getLength(); j++) {
						Node d = grandchildren.item(j);
						try {
							if(d.getNodeName().equals("FlightNumber")) {
								int flightNum = Integer.parseInt(d.getChildNodes().item(0).getNodeValue());
								route.setFlightNumber(flightNum);
							}
							else if(d.getNodeName().equals("DayOfWeek")) {
								String dayOfWeek = d.getChildNodes().item(0).getNodeValue();
								route.setDayOfWeek(dayOfWeek);
							}
							else if(d.getNodeName().equals("DepartureTime")) {
								LocalTime departureTime = LocalTime.parse(d.getChildNodes().item(0).getNodeValue());
								route.setDepartureTime(departureTime);
							}
							else if(d.getNodeName().equals("DepartureAirport")) {
								String departureAirport = d.getChildNodes().item(0).getNodeValue();
								route.setDepartureAirport(departureAirport);
							}
							else if(d.getNodeName().equals("DepartureAirportIATACode")) {
								String depAirCode = d.getChildNodes().item(0).getNodeValue();
								route.setDepartureAirportCode(depAirCode);
							}
							else if(d.getNodeName().equals("ArrivalTime")) {
								LocalTime arrTime = LocalTime.parse(d.getChildNodes().item(0).getNodeValue());
								route.setArrivalTime(arrTime);
							}
							else if(d.getNodeName().equals("ArrivalAirport")) {
								String arrAirport = d.getChildNodes().item(0).getNodeValue();
								route.setArrivalAirport(arrAirport);
							}
							else if(d.getNodeName().equals("ArrivalAirportIATACode")) {
								String arrAirCode = d.getChildNodes().item(0).getNodeValue();
								route.setArrivalAirportCode(arrAirCode);
							}
							else if(d.getNodeName().equals("Duration")) {
								Duration duration = Duration.parse(d.getChildNodes().item(0).getNodeValue());
								route.setDuration(duration);
							}
						}
						catch(NumberFormatException | DOMException | DateTimeParseException e) {
							throw new DataLoadingException(e);
						}
						
					}
					routeList.add(route);
				}
			}
		}
		catch(ParserConfigurationException | SAXException | IOException | NullPointerException e) {
			throw new DataLoadingException(e);
		}

	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		routeList.clear();

	}

}
