package carcomparison;

import java.text.DecimalFormat;

public class CarDetails {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00%");

    private final String carName;
    private final double purchasePrice;
    private final double annualDepreciationRate;
    private final double taxRate;
    private final double monthlyPayment;
    private final int loanLengthMonths;
    private final double monthlyInsurance;

    public CarDetails(
            String carName,
            double purchasePrice,
            double annualDepreciationRate,
            double taxRate,
            double monthlyPayment,
            int loanLengthMonths,
            double monthlyInsurance) {
        this.carName = carName;
        this.purchasePrice = purchasePrice;
        this.annualDepreciationRate = annualDepreciationRate;
        this.taxRate = taxRate;
        this.monthlyPayment = monthlyPayment;
        this.loanLengthMonths = loanLengthMonths;
        this.monthlyInsurance = monthlyInsurance;
    }

    public String getCarName() {
        return carName;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getAnnualDepreciationRate() {
        return annualDepreciationRate;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public int getLoanLengthMonths() {
        return loanLengthMonths;
    }

    public double getMonthlyInsurance() {
        return monthlyInsurance;
    }

    public double calculateOwnershipYears() {
        return loanLengthMonths / 12.0;
    }

    public double calculateEstimatedDepreciationCost() {
        double depreciationFraction = Math.min(1.0, annualDepreciationRate * calculateOwnershipYears());
        return purchasePrice * depreciationFraction;
    }

    public double calculateSalesTax() {
        return purchasePrice * taxRate;
    }

    public double calculateTotalLoanPayments() {
        return monthlyPayment * loanLengthMonths;
    }

    public double calculateEstimatedFinanceCost() {
        return Math.max(0.0, calculateTotalLoanPayments() - purchasePrice);
    }

    public double calculateTotalInsuranceCost() {
        return monthlyInsurance * loanLengthMonths;
    }

    public double calculateEstimatedOwnershipCost() {
        return calculateEstimatedDepreciationCost()
                + calculateSalesTax()
                + calculateEstimatedFinanceCost()
                + calculateTotalInsuranceCost();
    }

    @Override
    public String toString() {
        return carName
                + " | Price: " + CURRENCY_FORMAT.format(purchasePrice)
                + " | Depreciation: " + PERCENT_FORMAT.format(annualDepreciationRate) + " annually"
                + " | Tax: " + PERCENT_FORMAT.format(taxRate)
                + " | Payment: " + CURRENCY_FORMAT.format(monthlyPayment) + "/month for " + loanLengthMonths + " months"
                + " | Insurance: " + CURRENCY_FORMAT.format(monthlyInsurance) + "/month";
    }
}
