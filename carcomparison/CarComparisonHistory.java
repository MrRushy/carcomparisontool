package carcomparison;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class CarComparisonHistory {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");

    private static class ComparisonRecord {
        private final String carName;
        private final double totalCost;

        private ComparisonRecord(String carName, double totalCost) {
            this.carName = carName;
            this.totalCost = totalCost;
        }

        @Override
        public String toString() {
            return carName + " -> " + CURRENCY_FORMAT.format(totalCost);
        }
    }

    private final LinkedList<ComparisonRecord> history = new LinkedList<>();
    private final Stack<LinkedList<ComparisonRecord>> clearedHistorySnapshots = new Stack<>();

    public void addComparison(String carName, double totalCost) {
        ComparisonRecord comparison = new ComparisonRecord(carName, totalCost);
        history.add(comparison);
        System.out.println("Added to history: " + comparison);
    }

    public void displayHistory() {
        if (history.isEmpty()) {
            System.out.println("No comparison history is available.");
            return;
        }

        System.out.println("\nComparison history:");
        for (ComparisonRecord comparison : history) {
            System.out.println(comparison);
        }
    }

    public void clearHistory() {
        if (history.isEmpty()) {
            System.out.println("History is already empty.");
            return;
        }

        clearedHistorySnapshots.push(new LinkedList<>(history));
        history.clear();
        System.out.println("History cleared.");
    }

    public void restoreLastClearedHistory() {
        if (clearedHistorySnapshots.isEmpty()) {
            System.out.println("There is no cleared history to restore.");
            return;
        }

        if (!history.isEmpty()) {
            System.out.println("History already has entries. Clear it before restoring the last cleared history.");
            return;
        }

        List<ComparisonRecord> restoredHistory = clearedHistorySnapshots.pop();
        history.addAll(restoredHistory);
        System.out.println("Last cleared history restored.");
    }
}
