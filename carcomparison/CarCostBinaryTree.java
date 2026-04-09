package carcomparison;

import java.text.DecimalFormat;

public class CarCostBinaryTree {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");

    private static class TreeNode {
        private final String carName;
        private final double totalCost;
        private TreeNode left;
        private TreeNode right;

        private TreeNode(String carName, double totalCost) {
            this.carName = carName;
            this.totalCost = totalCost;
        }
    }

    private TreeNode root;

    public void insert(String carName, double totalCost) {
        root = insert(root, carName, totalCost);
    }

    private TreeNode insert(TreeNode currentNode, String carName, double totalCost) {
        if (currentNode == null) {
            return new TreeNode(carName, totalCost);
        }

        int costComparison = Double.compare(totalCost, currentNode.totalCost);
        if (costComparison < 0 || (costComparison == 0 && carName.compareToIgnoreCase(currentNode.carName) < 0)) {
            currentNode.left = insert(currentNode.left, carName, totalCost);
        } else {
            currentNode.right = insert(currentNode.right, carName, totalCost);
        }

        return currentNode;
    }

    public void displaySortedCars() {
        if (root == null) {
            System.out.println("No processed cars are available yet.");
            return;
        }

        System.out.println("\nCars sorted by estimated ownership cost:");
        inOrderTraversal(root);
    }

    private void inOrderTraversal(TreeNode currentNode) {
        if (currentNode == null) {
            return;
        }

        inOrderTraversal(currentNode.left);
        System.out.printf("%s -> %s%n", currentNode.carName, CURRENCY_FORMAT.format(currentNode.totalCost));
        inOrderTraversal(currentNode.right);
    }

    public void displayLeastExpensiveCar() {
        if (root == null) {
            System.out.println("No processed cars are available yet.");
            return;
        }

        TreeNode currentNode = root;
        while (currentNode.left != null) {
            currentNode = currentNode.left;
        }

        System.out.printf("Least expensive car: %s -> %s%n", currentNode.carName, CURRENCY_FORMAT.format(currentNode.totalCost));
    }

    public void displayMostExpensiveCar() {
        if (root == null) {
            System.out.println("No processed cars are available yet.");
            return;
        }

        TreeNode currentNode = root;
        while (currentNode.right != null) {
            currentNode = currentNode.right;
        }

        System.out.printf("Most expensive car: %s -> %s%n", currentNode.carName, CURRENCY_FORMAT.format(currentNode.totalCost));
    }

    public void clear() {
        root = null;
        System.out.println("Cost ranking tree cleared.");
    }
}
