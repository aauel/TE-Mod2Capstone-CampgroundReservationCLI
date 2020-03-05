# TE-Mod2Capstone-CampgroundReservationCLI

### Module 2 Capstone - National Park Campsite Reservation

Congratulations! You did such a great job on your previous application we want you to build our new campsite
reservation application. We are tasking you to build a command line driven application that our National Park
Service can use to book campsite reservations.

#### The requirements for your application are listed below:

1. As a user of the system, I need the ability to view a list of the available parks in the system, sorted
alphabetically by name.
    1. A park includes an id, name, location, established date, area, annual visitor count, and
description.

2. As a user of the system, I need the ability to select a park that my customer is visiting and see a list of
all campgrounds for that available park.
    1. A campground includes an id, name, open month, closing month, and a daily fee.
    
3. As a user of the system, I need the ability to select a campground and search for date availability so
that I can make a reservation.
    1. A reservation search only requires the desired campground, a start date, and an end date.
    2. A campsite is unavailable if any part of their preferred date range overlaps with an existing
    reservation.
    3. If no campsites are available, indicate to the user that there are no available sites and ask them
    if they would like to enter in an alternate date range.
    4. The TOP 5 available campsites should be displayed along with the cost for the total stay.
    5. BONUS: If a date range is entered that occurs during the park off-season, then the user should
    not see any campsites available for reservation.
    
4. As a user of the system, once I find a campsite that is open during the time window I am looking for, I
need the ability to book a reservation at a selected campsite.
    1. A reservation requires a name to reserve under, a start date, and an end date.
    2. A confirmation id is presented to the user once the reservation has been submitted


#### Sample Screens


##### View Parks Interface
```
Select a Park for Further Details
1) Acadia
2) Arches
3) Cuyahoga National Valley Park
Q) quit
```

##### Park Information Screen
```
Acadia National Park
Location: Maine
Established: 02/26/1919
Area: 47,389 sq km
Annual Visitors: 2,563,129

Covering most of Mount Desert Island and other coastal islands, Acadia features the
tallest mountain on the Atlantic coast of the United States, granite peaks, ocean
shoreline, woodlands, and lakes. There are freshwater, estuary, forest, and intertidal
habitats.

Select a Command
1) View Campgrounds
2) Search for Reservation
3) Return to Previous Screen
```

##### Park Campgrounds
```
Acadia National Park Campgrounds

     Name           Open    Close     Daily Fee
#1   Blackwoods     January December  $35.00
#2   Schoodic Woods May     October   $30.00
#3   Seawall        May     September $30.00

Select a Command
1) Search for Available Reservation
2) Return to Previous Screen
```

##### Search for Campground Reservation
```
     Name           Open    Close     Daily Fee
#1   Blackwoods     January December  $35.00
#2   Schoodic Woods May     October   $30.00
#3   Seawall        May     September $30.00

Which campground (enter 0 to cancel)? __
What is the arrival date? __/__/____
What is the departure date? __/__/____
```

##### Search Results
```
Results Matching Your Search Criteria
Site No.  Max Occup.  Accessible?  Max RV Length  Utility  Cost
1         4           No           N/A            N/A      $XX
4         6           Yes          N/A            N/A      $XX
13        12          Yes          20             Yes      $XX

Which site should be reserved (enter 0 to cancel)? __
What name should the reservation be made under? __
```

##### Reservation Confirmation
```
The reservation has been made and the confirmation id is {Reservation_id}
```
