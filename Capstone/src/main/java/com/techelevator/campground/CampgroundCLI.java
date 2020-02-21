package com.techelevator.campground;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.campground.model.Campground;
import com.techelevator.campground.model.CampgroundDAO;
import com.techelevator.campground.model.Park;
import com.techelevator.campground.model.ParkDAO;
import com.techelevator.campground.model.ReservationDAO;
import com.techelevator.campground.model.Site;
import com.techelevator.campground.model.SiteDAO;
import com.techelevator.campground.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.campground.model.jdbc.JDBCParkDAO;
import com.techelevator.campground.model.jdbc.JDBCReservationDAO;
import com.techelevator.campground.model.jdbc.JDBCSiteDAO;
import com.techelevator.view.Menu;

public class CampgroundCLI {
	
	private Menu menu;
	private CampgroundDAO campgroundDao;
	private SiteDAO siteDao;
	private ParkDAO parkDao;
	private ReservationDAO reservationDao;
	private Scanner scan = new Scanner(System.in);
	
	private static final String PARK_MENU_VIEW_CAMPGROUNDS = "View Campgrounds";
	private static final String PARK_MENU_SEARCH_FOR_RESERVATION = "Search for Reservation";
	private static final String PARK_MENU_RETURN_TO_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String[] PARK_MENU_OPTIONS = new String[] {PARK_MENU_VIEW_CAMPGROUNDS, 
								PARK_MENU_RETURN_TO_PREVIOUS_SCREEN};
	private static final String CAMPGROUND_VIEW_RESERVATIONS = "Search for Available Reservation In Specific Campground";
	private static final String CAMPGROUND_RETURN_TO_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String[] CAMPGROUND_MENU_OPTIONS = new String[] {CAMPGROUND_VIEW_RESERVATIONS, 
								CAMPGROUND_RETURN_TO_PREVIOUS_SCREEN };
	

	public static void main(String[] args) {
		CampgroundCLI application = new CampgroundCLI();
		application.run();
	}
	
	public CampgroundCLI() {
		this.menu = new Menu(System.in, System.out);
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		
		campgroundDao = new JDBCCampgroundDAO(dataSource);
		siteDao = new JDBCSiteDAO(dataSource);
		reservationDao = new JDBCReservationDAO(dataSource);
		parkDao = new JDBCParkDAO(dataSource);	
	}
	
	private String[] populateMainMenu() {
		List<Park> parks = parkDao.getAllParks();
		String[] parkNames = new String[parks.size() + 1];
		for (int i = 0; i < parks.size(); i++) {
			parkNames[i] = parks.get(i).getName();
		}
		parkNames[parkNames.length - 1] = "Quit";
		return parkNames;
	}

	public void run() {
		while (true) {
			System.out.println("View Parks Interface");
			System.out.println("Select a Park for Further Details");
			String choice = (String) menu.getChoiceFromOptions(populateMainMenu());
			Park park = parkDao.getParkByName(choice);
			handleParkChoice(park);
		}
	}
	
	private void handleParkChoice(Park parkChosen) {
		displayPark(parkChosen);
		String choice = (String) menu.getChoiceFromOptions(PARK_MENU_OPTIONS);
		if (choice.equals(PARK_MENU_VIEW_CAMPGROUNDS)) {
			int chosen_park_id = parkDao.getParkIdByName(parkChosen.getName());
			handleViewCampgrounds(chosen_park_id, parkChosen);
		} else if (choice.equals(PARK_MENU_SEARCH_FOR_RESERVATION)) {
			//BONUS if we get to it
		}
	}
	
	private void displayPark(Park parkChosen) {
		System.out.println();
		System.out.println("Park Information Screen");
		System.out.println(parkChosen.getName() + " Park");
		System.out.format("%-17s %s\n", "Location:", parkChosen.getLocation());
		System.out.format("%-17s %tD\n", "Established:", parkChosen.getEstablish_date());
		System.out.format("%-17s %,d acres\n", "Area:", parkChosen.getArea());
		System.out.format("%-17s %,d\n", "Annual Visitors:", parkChosen.getVisitors());
		String description = parkChosen.getDescription();
		System.out.print("\n" + shortenStringFullWords(description, 100));
	}
	
	public String shortenStringFullWords(String str, int maxLength) {
		String result = "";
		String line = "";
		String[] tokens = str.split(" ");
		for (String token : tokens) {
			if (line.length() + token.length() <= maxLength - 3) {
				line += token;
				line += " ";
			} else {
				result += line + "\n";
				line = "";
			}
		}
		return result;
	}
	
	private void handleViewCampgrounds(int chosen_park_id, Park parkChosen) {
		System.out.println("Park Campgrounds");
		List<Campground> campgrounds = campgroundDao.getCampgroundsInPark(chosen_park_id);
		displayCampgrounds(parkChosen, campgrounds);
		String choice = (String) menu.getChoiceFromOptions(CAMPGROUND_MENU_OPTIONS);
		if (choice.equals(CAMPGROUND_VIEW_RESERVATIONS)) {
			handleReservationSearch(parkChosen, campgrounds, chosen_park_id);
		} else if (choice.equals(CAMPGROUND_RETURN_TO_PREVIOUS_SCREEN)) {
			handleParkChoice(parkChosen);
		}
	}
	
	private void handleReservationSearch(Park parkChosen, List<Campground> campgrounds, int chosen_park_id) {
		System.out.println("Search for Campground Reservation");
		displayCampgrounds(parkChosen, campgrounds);
		System.out.println("\nWhich campground (enter 0 to cancel)?");
		try {
			int campgroundInput = Integer.parseInt(scan.nextLine());
			if (campgroundInput != 0) {
				if(seeIfCampgroundIsValid(parkChosen, campgroundInput)) {
					Campground campgroundChosen = campgrounds.get(campgroundInput - 1);
					handleReservationRequest(campgroundChosen, campgrounds, parkChosen);		
				} else {
					System.out.println("Not a valid campground");
					handleViewCampgrounds(chosen_park_id, parkChosen);
				}
			} else {
				handleViewCampgrounds(chosen_park_id, parkChosen);
			}
		} catch(NumberFormatException e) {
			System.out.println("Not a valid campground, try again.");
			handleReservationSearch(parkChosen, campgrounds, chosen_park_id);
		}	
	}
	
	private void handleReservationRequest(Campground campgroundChosen, List<Campground> campgrounds, Park parkChosen) {
		boolean datesChosen = false;
		while(!datesChosen) {
			System.out.println("What is the arrival date? (YYYY-MM-DD)");
			String arrivalDate = scan.nextLine();
			try {
				LocalDate parsedArrivalDate = LocalDate.parse(arrivalDate);
				System.out.println("What is the departure date? (YYYY-MM-DD)");
				String departureDate = scan.nextLine();
				LocalDate parsedDepartureDate = LocalDate.parse(departureDate);
				datesChosen = true;
				int campground_id = campgroundChosen.getCampground_id();
				if (chosenDatesValidForCampground(campground_id, parsedArrivalDate, parsedDepartureDate)) {
					List<Site> sites = siteDao.availableSites(campgroundChosen.getName(), 
										arrivalDate, departureDate);
					if(sites.size() == 0) {
						System.out.println("No sites available");
						return;
					} else {
						handleMakingReservation(sites, campgrounds, arrivalDate, departureDate, 
												campgroundChosen, parsedArrivalDate, parkChosen);	
					}
				} else {
					System.out.println("\nSorry, this campground is closed during those dates.\n");
					handleReservationSearch(parkChosen, campgrounds, parkChosen.getPark_id());	
				}
			} catch(DateTimeParseException e) {
				System.out.println("Invalid date format, try again.");
			}
		}
	}
	
	private boolean chosenDatesValidForCampground(int campground_id, LocalDate parsedArrivalDate, 
													LocalDate parsedDepartureDate) {
		 boolean result = false;
		 Campground campChosen = campgroundDao.getCampgroundByCampgroundId(campground_id);
		 int from_month = Integer.parseInt(campChosen.getOpen_from_mm());
		 int to_month = Integer.parseInt(campChosen.getOpen_to_mm());
		 
		 int parsedArrivalMonth = parsedArrivalDate.getMonthValue();
		 int parsedDepartureMonth = parsedDepartureDate.getMonthValue();
		 
		 if (parsedArrivalMonth >= from_month && parsedArrivalMonth <= to_month) {
			 if (parsedDepartureMonth >= from_month && parsedDepartureMonth <= to_month) {
				 result = true;
			 }
		 }		
		 return result;
	}
	
	private boolean seeIfCampgroundIsValid(Park parkChosen, int campgroundInput) {
		boolean result = false;
		List<Campground> campgrounds = campgroundDao.getCampgroundsInPark(parkChosen.getPark_id());
		for (Campground c : campgrounds) {
			if (c.getCampground_id() == campgroundInput) {
				result = true;
			}
		}
		return result;
	}
	
	private boolean seeIfSiteIsValid(int siteChosen, List<Site> sites) {
		boolean result = false;
		for (Site s : sites) {
			if (s.getSite_id() == siteChosen) {
				result = true;
			}
		}
		return result;
	}
	
	private void displayCampgrounds(Park parkChosen, List<Campground> campgrounds) {
		int num = 1;
		System.out.println(parkChosen.getName() + " Park Campgrounds");
		System.out.format("%11s %35s %11s %15s", "Name", "Open", "Close", "Daily Fee\n");
		for (Campground c : campgrounds) {
			String open_from = new DateFormatSymbols().getMonths()[Integer.parseInt(c.getOpen_from_mm()) - 1];
			String open_to = new DateFormatSymbols().getMonths()[Integer.parseInt(c.getOpen_to_mm()) - 1];
			System.out.format("#%-5d %-35s %-10s %-10s $%.2f\n", num, c.getName(), 
					open_from, open_to, c.getDaily_fee());
			num++;
		}
	}
	
	private void handleMakingReservation(List<Site> sites, List<Campground> campgrounds, String arrivalDate, 
										String departureDate, Campground campgroundChosen, LocalDate parsedArrivalDate,
										Park parkChosen) {
		Period period = Period.between(LocalDate.parse(arrivalDate), LocalDate.parse(departureDate));
		int days = period.getDays();
		if(days < 1) {
			System.out.println("Invalid dates. Try again.\n");
			handleReservationRequest(campgroundChosen, campgrounds, parkChosen);
		} else {
			handleValidReservationRequest(sites, campgrounds, campgroundChosen, days, parkChosen, parsedArrivalDate);
		}
	}
	
	private void handleValidReservationRequest(List<Site> sites, List<Campground> campgrounds, Campground campgroundChosen,
			int days, Park parkChosen, LocalDate parsedArrivalDate) {
		printAvailableSites(sites, campgroundChosen, days);
		System.out.println("Which site should be reserved (enter 0 to cancel)?");
		try {
			int siteReserved = Integer.parseInt(scan.nextLine());
			if (siteReserved == 0) {
				handleReservationSearch(parkChosen, campgrounds, parkChosen.getPark_id());
			} else if (!seeIfSiteIsValid(siteReserved, sites)) {
				System.out.println("Not a valid site, try again.");
				handleValidReservationRequest(sites, campgrounds, campgroundChosen, days, parkChosen, parsedArrivalDate);
			} else {
				System.out.println("What name should the reservation be made under? ");
				String reservationName = scan.nextLine();
				int reservation_id = reservationDao.createReservationAndReturnId(reservationName, siteReserved,
						parsedArrivalDate, days);
				System.out.println("\nThe reservation has been made and the confirmation Id is " + reservation_id);
			}
		} catch (NumberFormatException e) {
			System.out.println("Not a valid site number, try again.");
			handleValidReservationRequest(sites, campgrounds, campgroundChosen, days, parkChosen, parsedArrivalDate);
		}
	}
	
	private void printAvailableSites(List<Site> sites, Campground campground, int days) {
		System.out.println("\nResults Matching Your Search Criteria");
		System.out.format("%-10s %-10s %-13s %-15s %-10s %s", "Site No.", "Max Occup.", "Accessible?", "Max Rv Length"
							, "Utility", "Cost\n");
		for (Site site : sites) {
			System.out.format("%-10d %-10d %-13b %-15d %-10b $%.2f\n", site.getSite_number(), site.getMax_occupancy(),
							site.isAccessible(), site.getMax_rv_length(), site.isUtilities(), 
							(campground.getDaily_fee().multiply(new BigDecimal(days))));		
		}
	}
	
}
