import java.util.*;
import java.io.*;

public class AloneAirlineSystem {
    private List<Customer> customers = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<Flight> flights = new ArrayList<>();

    public AloneAirlineSystem() {
        loadFlights();
        loadTickets();
    }

    private void loadFlights() {
        flights.add(new Flight("AA101", "Hanoi", "Da Nang", "2024/08/08", "12:00"));
        flights.add(new Flight("AA102", "Hanoi", "Ho Chi Minh", "2024/08/08", "15:00"));
        flights.add(new Flight("AA103", "Hanoi", "Da Nang", "2024/08/07", "15:00"));
        flights.add(new Flight("AA104", "Hanoi", "Ho Chi Minh", "2024/08/07", "19:00"));
        flights.add(new Flight("AA105", "Hanoi", "Da Nang", "2024/08/09", "11:30"));
        flights.add(new Flight("AA101", "Hanoi", "Ho Chi Minh", "2024/08/10", "06:00"));
        flights.add(new Flight("AA101", "Hanoi", "Da Nang", "2024/08/10", "11:00"));
        flights.add(new Flight("AA101", "Hanoi", "Ho Chi Minh", "2024/08/12", "12:00"));
        flights.add(new Flight("AA101", "Hanoi", "Da Nang", "2024/08/11", "09:00"));
        flights.add(new Flight("AA101", "Hanoi", "HO Chi Minh", "2024/08/13", "18:00"));
        flights.add(new Flight("AA101", "Hanoi", "Da Nang", "2024/08/14", "17:00"));

    }

    private void loadTickets() {
        try (BufferedReader reader = new BufferedReader(new FileReader("listtickets.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9) { // Adjusted for the new seatPosition field
                    tickets.add(new Ticket(parts[3], parts[4], parts[5], parts[6], Double.parseDouble(parts[7]), parts[0], parts[2], parts[8]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public void bookTicket(Customer customer) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Available flights:");
        for (int i = 0; i < flights.size(); i++) {
            Flight flight = flights.get(i);
            System.out.println((i + 1) + ". Flight: " + flight.getFlightNumber() + ", From: " + flight.getDeparture() + ", To: " + flight.getDestination() + ", Date: " + flight.getDepartureDate() + ", Time: " + flight.getDepartureTime());
        }

        System.out.print("Select flight by number: ");
        int flightChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (flightChoice < 1 || flightChoice > flights.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        Flight selectedFlight = flights.get(flightChoice - 1);

        System.out.println("1. Economy: 2,000,000 VND\n2. Business: 2,500,000 VND\n3. First Class: 4,799,000 VND");
        System.out.print("Select ticket type by number: ");
        int ticketTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        double price;
        switch (ticketTypeChoice) {
            case 1:
                price = 2000000;
                break;
            case 2:
                price = 2500000;
                break;
            case 3:
                price = 4799000;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        System.out.print("Enter seat position (e.g., 12A): ");
        String seatPosition = scanner.nextLine();

        Ticket ticket = new Ticket(selectedFlight.getFlightNumber(), selectedFlight.getDeparture(), selectedFlight.getDestination(), selectedFlight.getDepartureTime(), price, customer.getName(), customer.getEmail(), seatPosition);
        tickets.add(ticket);
        saveTickets();

        System.out.println("Ticket booked successfully! Please arrive 30 minutes to 1 hour before departure.");
    }

    public void modifyTicket() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Your tickets:");
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            System.out.println((i + 1) + ". " + ticket);
        }

        System.out.print("Select ticket to modify by number: ");
        int ticketChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (ticketChoice < 1 || ticketChoice > tickets.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        Ticket ticket = tickets.get(ticketChoice - 1);

        System.out.println("Modify ticket details (press enter to keep current values):");

        System.out.print("New flight number (" + ticket.getFlightNumber() + "): ");
        String newFlightNumber = scanner.nextLine();
        if (!newFlightNumber.isEmpty() && Ticket.validateFlightNumber(newFlightNumber)) {
            String newDestination = "";
            String newDepartureTime = "";
            boolean validDestination = false;
            boolean validDepartureTime = false;

            // Ensure new destination is valid
            while (!validDestination) {
                System.out.print("New arrival destination (" + ticket.getArrival() + "): ");
                newDestination = scanner.nextLine();
                if (newDestination.isEmpty()) {
                    newDestination = ticket.getArrival();
                    validDestination = true;
                } else {
                    for (Flight flight : flights) {
                        if (flight.getDestination().equalsIgnoreCase(newDestination)) {
                            validDestination = true;
                            break;
                        }
                    }
                    if (!validDestination) {
                        System.out.println("Invalid destination. Please enter a valid destination from available flights.");
                    }
                }
            }

            // Ensure new departure time is valid
            while (!validDepartureTime) {
                System.out.print("New departure time (" + ticket.getDepartureTime() + "): ");
                newDepartureTime = scanner.nextLine();
                if (newDepartureTime.isEmpty()) {
                    newDepartureTime = ticket.getDepartureTime();
                    validDepartureTime = true;
                } else {
                    for (Flight flight : flights) {
                        if (flight.getDepartureTime().equals(newDepartureTime) && flight.getDestination().equalsIgnoreCase(newDestination)) {
                            validDepartureTime = true;
                            break;
                        }
                    }
                    if (!validDepartureTime) {
                        System.out.println("Invalid departure time. Please enter a valid departure time from available flights.");
                    }
                }
            }

            ticket = new Ticket(newFlightNumber, ticket.getDeparture(), newDestination, newDepartureTime, ticket.getPrice(), ticket.getCustomerName(), ticket.getCustomerEmail(), ticket.getseatPosition());
            tickets.set(ticketChoice - 1, ticket); // Update the list with the new ticket details
        }

        System.out.println("Ticket modified successfully!");
        saveTickets();
    }
    public void deleteTicket() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Your tickets:");
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            System.out.println((i + 1) + ". " + ticket);
        }

        System.out.print("Select ticket to delete by number: ");
        int ticketChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (ticketChoice < 1 || ticketChoice > tickets.size()) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        tickets.remove(ticketChoice - 1);
        System.out.println("Ticket deleted successfully!");
        saveTickets();
    }

    private void saveTickets() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("listticket.csv"))) {
            for (Ticket ticket : tickets) {
                writer.write(ticket.getCustomerName() + "," + ticket.getFlightNumber() + "," + ticket.getCustomerEmail() + "," + ticket.getDeparture() + "," + ticket.getArrival() + "," + ticket.getDepartureTime() + "," + ticket.getPrice() + "," + ticket.getseatPosition() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
