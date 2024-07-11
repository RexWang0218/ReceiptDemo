import java.math.BigDecimal;
import java.util.Map;

public class ReceiptApplication {
    enum ProductCategory {
        GENERAL, FOOD, CLOTHING
    }

    private static final Map<String, ProductCategory> productCategoryMap = Map.of(
            "book", ProductCategory.GENERAL,
            "potato chips", ProductCategory.FOOD,
            "pencil", ProductCategory.GENERAL,
            "shirt", ProductCategory.CLOTHING
    );
    static double basicUnit = 0.05;

    public static void main(String[] args) {
        try {
            // CASE 1
            System.out.println("CASE 1");
            printReceipt(new ProductInfo[]{new ProductInfo("book", 17.99, 1),
                            new ProductInfo("potato chips", 3.99, 1)},
                    Location.CA);

            // CASE 2
            System.out.println("CASE 2");
            printReceipt(new ProductInfo[]{new ProductInfo("book", 17.99, 1),
                            new ProductInfo("pencil", 2.99, 3)},
                    Location.NY);

            // CASE 3
            System.out.println("CASE 3");
            printReceipt(new ProductInfo[]{new ProductInfo("pencil", 2.99, 2),
                            new ProductInfo("shirt", 29.99, 1)},
                    Location.NY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printReceipt(ProductInfo[] productInfos, Location location) {
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        String formatPattern = "%-12s %10s %10s";

        BigDecimal productAmount;
        System.out.println(String.format(formatPattern, "item", "price", "qty"));
        System.out.println("");

        for (ProductInfo productInfo : productInfos) {
            productAmount = productInfo.getPrice().multiply(productInfo.getQuantity());
            totalAmount = totalAmount.add(productAmount);

            if (productCategoryMap.containsKey(productInfo.getItem()) && location.isExempt(productCategoryMap.get(productInfo.getItem()))) {
                totalTax = totalTax.add(BigDecimal.ZERO);
            } else {
                totalTax = totalTax.add(calculateSalesTax(productAmount, BigDecimal.valueOf(location.getTaxRate())));
            }

            System.out.println(String.format(formatPattern, productInfo.getItem(), "$" + productInfo.getPrice(), productInfo.getQuantity()));
        }

        System.out.println(String.format(formatPattern, "subtotal:", "", "$" + totalAmount));
        System.out.println(String.format(formatPattern, "tax:", "", "$" + totalTax.setScale(2)));
        System.out.println(String.format(formatPattern, "total:", "", "$" + totalTax.setScale(2).add(totalAmount)));
        System.out.println(System.lineSeparator());
    }

    private static BigDecimal calculateSalesTax(BigDecimal productAmount, BigDecimal taxRate) {
        BigDecimal basicUnitBigDecimal = BigDecimal.valueOf(basicUnit);
        BigDecimal originalTax = productAmount.multiply(taxRate);

        BigDecimal[] result = originalTax.divideAndRemainder(basicUnitBigDecimal);
        BigDecimal divide = result[0];
        BigDecimal remainder = result[1];

        BigDecimal tax;
        if (remainder.compareTo(BigDecimal.ZERO) > 0) {
            tax = divide.add(BigDecimal.valueOf(1)).multiply(basicUnitBigDecimal);
        } else {
            tax = divide.multiply(basicUnitBigDecimal);
        }

        return tax;
    }

    enum Location {
        CA(0.0975, ProductCategory.FOOD),                           // California
        NY(0.08875, ProductCategory.FOOD, ProductCategory.CLOTHING);// New York

        private double taxRate;
        private ProductCategory[] exemptProductCategories;

        Location(double taxRate, ProductCategory... exemptProductCategories) {
            this.taxRate = taxRate;
            this.exemptProductCategories = exemptProductCategories;
        }

        public double getTaxRate() {
            return taxRate;
        }

        public boolean isExempt(ProductCategory productCategory) {
            for (ProductCategory pc : exemptProductCategories) {
                if (pc.equals(productCategory))
                    return true;
            }

            return false;
        }
    }
}

class ProductInfo {
    private String item;
    private BigDecimal price;
    private BigDecimal quantity;

    public ProductInfo(String item,
                       double price,
                       int quantity) {
        this.item = item;
        this.price = BigDecimal.valueOf(price);
        this.quantity = BigDecimal.valueOf(quantity);
    }

    public String getItem() {
        return item;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
