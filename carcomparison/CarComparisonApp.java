package carcomparison;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class CarComparisonApp {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat YEARS_FORMAT = new DecimalFormat("0.##");

    private final Queue<CarDetails> calculationQueue = new LinkedList<>();
    private final CarComparisonHistory history = new CarComparisonHistory();
    private final CarCostBinaryTree carCostTree = new CarCostBinaryTree();
    private final ArrayList<CarDetails> carsByMonthlyPayment = new ArrayList<>();
    private final Map<String, List<String>> modelsByBrand = new HashMap<>();
    private final Scanner scanner;

    public CarComparisonApp(Scanner scanner) {
        this.scanner = scanner;
    }

    public void addCar(
            String carName,
            double purchasePrice,
            double annualDepreciationRate,
            double taxRate,
            double monthlyPayment,
            int loanLengthMonths,
            double monthlyInsurance) {
        CarDetails car = new CarDetails(
                carName,
                purchasePrice,
                annualDepreciationRate,
                taxRate,
                monthlyPayment,
                loanLengthMonths,
                monthlyInsurance);

        registerCar(car, true);
    }

    private void registerCar(CarDetails car, boolean showMessage) {
        calculationQueue.add(car);
        carsByMonthlyPayment.add(car);
        addModelByBrand(car.getCarName());

        if (showMessage) {
            System.out.println("Queued car: " + car.getCarName());
        }
    }

    private void addModelByBrand(String carName) {
        String trimmedName = carName == null ? "" : carName.trim();
        if (trimmedName.isEmpty()) {
            return;
        }

        String[] nameParts = trimmedName.split("\\s+", 2);
        String brandKey = nameParts[0].toLowerCase(Locale.ROOT);
        String modelName = nameParts.length > 1 ? nameParts[1] : "(model not provided)";

        modelsByBrand.putIfAbsent(brandKey, new ArrayList<>());
        modelsByBrand.get(brandKey).add(modelName);
    }

    public void processQueue() {
        if (calculationQueue.isEmpty()) {
            System.out.println("No cars are waiting in the queue.");
            return;
        }

        while (!calculationQueue.isEmpty()) {
            CarDetails car = calculationQueue.poll();
            double totalCost = car.calculateEstimatedOwnershipCost();

            System.out.printf("%nProcessed %s%n", car.getCarName());
            System.out.printf("Estimated ownership cost: %s%n", CURRENCY_FORMAT.format(totalCost));
            displayCostBreakdown(car);

            history.addComparison(car.getCarName(), totalCost);
            carCostTree.insert(car.getCarName(), totalCost);
        }
    }

    private void displayCostBreakdown(CarDetails car) {
        System.out.println("Cost breakdown:");
        System.out.printf("Purchase price: %s%n", CURRENCY_FORMAT.format(car.getPurchasePrice()));
        System.out.printf("Sales tax: %s%n", CURRENCY_FORMAT.format(car.calculateSalesTax()));
        System.out.printf(
                "Total loan payments over %d months: %s%n",
                car.getLoanLengthMonths(),
                CURRENCY_FORMAT.format(car.calculateTotalLoanPayments()));
        System.out.printf("Estimated finance cost: %s%n", CURRENCY_FORMAT.format(car.calculateEstimatedFinanceCost()));
        System.out.printf(
                "Insurance over %d months: %s%n",
                car.getLoanLengthMonths(),
                CURRENCY_FORMAT.format(car.calculateTotalInsuranceCost()));
        System.out.printf(
                "Estimated depreciation over %s years: %s%n",
                YEARS_FORMAT.format(car.calculateOwnershipYears()),
                CURRENCY_FORMAT.format(car.calculateEstimatedDepreciationCost()));
        System.out.println("Ownership total uses depreciation, sales tax, finance cost, and insurance.");
    }

    public void displayCarsByMonthlyPayment() {
        if (carsByMonthlyPayment.isEmpty()) {
            System.out.println("No cars are available to sort.");
            return;
        }

        ArrayList<CarDetails> sortedCars = new ArrayList<>(carsByMonthlyPayment);
        bubbleSortByMonthlyPayment(sortedCars);

        System.out.println("\nCars sorted by monthly payment:");
        for (CarDetails car : sortedCars) {
            System.out.println(car);
        }
    }

    private static void bubbleSortByMonthlyPayment(ArrayList<CarDetails> cars) {
        int numberOfCars = cars.size();
        for (int i = 0; i < numberOfCars - 1; i++) {
            for (int j = 0; j < numberOfCars - i - 1; j++) {
                if (cars.get(j).getMonthlyPayment() > cars.get(j + 1).getMonthlyPayment()) {
                    CarDetails currentCar = cars.get(j);
                    cars.set(j, cars.get(j + 1));
                    cars.set(j + 1, currentCar);
                }
            }
        }
    }

    public void displayCarsByBrand() {
        if (modelsByBrand.isEmpty()) {
            System.out.println("No cars are available to group by brand.");
            return;
        }

        String brandInput = readNonEmptyLine("\nEnter a car brand: ");
        List<String> models = modelsByBrand.get(brandInput.toLowerCase(Locale.ROOT));

        if (models == null || models.isEmpty()) {
            System.out.println("No models were found for that brand.");
            return;
        }

        System.out.printf("Models for %s:%n", brandInput);
        for (String model : models) {
            System.out.println("- " + model);
        }
    }

    public void addCarFromUserInput() {
        String carName = readNonEmptyLine("\nEnter car name: ");
        double purchasePrice = readMinimumDouble("Enter purchase price (minimum $2,000): ", 2000.0);
        double annualDepreciationRate = readRate("Enter annual depreciation rate as a decimal (for example, 0.15): ");
        double taxRate = readRate("Enter tax rate as a decimal (for example, 0.08): ");
        double monthlyPayment = readMinimumDouble("Enter monthly loan payment (minimum $150): ", 150.0);
        int loanLengthMonths = readMinimumInt("Enter loan length in months (minimum 24): ", 24);
        double monthlyInsurance = readMinimumDouble("Enter monthly insurance cost (minimum $150): ", 150.0);

        addCar(
                carName,
                purchasePrice,
                annualDepreciationRate,
                taxRate,
                monthlyPayment,
                loanLengthMonths,
                monthlyInsurance);

        System.out.println("Car added successfully.");
    }

    public void displaySortedCars() {
        carCostTree.displaySortedCars();
    }

    public void displayLeastExpensiveCar() {
        carCostTree.displayLeastExpensiveCar();
    }

    public void displayMostExpensiveCar() {
        carCostTree.displayMostExpensiveCar();
    }

    public void clearComparisonData() {
        history.clearHistory();
        calculationQueue.clear();
        carCostTree.clear();
        System.out.println("Pending queue cleared.");
    }

    public void displayMenu() {
        int choice;

        do {
            printMenu();
            choice = readMenuChoice();

            switch (choice) {
                case 1:
                    addCarFromUserInput();
                    break;
                case 2:
                    processQueue();
                    break;
                case 3:
                    history.displayHistory();
                    break;
                case 4:
                    displaySortedCars();
                    break;
                case 5:
                    displayCarsByMonthlyPayment();
                    break;
                case 6:
                    displayLeastExpensiveCar();
                    break;
                case 7:
                    displayMostExpensiveCar();
                    break;
                case 8:
                    displayCarsByBrand();
                    break;
                case 9:
                    clearComparisonData();
                    break;
                case 10:
                    history.restoreLastClearedHistory();
                    break;
                case 11:
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid menu choice.");
            }
        } while (choice != 11);
    }

    private void printMenu() {
        System.out.println("\n==============================");
        System.out.println(" Car Ownership Cost Comparator");
        System.out.println("==============================");
        System.out.println("1. Add car");
        System.out.println("2. Process queued cars");
        System.out.println("3. View comparison history");
        System.out.println("4. View cars by estimated ownership cost");
        System.out.println("5. View cars by monthly payment");
        System.out.println("6. View least expensive car");
        System.out.println("7. View most expensive car");
        System.out.println("8. View models by brand");
        System.out.println("9. Clear history, queue, and tree");
        System.out.println("10. Restore last cleared history");
        System.out.println("11. Exit");
    }

    private int readMenuChoice() {
        while (true) {
            String input = readNonEmptyLine("Enter your choice: ");

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 11) {
                    return choice;
                }
            } catch (NumberFormatException exception) {
                // Re-prompt below with the same message.
            }

            System.out.println("Please enter a number from 1 to 11.");
        }
    }

    private String readNonEmptyLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be blank.");
        }
    }

    private double readMinimumDouble(String prompt, double minimumValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (value >= minimumValue) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // Re-prompt below with the same message.
            }

            System.out.printf("Please enter a number greater than or equal to %s.%n", CURRENCY_FORMAT.format(minimumValue));
        }
    }

    private int readMinimumInt(String prompt, int minimumValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= minimumValue) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // Re-prompt below with the same message.
            }

            System.out.printf("Please enter an integer greater than or equal to %d.%n", minimumValue);
        }
    }

    private double readRate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (value > 0.0 && value < 1.0) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // Re-prompt below with the same message.
            }

            System.out.println("Please enter a decimal value greater than 0 and less than 1.");
        }
    }

    private void loadSampleCars() {
        registerCar(new CarDetails("Toyota Supra", 30000, 0.15, 0.08, 500, 60, 200), false);
        registerCar(new CarDetails("Chevy Silverado", 28000, 0.12, 0.07, 480, 60, 300), false);
        registerCar(new CarDetails("BMW M3", 45000, 0.20, 0.09, 750, 72, 500), false);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            CarComparisonApp app = new CarComparisonApp(scanner);
            app.loadSampleCars();
            app.displayMenu();
        }
    }
}
