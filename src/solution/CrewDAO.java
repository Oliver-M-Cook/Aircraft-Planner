package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {
	
	List<Pilot> pilotList = new ArrayList<>();
	List<CabinCrew> ccList = new ArrayList<>();
	List<Crew> crewList = new ArrayList<>();

	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		try {
			//Open the file
			BufferedReader reader = Files.newBufferedReader(p);
			
			//Read the JSON file
			//With the for loops could try a shorter loop to make the code to run quicker by looping through to check if pilots
			String json = "";
			String line = "";
			while((line = reader.readLine()) != null) {
				json = json + line;
			}
			JSONObject root = new JSONObject(json);
			
			try {
				JSONArray pilots = root.getJSONArray("pilots");
				JSONArray cabincrews = root.getJSONArray("cabincrew");
				for(int i = 0; i < pilots.length(); i++) {
					JSONObject pilotJSON = pilots.getJSONObject(i);
					Pilot pilot = new Pilot();
					pilot.setForename(pilotJSON.getString("forename"));
					pilot.setSurname(pilotJSON.getString("surname"));
					pilot.setHomeBase(pilotJSON.getString("home_airport"));
					pilot.setRank(Rank.valueOf(pilotJSON.getString("rank")));
					JSONArray typeRatings = pilotJSON.getJSONArray("type_ratings");
					for(int j = 0; j < typeRatings.length(); j++) {
						pilot.setQualifiedFor(typeRatings.getString(j));
					}
					pilotList.add(pilot);
					crewList.add(pilot);
				}
				for(int i = 0; i < cabincrews.length(); i++) {
					JSONObject ccJSON = cabincrews.getJSONObject(i);
					CabinCrew cabinCrew = new CabinCrew();
					cabinCrew.setForename(ccJSON.getString("forename"));
					cabinCrew.setSurname(ccJSON.getString("surname"));
					cabinCrew.setHomeBase(ccJSON.getString("home_airport"));
					JSONArray typeRatings = ccJSON.getJSONArray("type_ratings");
					for(int j = 0; j < typeRatings.length(); j++) {
						cabinCrew.setQualifiedFor(typeRatings.getString(j));
					}
					ccList.add(cabinCrew);
					crewList.add(cabinCrew);
				}
			}
			catch(JSONException | IllegalArgumentException e) {
				throw new DataLoadingException(e);
			}
			
		}
		catch(IOException | NullPointerException ioe) {
			throw new DataLoadingException(ioe);
		}

	}
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		List<CabinCrew> filteredCC = new ArrayList<>();
		for(CabinCrew cc : ccList) {
			if(cc.getHomeBase().equals(airportCode)) {
				filteredCC.add(cc);
			}
		}
		return filteredCC;
	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<CabinCrew> filteredCC = new ArrayList<>();
		for(CabinCrew cc : ccList) {
			if(cc.isQualifiedFor(typeCode) && cc.getHomeBase().equals(airportCode)) {
				filteredCC.add(cc);
			}
		}
		return filteredCC;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		List<CabinCrew> filteredCC = new ArrayList<>();
		for(CabinCrew cc : ccList) {
			if(cc.isQualifiedFor(typeCode)) {
				filteredCC.add(cc);
			}
		}
		return filteredCC;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		List<Pilot> filteredPilots = new ArrayList<>();
		for(Pilot p : pilotList) {
			if(p.getHomeBase().equals(airportCode)) {
				filteredPilots.add(p);
			}
		}
		return filteredPilots;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<Pilot> filteredPilots = new ArrayList<>();
		for(Pilot p : pilotList) {
			if(p.isQualifiedFor(typeCode) && p.getHomeBase().equals(airportCode)) {
				filteredPilots.add(p);
			}
		}
		return filteredPilots;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		List<Pilot> filteredPilots = new ArrayList<>();
		for(Pilot p : pilotList) {
			if(p.isQualifiedFor(typeCode)) {
				filteredPilots.add(p);
			}
		}
		return filteredPilots;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		return ccList;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		List<Crew> safeCrewList = crewList;
		return safeCrewList;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		List<Pilot> safePilotList = pilotList;
		return safePilotList;
	}

	@Override
	public int getNumberOfCabinCrew() {
		return ccList.size();
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		return pilotList.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		crewList.clear();
		pilotList.clear();
		ccList.clear();

	}

}
