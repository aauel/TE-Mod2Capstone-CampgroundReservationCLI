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
	
	private static final String PARK_MENU_VIEW_CAMPGROUNDS = "View This Park's Campgrounds";
	private static final String PARK_MENU_SEARCH_FOR_RESERVATION = "Search for Reservation";
	private static final String PARK_MENU_RETURN_TO_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String[] PARK_MENU_OPTIONS = new String[] {PARK_MENU_VIEW_CAMPGROUNDS, 
																	PARK_MENU_RETURN_TO_PREVIOUS_SCREEN};
//	private static final String CAMPGROUND_RESERVE_ANY_CAMPGROUND = "Find Available Sites in Any of These Campgrounds";
	private static final String CAMPGROUND_RESERVE_SPECIFIC_CAMPGROUND = "Find Available Sites for a Specific Campground";
	private static final String CAMPGROUND_RETURN_TO_PREVIOUS_SCREEN = "Return to Previous Screen";
	private static final String[] CAMPGROUND_MENU_OPTIONS = new String[] {CAMPGROUND_RESERVE_SPECIFIC_CAMPGROUND, 
																		  CAMPGROUND_RETURN_TO_PREVIOUS_SCREEN };
	private static final String CAMPGROUND_CLOSED_NEW_DATES = "Try new dates for this campground";
	private static final String CAMPGROUND_CLOSED_SWITCH_CAMPGROUND = "Try a different campground";
	private static final String[] CAMPGROUND_CLOSED_OPTIONS = new String[] {CAMPGROUND_CLOSED_NEW_DATES, 
																			CAMPGROUND_CLOSED_SWITCH_CAMPGROUND };
	

	public static void main(String[] args) {
		CampgroundCLI application = new CampgroundCLI();
		application.run();
	}
	
	/***********************************************************************************
	 * This method starts the application interface with the user.  It calls 
	 * getAllParkNames() to display as menu options for the user to choose from 
	 */
	public void run() {
		while (true) {
			System.out.println("\n\nSelect a Park for Further Details");
			String choice = (String) menu.getChoiceFromOptions(getAllParkNames());
			Park park = parkDao.getParkByName(choice);
			handleParkChoice(park);
		}
	}
	
	
	/*********************************************************************************** 
	 * This method calls displayPark() to display the chosen park information to the   
	 * console, then prompts the user to view campgrounds, or return to the main menu
	 * 
	 * @param parkChosen to retrieve list of campgrounds from campground Dao
	 */
	private void handleParkChoice(Park parkChosen) {
		displayPark(parkChosen);
		String choice = (String) menu.getChoiceFromOptions(PARK_MENU_OPTIONS);
		if (choice.equals(PARK_MENU_VIEW_CAMPGROUNDS)) {
			viewCampgrounds(parkChosen);
		} else if (choice.equals(PARK_MENU_SEARCH_FOR_RESERVATION)) {
			//BONUS if we get to it
		} else {
			run();  // return to list of parks
		}
	}
	
	
	/*********************************************************************************** 
	 * This method calls displayCampgrounds() to display a list of campgrounds and 
	 * corresponding attributes for a specific park, then prompts the user to start a 
	 * reservation or return to the previous screen
	 * 
	 * @param parkChosen to retrieve list of campgrounds from campground Dao
	 */
	private void viewCampgrounds(Park parkChosen) {
		List<Campground> campgroundsInChosenPark = campgroundDao.getCampgroundsInPark(parkChosen.getPark_id());
		displayCampgrounds(parkChosen.getName(), campgroundsInChosenPark);
		String choice = (String) menu.getChoiceFromOptions(CAMPGROUND_MENU_OPTIONS);
		if (choice.equals(CAMPGROUND_RESERVE_SPECIFIC_CAMPGROUND)) {
			startReservation(parkChosen, campgroundsInChosenPark);
		} else if (choice.equals(CAMPGROUND_RETURN_TO_PREVIOUS_SCREEN)) {
			handleParkChoice(parkChosen);
		}
	}
	
	
	/*********************************************************************************** 
	 * This method prints a list of campgrounds and corresponding attributes for a 
	 * specific park chosen, then prompts the user to choose a campground to continue
	 * their reservation.
	 * 
	 * @param parkChosenName to display the name of the park in the header
	 * @param campgroundsInChosenPark to display the campground name, the months open, 
	 * 		  and the daily fee, in a tabular format
	 */
	private void startReservation(Park parkChosen, List<Campground> campgroundsInChosenPark) {
		boolean restartReservation = false;
		while (!restartReservation) {
			displayCampgrounds(parkChosen.getName(), campgroundsInChosenPark);
			System.out.println();
			try {
				int campgroundInput = Integer.parseInt(getUserInput("\nWhich campground (enter 0 to cancel)?"));
				if (campgroundInput != 0) {
					if(campgroundInput <= campgroundsInChosenPark.size() && 
							campgroundInput > 0) {
						
						/***** HAPPY PATH *****/
						Campground campgroundChosen = campgroundsInChosenPark.get(campgroundInput - 1);
						restartReservation = getReservationDates(campgroundChosen);
						
					} else {
						System.out.println("Not a valid campground");
					}
				} else {
					viewCampgrounds(parkChosen);
				}
			} catch(NumberFormatException e) {
				System.out.println("Not a valid campground, try again.");
			}	
		}
	}
	
	
	/*********************************************************************************** 
	 * This method prompts the user for an arrival and departure date, checks for valid
	 * date input, checks the dates against the campground's open season, and then 
	 * moves forward only if there are available sites for the campground and dates
	 * 
	 * @param campgroundChosen to compare to open_to and open_from months, and to look
	 * 		  for available sites to reserve
	 * @return true if the dates were entered in the correct order
	 */
	private boolean getReservationDates(Campground campgroundChosen) {
		boolean validDates = false;
		while(!validDates) {
			try {
				// Prompt user for arrival and departure dates
				String arrivalDate = getUserInput("What is the arrival date? (YYYY-MM-DD)");
				LocalDate parsedArrivalDate = LocalDate.parse(arrivalDate);
				String departureDate = getUserInput("What is the departure date? (YYYY-MM-DD)");
				LocalDate parsedDepartureDate = LocalDate.parse(departureDate);
				
				/* TESTING ONLY */
//				LocalDate parsedArrivalDate = LocalDate.of(2020, 2, 2);
//				LocalDate parsedDepartureDate = LocalDate.of(2020, 2, 20);
				
				System.out.println("-------------------------------------------------------------------------------------------------");
			
				if (validDatePeriod(parsedArrivalDate, parsedDepartureDate)) {
					// departureDate > arrivalDate
					if (chosenDatesValidForCampground(campgroundChosen, parsedArrivalDate, parsedDepartureDate)) {
						/***** HAPPY PATH *****/
						validDates = checkForAvailableSites(campgroundChosen.getName(), 
															parsedArrivalDate, parsedDepartureDate);
						// if no sites available, return to beginning of while loop to prompt for new dates						
					} else {
						System.out.println("\nSorry, this campground is closed during those dates.\n");
						System.out.println("How would you like to proceed?");

						String choice = (String) menu.getChoiceFromOptions(CAMPGROUND_CLOSED_OPTIONS);
						if (choice.equals(CAMPGROUND_CLOSED_SWITCH_CAMPGROUND)) {
							return false;
							// jump out of getReservationDates to go back to startReservation where 
							// user will be presented with other campgrounds in the same park
						}
					}
				}
			} catch(DateTimeParseException e) {
				// user input could not be parsed into a LocalDate
				System.out.println("Invalid date format, try again.");
				// return to beginning of while(!validDates) to prompt for new dates
			}
		}
		return true;
	}
	
	
	/*********************************************************************************** 
	 * This method checks that the arrival date precedes the departure date entered
	 * 
	 * @return true if the dates were entered in the correct order
	 */
	private boolean validDatePeriod(LocalDate parsedArrivalDate, LocalDate parsedDepartureDate) {
		boolean result = true;
		Period period = Period.between(parsedArrivalDate, parsedDepartureDate);
		if(period.getDays() < 1) {
			System.out.println("Invalid dates. Try again.\n");
			return false;
		} 
		return result;
	}
	
	
	/*********************************************************************************** 
	 * This method checks that the arrival and departure dates entered against the
	 * specific campground's open season
	 * 
	 * @param campgroundChosen
	 * @return true if the dates fall inside the campground's open season
	 */
	private boolean chosenDatesValidForCampground(Campground campgroundChosen, LocalDate parsedArrivalDate, 
													LocalDate parsedDepartureDate) {
		 boolean result = false;
		 int from_month = Integer.parseInt(campgroundChosen.getOpen_from_mm());
		 int to_month = Integer.parseInt(campgroundChosen.getOpen_to_mm());
		 
		 int parsedArrivalMonth = parsedArrivalDate.getMonthValue();
		 int parsedDepartureMonth = parsedDepartureDate.getMonthValue();
		 
		 if (parsedArrivalMonth >= from_month && parsedArrivalMonth <= to_month) {
			 if (parsedDepartureMonth >= from_month && parsedDepartureMonth <= to_month) {
				 result = true;
			 }
		 }		
		 return result;
	}

	
	/*********************************************************************************** 
	 * This method checks that there is at least 1 available site for the dates entered,
	 * and if so, calls getSitesForChosenDates() to continue with reservation.
	 * 
	 * @return false if the dates fall outside the campground's open season
	 */
	private boolean checkForAvailableSites(String campgroundName, LocalDate parsedArrivalDate, LocalDate parsedDepartureDate) {
		boolean result = false;
		List<Site> sites = siteDao.getTop5AvailableSites(campgroundName, parsedArrivalDate, parsedDepartureDate);
		if(sites.size() == 0) {
			System.out.println("No sites available during those dates.");
			// return to getReservationDates()
		} else {
			/***** HAPPY PATH *****/
			int days = Period.between(parsedArrivalDate, parsedDepartureDate).getDays();
			result = getSitesForChosenDates(sites, parsedArrivalDate, days);	
		}
		return result;
	}


	/*********************************************************************************** 
	 * This method displays 
	 * 
	 * @param sites contains between 1 and 5 Sites that are available to reserve for the
	 * 		  user's chosen campground and requested dates
	 * @param parsedArrivalDate for the reservation record
	 * @param days for the reservation record
	 * 
	 * @return false if the user selects 0 to cancel
	 */
	private boolean getSitesForChosenDates(List<Site> sites, LocalDate parsedArrivalDate, int days) {
		// first print out the available sites and their attributes
		printAvailableSites(sites, days);
		boolean validSiteChosen = false;
		while(validSiteChosen == false) {
			try {
				// prompt the user to select a site, or 0 to cancel
				int choice = Integer.parseInt(getUserInput("\nWhich site should be reserved (enter 0 to cancel)?"));
				if (choice == 0) {
					// user entered 0 to cancel, so exit the method
					return false;
				} else if (!seeIfSiteIsValid(choice, sites)) {
					// user entered a number higher than the number of sites listed
					System.out.println("Please enter a site number from the list above.");
					// restart while loop to make another site selection
				} else {
					/***** HAPPY PATH *****/
					// prompt for a name for the reservation and then add new reservation to the database
					validSiteChosen = true;
					int site_id = sites.get(choice -1).getSite_id();
					finalizeReservation(site_id, parsedArrivalDate, days);
				}
			} catch (NumberFormatException e) {
				System.out.println("Not a valid site number.  Please try again.");
				// restart while loop to make another site selection
			}
		}
		return true;
	}
	
	
	/*********************************************************************************** 
	 * This method prints a list of sites and corresponding attributes, including the
	 * total cost of a reservation based on the campground chosen, and number of days
	 * 
	 * @param sites to retrieve all of the attributes
	 * @param days to calculate the total cost based on the campground daily fee
	 */
	private void printAvailableSites(List<Site> sites, int days) {
		// pull campground_id from site in order to get the daily_fee for calculating total cost of reservation
		int campground_id = sites.get(0).getCampground_id();
		BigDecimal dailyFee = campgroundDao.getDailyFeeByCampgroundId(campground_id);
		
		System.out.println("\nResults Matching Your Search Criteria");
		System.out.format("%-10s %-10s %-13s %-15s %-10s %s", "Site No.", "Max Occup.", "Accessible?", "Max Rv Length"
							, "Utility", "Cost\n");
		for (Site site : sites) {
			System.out.format("%-10d %-10d %-13s %-15s %-10s $%.2f\n", site.getSite_number(), site.getMax_occupancy(),
							getYesNo(site.isAccessible()), getNumOrNA(site.getMax_rv_length()), 
							getYesNo(site.isUtilities()), dailyFee.multiply(new BigDecimal(days)));		
		}
	}
	
	
	/*********************************************************************************** 
	 * This method prompts the user for a name for their reservation, then inserts a
	 * new reservation into the database for the chosen site and dates
	 * 
	 * @param site_id to associate the reservation to the correct site in the database
	 * @param parsedArrivalDate for the reservation record
	 * @param days for the reservation record
	 */
	private void finalizeReservation(int site_id, LocalDate parsedArrivalDate, int days) {
		String reservationName = "";
		while (reservationName.length() == 0) {
			reservationName = getUserInput("What name should the reservation be made under? ");
		}
		int reservation_id = reservationDao.createReservationAndReturnId(reservationName, site_id, parsedArrivalDate, days);
		
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println("\nThe reservation has been made and the confirmation Id is " + reservation_id);
		System.out.println("\n-------------------------------------------------------------------------------------------------");	
	}
	
	
	
	
/* ----------------------------------------------------------------------------------------
 * 
 *  HELPER METHODS - No application flow is determined in these methods.
 * 
 * ---------------------------------------------------------------------------------------- */	
	
	
	/***********************************************************************************
	 * This method initializes a new Menu, establishes the database connection,
	 * creates jdbc DAOs for each class (park, campground, site, reservation) and
	 * prints out the welcome banner.
	 */
	public CampgroundCLI() {
		this.menu = new Menu(System.in, System.out);
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		
		parkDao = new JDBCParkDAO(dataSource);	
		campgroundDao = new JDBCCampgroundDAO(dataSource);
		siteDao = new JDBCSiteDAO(dataSource);
		reservationDao = new JDBCReservationDAO(dataSource);
		
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("WELCOME TO THE NATIONAL PARK CAMPSITE RESERVATION SYSTEM!");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
	
	/***********************************************************************************
	 *  This method retrieves a list of park names from the parkDao
	 * 
	 * @return String[] of park names in order to populate menu options
	 */
	private String[] getAllParkNames() {
		List<String> parks = parkDao.getAllParkNames();
		String[] parkNames = new String[parks.size() + 1];
		for (int i = 0; i < parks.size(); i++) {
			parkNames[i] = parks.get(i);
		}
		parkNames[parkNames.length - 1] = "Quit";
		return parkNames;
	}
	
	
	/*********************************************************************************** 
	 * This method prints park information to the system console, including location, 
	 * established date, area, annual visitors, and description.
	 * 
	 * @param parkChosenName to retrieve the attributes for the chosen park
	 */
	private void displayPark(Park parkChosen) {
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(parkChosen.getName() + " Park Information\n");
		System.out.format("%-17s %s\n", "Location:", parkChosen.getLocation());
		System.out.format("%-17s %tD\n", "Established:", parkChosen.getEstablish_date());
		System.out.format("%-17s %,d acres\n", "Area:", parkChosen.getArea());
		System.out.format("%-17s %,d\n", "Annual Visitors:", parkChosen.getVisitors());
		System.out.println();
		shortenStringFullWords(parkChosen.getDescription(), 90);
		System.out.println("\n-------------------------------------------------------------------------------------------------");
	}
	
	
	/*********************************************************************************** 
	 * This method is used by displayPark to break up the park description over
	 * multiple lines when printing to the console.
	 * 
	 * @param s is the park description without any formatting
	 * @param maxLength dictates when to insert a new line
	 */
	private void shortenStringFullWords(String s, int maxLength){
		int charsWritten = 0;
		for(String w : s.split("\\s+")){
			if (charsWritten + w.length() > maxLength){
				System.out.println();
				charsWritten = 0;
			}
			System.out.print(w+" ");
			charsWritten += w.length() + 1;
		}
	}
	
	/*********************************************************************************** 
	 * This method prints a list of campgrounds and corresponding attributes for a 
	 * specific park to the system console
	 * 
	 * @param parkChosenName to display the name of the park in the header
	 * @param campgroundsInChosenPark to display the campground name, the months open, 
	 * 		  and the daily fee, in a tabular format
	 */
	private void displayCampgrounds(String parkChosenName, List<Campground> campgroundsInChosenPark) {
		int num = 1;
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------------------------");
		System.out.println(parkChosenName + " Park Campgrounds\n");
		System.out.format("%9s %35s %11s %15s", "Name", "Open", "Close", "Daily Fee\n");
		for (Campground c : campgroundsInChosenPark) {
			String open_from = new DateFormatSymbols().getMonths()[Integer.parseInt(c.getOpen_from_mm()) - 1];
			String open_to = new DateFormatSymbols().getMonths()[Integer.parseInt(c.getOpen_to_mm()) - 1];
			System.out.format("#%-3d %-35s %-10s %-10s $%.2f\n", num, c.getName(), 
					open_from, open_to, c.getDaily_fee());
			num++;
		}
		System.out.println("-------------------------------------------------------------------------------------------------");
	}
	
	private boolean seeIfSiteIsValid(int siteChosen, List<Site> sites) {
		boolean result = false;
		for (Site s : sites) {
			if (s.getSite_number() == siteChosen) {
				result = true;
			}
		}
		return result;
	}
	
	
	/*********************************************************************************** 
	 * This method converts true/false to Yes/No, respectively, as a string
	 */
	private String getYesNo(boolean bool) {
		String result = "No";
		if (bool) result = "Yes";
		return result;		
	}
	
	/*********************************************************************************** 
	 * This method takes an integer, and returns "N/A" if num == 0, otherwise, the
	 * number in a string format
	 */
	private String getNumOrNA(int num) {
		String result = "N/A";
		if (num > 0) result = Integer.toString(num);
		return result;		
	}	
	
	
	@SuppressWarnings("resource")
	private String getUserInput(String prompt) {
		System.out.print(prompt + " >>> ");
		return new Scanner(System.in).nextLine();
	}
	
}
