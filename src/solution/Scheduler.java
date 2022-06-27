package solution;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;
import baseclasses.Route;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;
import baseclasses.Aircraft;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.CabinCrew;
import baseclasses.Crew;

public class Scheduler implements IScheduler {
	//105 million is Kris' score
	@Override
	public Schedule generateSchedule(IAircraftDAO arg0, ICrewDAO arg1, IRouteDAO arg2, IPassengerNumbersDAO arg3,
			LocalDate arg4, LocalDate arg5) {
		
		Schedule schedule = new Schedule(arg2, arg4, arg5);
		
		for(FlightInfo f : schedule.getRemainingAllocations()) {
			for(Aircraft a : arg0.getAllAircraft()) {
				try {
					if(a.getStartingPosition() == f.getFlight().getDepartureAirportCode()) {
						schedule.allocateAircraftTo(a, f);
						a.setStartingPosition(f.getFlight().getArrivalAirportCode());
						break;
					}
				}
				catch(DoubleBookedException e) {
					//Leave this blank for now
				}
			}
			
			if(schedule.getAircraftFor(f) == null) {
				for(Aircraft a : arg0.getAllAircraft()) {
					try {
						schedule.allocateAircraftTo(a, f);
						a.setStartingPosition(f.getFlight().getArrivalAirportCode());
						break;
					}
					catch(DoubleBookedException e) {
						//Leave this blank for now
					}
				}
			}
			
			for(Pilot p : arg1.getAllPilots()) {
				try {
					if(p.isQualifiedFor(schedule.getAircraftFor(f)) && p.getRank() == Rank.CAPTAIN) {
						schedule.allocateCaptainTo(p, f);
						p.setHomeBase(f.getFlight().getArrivalAirportCode());
						break;
					}
				}
				catch(DoubleBookedException e) {
					//Leave this blank for now
				}
			}
			
			if(schedule.getCaptainOf(f) == null) {
				for(Pilot p : arg1.findPilotsByHomeBase(f.getFlight().getDepartureAirportCode())) {
					try {
						if(p.getRank() == Rank.CAPTAIN) {
							schedule.allocateCaptainTo(p, f);
							p.setHomeBase(f.getFlight().getArrivalAirportCode());
							break;
						}
					}
					catch(DoubleBookedException e) {
						//Leave this blank for now
					}
				}
			}
			
			if(schedule.getCaptainOf(f) == null) {
				for(Pilot p : arg1.getAllPilots()) {
					try {
						if(p.getRank() == Rank.CAPTAIN) {
							schedule.allocateCaptainTo(p, f);
							p.setHomeBase(f.getFlight().getArrivalAirportCode());
							break;
						}
					}
					catch(DoubleBookedException e) {
						//Leave this blank for now
					}
				}
			}
			
			if(schedule.getCaptainOf(f) == null) {
				for(Pilot p : arg1.getAllPilots()) {
					try {
						schedule.allocateCaptainTo(p, f);
						p.setHomeBase(f.getFlight().getArrivalAirportCode());
						break;
					}
					catch(DoubleBookedException e) {
						//Leave this blank for now
					}
				}
			}
			
			for(Pilot p : arg1.getAllPilots()) {
				try {
					if(p.isQualifiedFor(schedule.getAircraftFor(f))) {
						schedule.allocateFirstOfficerTo(p, f);
						p.setHomeBase(f.getFlight().getArrivalAirportCode());
						break;
					}
				}
				catch(DoubleBookedException e) {
					//Leave this blank for now
				}
			}
			
			if(schedule.getFirstOfficerOf(f) == null) {
				for(Pilot p : arg1.findPilotsByHomeBase(f.getFlight().getDepartureAirportCode())) {
					try {
						schedule.allocateFirstOfficerTo(p, f);
						p.setHomeBase(f.getFlight().getArrivalAirportCode());
						break;
					}
					catch(DoubleBookedException e) {
						//Leave this blank for now
					}
				}
			}
			
			if(schedule.getFirstOfficerOf(f) == null) {
				for(Pilot p : arg1.getAllPilots()) {
					try {
						schedule.allocateFirstOfficerTo(p, f);
						p.setHomeBase(f.getFlight().getArrivalAirportCode());
						break;
					}
					catch(DoubleBookedException e) {
						//Leave this blank for now
					}
				}
			}
			
			for(CabinCrew cc : arg1.getAllCabinCrew()) {
				if(schedule.getCabinCrewOf(f).size() < schedule.getAircraftFor(f).getCabinCrewRequired()) {
					try {
						if(cc.isQualifiedFor(schedule.getAircraftFor(f))) {
							schedule.allocateCabinCrewTo(cc, f);
							cc.setHomeBase(f.getFlight().getArrivalAirportCode());
						}
					}
					catch(DoubleBookedException e) {
						//Leave this blank for now
					}
				}
			}
			
			if(schedule.getCabinCrewOf(f).size() < schedule.getAircraftFor(f).getCabinCrewRequired()) {
				for(CabinCrew cc : arg1.findCabinCrewByHomeBase(f.getFlight().getDepartureAirportCode())) {
					if(schedule.getCabinCrewOf(f).size() < schedule.getAircraftFor(f).getCabinCrewRequired()) {
						try {
							schedule.allocateCabinCrewTo(cc, f);
							cc.setHomeBase(f.getFlight().getArrivalAirportCode());
						}
						catch(DoubleBookedException e) {
							//Leave this blank for now
						}
					}
				}
			}
			
			if(schedule.getCabinCrewOf(f).size() < schedule.getAircraftFor(f).getCabinCrewRequired()) {
				for(CabinCrew cc : arg1.getAllCabinCrew()) {
					if(schedule.getCabinCrewOf(f).size() < schedule.getAircraftFor(f).getCabinCrewRequired()) {
						try {
							schedule.allocateCabinCrewTo(cc, f);
							cc.setHomeBase(f.getFlight().getArrivalAirportCode());
						}
						catch(DoubleBookedException e) {
							//Leave this blank for now
						}
					}
				}
			}
			try {
				schedule.completeAllocationFor(f);
			}
			catch(InvalidAllocationException e) {
				//Leave this blank for now
			}
		}
		
		return schedule;
	}

	@Override
	public void setSchedulerRunner(SchedulerRunner arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
