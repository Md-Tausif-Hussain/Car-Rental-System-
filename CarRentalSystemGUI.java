
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CarRentalSystemGUI extends JFrame {
    private CarRentalSystem system;
    private JTextArea displayArea;

    public CarRentalSystemGUI() {
        system = new CarRentalSystem();

        setTitle("Car Rental System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        
        JButton addButton = new JButton("Add Car");
        JButton addCustomerButton = new JButton("Add Customer");
        JButton rentButton = new JButton("Rent Car");
        JButton returnButton = new JButton("Return Car");
        JButton historyButton = new JButton("View Rental History");

        addButton.addActionListener(e -> addCar());
        addCustomerButton.addActionListener(e -> addCustomer());
        rentButton.addActionListener(e -> rentCar());
        returnButton.addActionListener(e -> returnCar());
        historyButton.addActionListener(e -> viewRentalHistory());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(addCustomerButton);
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(historyButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
    }

    private void addCar() {
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField priceField = new JTextField();
        
        Object[] message = {
            "Brand:", brandField,
            "Model:", modelField,
            "Price per day:", priceField,
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Car", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String brand = brandField.getText();
            String model = modelField.getText();
            double price;
            try {
                price = Double.parseDouble(priceField.getText());
                system.addCar(new Car(brand, model, price));
                displayArea.append("Car added successfully: " + brand + " " + model + "\n");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid price format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addCustomer() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name:", "Add Customer", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isEmpty()) {
            system.addCustomer(new Customer(name, system.getNextCustomerId()));
            displayArea.append("Customer added successfully: " + name + "\n");
        }
    }

    private void rentCar() {
        List<Car> availableCars = system.getAvailableCars();
        if (availableCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available for rent.", "Rent Car", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<String> carList = new ArrayList<>();
        for (Car car : availableCars) {
            carList.add(car.toString());
        }
        String carChoice = (String) JOptionPane.showInputDialog(this, "Choose a car to rent:", "Rent Car",
                JOptionPane.PLAIN_MESSAGE, null, carList.toArray(), carList.get(0));

        if (carChoice != null) {
            List<Customer> customers = system.getCustomers();
            if (customers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No customers registered.", "Rent Car", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<String> customerList = new ArrayList<>();
            for (Customer customer : customers) {
                customerList.add(customer.toString());
            }
            String customerChoice = (String) JOptionPane.showInputDialog(this, "Choose a customer:", "Rent Car",
                    JOptionPane.PLAIN_MESSAGE, null, customerList.toArray(), customerList.get(0));

            if (customerChoice != null) {
                Car car = availableCars.get(carList.indexOf(carChoice));
                Customer customer = customers.get(customerList.indexOf(customerChoice));
                system.rentCar(car, customer);
                displayArea.append("Car rented successfully: " + car + " to " + customer + "\n");
            }
        }
    }

    private void returnCar() {
        List<Rental> activeRentals = system.getActiveRentals();
        if (activeRentals.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active rentals.", "Return Car", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<String> rentalList = new ArrayList<>();
        for (Rental rental : activeRentals) {
            rentalList.add(rental.toString());
        }
        String rentalChoice = (String) JOptionPane.showInputDialog(this, "Choose a rental to return:", "Return Car",
                JOptionPane.PLAIN_MESSAGE, null, rentalList.toArray(), rentalList.get(0));

        if (rentalChoice != null) {
            Rental rental = activeRentals.get(rentalList.indexOf(rentalChoice));
            system.returnCar(rental);
            displayArea.append("Car returned successfully: " + rental.getCar() + "\n");
        }
    }

    private void viewRentalHistory() {
        displayArea.append("\n--- Rental History ---\n");
        for (Rental rental : system.getRentals()) {
            displayArea.append(rental + "\n");
        }
        displayArea.append("----------------------\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarRentalSystemGUI gui = new CarRentalSystemGUI();
            gui.setVisible(true);
        });
    }
}

class Car {
    private String brand;
    private String model;
    private double pricePerDay;
    private boolean isAvailable;

    public Car(String brand, String model, double pricePerDay) {
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.isAvailable = true;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return brand + " " + model + " (Price per day: $" + pricePerDay + ")";
    }
}

class Customer {
    private String name;
    private int customerId;

    public Customer(String name, int customerId) {
        this.name = name;
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "Customer ID: " + customerId + ", Name: " + name;
    }
}

class Rental {
    private Car car;
    private Customer customer;
    private LocalDate rentalDate;
    private LocalDate returnDate;

    public Rental(Car car, Customer customer, LocalDate rentalDate, LocalDate returnDate) {
        this.car = car;
        this.customer = customer;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return car + " rented by " + customer + " on " + rentalDate;
    }
}

class CarRentalSystem {
    private List<Car> cars = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Rental> rentals = new ArrayList<>();
    private int nextCustomerId = 1;

    public List<Car> getAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            if (car.isAvailable()) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public List<Rental> getActiveRentals() {
        List<Rental> activeRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getReturnDate() == null) {
                activeRentals.add(rental);
            }
        }
        return activeRentals;
    }

    public int getNextCustomerId() {
        return nextCustomerId;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        nextCustomerId++;
    }

    public void rentCar(Car car, Customer customer) {
        car.setAvailable(false);
        rentals.add(new Rental(car, customer, LocalDate.now(), null));
    }

    public void returnCar(Rental rental) {
        rental.getCar().setAvailable(true);
        rental.setReturnDate(LocalDate.now());
    }
}
