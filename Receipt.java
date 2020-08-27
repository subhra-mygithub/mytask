package assignment;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.math.*;
import java.text.DecimalFormat;

public class Receipt {

	private ArrayList<Product> productsList = new ArrayList<Product>();
	private double total;
	private double taxTotal;

	@SuppressWarnings("resource")
	public Receipt(String inputFileName) {
		try {
			Scanner input = new Scanner(System.in);
			File file = new File(inputFileName);
			input = new Scanner(file);

			while (input.hasNextLine()) {

				String line = input.nextLine();
				String[] words = line.split(" ");
				int qty = Integer.parseInt(words[0]);
				boolean isImported = line.contains("imported");
				String[] exemptedItems = new String[] { "book", "chocolate", "medicine" };
				int exemptedItemIndex = containsItemFromArray(line, exemptedItems);
				String exemptedType = null;

				if (exemptedItemIndex != -1) {
					exemptedType = exemptedItems[exemptedItemIndex];
				}
				int splitIndex = line.lastIndexOf("at");
				if (splitIndex == -1) {
					System.out.println("Bad Formatting");

				} else {

					float price = Float.parseFloat((line.substring(splitIndex + 2))); 

					String name = line.substring(1, splitIndex); 

					for (int i = 0; i < qty; i++) {
						

						Product newProduct = null;

						if (isImported) {
						
							if (exemptedType != null) {
							

								if (exemptedType == "book") {
									newProduct = new Product(name, price, ItemType.IMPORTED_BOOK);
								} else if (exemptedType == "pills") {
									newProduct = new Product(name, price, ItemType.IMPORTED_MEDICAL);
								} else if (exemptedType == "chocolate") {
									newProduct = new Product(name, price, ItemType.IMPORTED_FOOD);
								}

							} else {
								// the product is imported and sales taxed
								newProduct = new Product(name, price, ItemType.IMPORTED_OTHERS);
							}

						} else {
							
							if (exemptedType != null) {
								

								if (exemptedType == "book") {
									newProduct = new Product(name, price, ItemType.BOOK);
								} else if (exemptedType == "pills") {
									newProduct = new Product(name, price, ItemType.MEDICAL);
								} else if (exemptedType == "chocolate") {
									newProduct = new Product(name, price, ItemType.FOOD);
								}

							} else {
								
								newProduct = new Product(name, price, ItemType.OTHERS);
							}
						}

						productsList.add(newProduct); 
					}
				}

			}
			input.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void calculateTotals() {
		int numOfItems = productsList.size();

		BigDecimal runningSum = new BigDecimal("0");
		BigDecimal runningTaxSum = new BigDecimal("0");

		for (int i = 0; i < numOfItems; i++) {

			runningTaxSum = BigDecimal.valueOf(0);

			BigDecimal totalBeforeTax = new BigDecimal(String.valueOf(this.productsList.get(i).getPrice()));

			runningSum = runningSum.add(totalBeforeTax);

			if (productsList.get(i).isSalesTaxable()) {
				BigDecimal salesTaxPercent = new BigDecimal(".10");
				BigDecimal salesTax = salesTaxPercent.multiply(totalBeforeTax);
				salesTax = round(salesTax, BigDecimal.valueOf(0.05), RoundingMode.UP);
				runningTaxSum = runningTaxSum.add(salesTax);

			}

			if (productsList.get(i).isImportedTaxable()) {
				BigDecimal importTaxPercent = new BigDecimal(".05");
				BigDecimal importTax = importTaxPercent.multiply(totalBeforeTax);

				importTax = round(importTax, BigDecimal.valueOf(0.05), RoundingMode.UP);
				runningTaxSum = runningTaxSum.add(importTax);

			}

			productsList.get(i).setPrice(runningTaxSum.floatValue() + productsList.get(i).getPrice());
			taxTotal += runningTaxSum.doubleValue();
			runningSum = runningSum.add(runningTaxSum);
		}
		taxTotal = roundTwoDecimals(taxTotal);
		total = runningSum.doubleValue();
	}

	public void setTotal(BigDecimal amount) {
		total = amount.doubleValue();
	}

	public double getTotal() {
		return total;
	}

	public void setSalesTaxTotal(BigDecimal amount) {
		taxTotal = amount.doubleValue();
	}

	public double getSalesTaxTotal() {
		return taxTotal;
	}

	public static int containsItemFromArray(String inputString, String[] items) {
		int index = -1;
		for (int i = 0; i < items.length; i++) {
			index = inputString.indexOf(items[i]);
			if (index != -1)
				return i;
		}
		return -1;

	}

	public static BigDecimal round(BigDecimal value, BigDecimal increment, RoundingMode roundingMode) {
		if (increment.signum() == 0) {
			return value;
		} else {
			BigDecimal divided = value.divide(increment, 0, roundingMode);
			BigDecimal result = divided.multiply(increment);
			result.setScale(2, RoundingMode.UNNECESSARY);
			return result;
		}
	}

	public double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	public void printReceipt() {
		int numOfItems = productsList.size();
		for (int i = 0; i < numOfItems; i++) {
			System.out.println("1" + productsList.get(i).getName() + "at " + productsList.get(i).getPrice());
		}
		System.out.printf("Sales Tax: %.2f\n", taxTotal);
		System.out.println("Total: " + total);
	}

}
