package solution;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IRouteDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IScheduler;
import baseclasses.Pilot;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;
import baseclasses.Aircraft.Manufacturer;
import baseclasses.FlightInfo;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {
	//Test with different manufacturer that is not in the enum
	public static void main(String[] args) {	
		IAircraftDAO aircraft = new AircraftDAO();
		ICrewDAO crew = new CrewDAO();
		IRouteDAO route = new RouteDAO();
		IPassengerNumbersDAO passNum = new PassengerNumbersDAO();
		IScheduler scheduler = new Scheduler();
				
		try {
			//Tells your Aircraft DAO to load this particular data file
			aircraft.loadAircraftData(Paths.get("./data/aircraft.csv"));
			crew.loadCrewData(Paths.get("./data/crew.json"));
			route.loadRouteData(Paths.get("./data/routes.xml"));
			passNum.loadPassengerNumbersData(Paths.get("./data/passengernumbers.db"));
			
			Schedule schedule = scheduler.generateSchedule(aircraft, crew, route, passNum, LocalDate.parse("2021-07-01"), LocalDate.parse("2021-08-31"));
			System.out.println(schedule.getRemainingAllocations().size());
			System.out.println(schedule.getCompletedAllocations().size());
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading aircraft data");
			dle.printStackTrace();
		}
	}

}
