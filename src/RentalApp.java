import java.sql.*;
import java.util.Scanner;

public class RentalApp {
    private static final String URL = "jdbc:mysql://localhost:3306/rental?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "Layal030208@";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to database!");

            while (true) {
                System.out.println("\n--- CAR-RENTAL SYSTEM MENU :) ---");
                System.out.println("1. Register new customer");
                System.out.println("2. View all customers");
                System.out.println("3. View available cars");
                System.out.println("4. Create car rental ");
                System.out.println("5. View all rentals");
                System.out.println("6. Exit");
                System.out.println("7. Delete customer");
                System.out.print("Choose: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> insertCustomer(con, scanner);
                    case 2 -> viewCustomers(con);
                    case 3 -> viewCars(con);
                    case 4 -> createRental(con, scanner);
                    case 5 -> viewRentals(con);
                    case 6 -> {
                        System.out.println("Bye!");
                        return;
                    }
                    case 7 -> deleteCustomer(con, scanner);
                    default -> System.out.println("Invalid choice!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // cutomer method
    private static void insertCustomer(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Name-Lastname: ");
        String name = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Zip: ");
        String zip = scanner.nextLine();
        System.out.print("City: ");
        String city = scanner.nextLine();
        System.out.print("Mobile phone: ");
        String mobile = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Driver license: ");
        String licens = scanner.nextLine();
        System.out.print("Driver Birth date (YYYY-MM-DD): ");
        String driverDate = scanner.nextLine();

        String sql = "INSERT INTO Renter (name, address, zip, city, mobilePhone, phone, email, driversLicens, driverDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, zip);
            ps.setString(4, city);
            ps.setString(5, mobile);
            ps.setString(6, phone);
            ps.setString(7, email);
            ps.setString(8, licens);
            ps.setString(9, driverDate);
            ps.executeUpdate();
            System.out.println("Customer inserted ✅");
        }
    }

    private static void viewCustomers(Connection con) throws SQLException {
        String sql = "SELECT * FROM Renter";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("renterID") + " | "
                        + rs.getString("name") + " | "
                        + rs.getString("address") + " | "
                        + rs.getString("city") + " | "
                        + rs.getString("email"));
            }
        }
    }

    // car method
    private static void viewCars(Connection con) throws SQLException {
        String sql = "SELECT c.carId, c.brand, c.model, c.fuelType, c.registrationNumber, c.odometer, ct.name AS type " +
                "FROM Car c JOIN CarType ct ON c.groupId = ct.groupId";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("carId") + " | "
                        + rs.getString("brand") + " " + rs.getString("model") +
                        " | " + rs.getString("fuelType") +
                        " | " + rs.getString("registrationNumber") +
                        " | Odo: " + rs.getInt("odometer") +
                        " | Type: " + rs.getString("type"));
            }
        }
    }

    // rental method
    private static void createRental(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Enter customer ID: ");
        int renterId = scanner.nextInt();
        System.out.print("Enter car ID: ");
        int carId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("From date (YYYY-MM-DD HH:MM:SS): ");
        String fromDate = scanner.nextLine();
        System.out.print("To date (YYYY-MM-DD HH:MM:SS): ");
        String toDate = scanner.nextLine();
        System.out.print("Max km: ");
        int maxKm = scanner.nextInt();
        System.out.print("Start odometer: ");
        int startOdo = scanner.nextInt();
        scanner.nextLine();

        String sql = "INSERT INTO RentalContract (renterId, carId, fromDateTime, toDateTime, maxKm, startOdometer) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, renterId);
            ps.setInt(2, carId);
            ps.setString(3, fromDate);
            ps.setString(4, toDate);
            ps.setInt(5, maxKm);
            ps.setInt(6, startOdo);
            ps.executeUpdate();
            System.out.println("Rental created ✅");
        }
    }

    private static void viewRentals(Connection con) throws SQLException {
        String sql = """
            SELECT rc.contractId, r.name, c.brand, c.model, rc.fromDateTime, rc.toDateTime, rc.maxKm
            FROM RentalContract rc
            JOIN Renter r ON rc.renterId = r.renterID
            JOIN Car c ON rc.carId = c.carId
            """;

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Contract #" + rs.getInt("contractId") + " | "
                        + rs.getString("name") + " | "
                        + rs.getString("brand") + " " + rs.getString("model") + " | "
                        + rs.getString("fromDateTime") + " → " + rs.getString("toDateTime") +
                        " | Max km: " + rs.getInt("maxKm"));
            }
        }
    }
    private static void deleteCustomer(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Enter customer ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        // Først tjekker vi om kunden findes
        String checkSql = "SELECT * FROM Renter WHERE renterID = ?";
        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setInt(1, id);
            ResultSet rs = checkPs.executeQuery();
            if (!rs.next()) {
                System.out.println("❌ Customer not found.");
                return;
            }
        }

        // Så sletter vi kunden
        String sql = "DELETE FROM Renter WHERE renterID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Customer deleted.");
            } else {
                System.out.println("❌ Could not delete customer.");
            }
        }
    }
}