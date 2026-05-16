package smartreceipt;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReceiptManagerTest {

    private Receipt r1() {
        return new Receipt("B003", LocalDate.of(2026, 5, 13), "Opal", "Transport", 25.00, "Weekly travel");
    }

    private Receipt r2() {
        return new Receipt("B001", LocalDate.of(2026, 5, 11), "Woolworths", "Grocery", 45.50, "Weekly shopping");
    }

    private Receipt r3() {
        return new Receipt("B002", LocalDate.of(2026, 5, 12), "Kmart", "Shopping", 80.00, "Clothes");
    }

    @Test
    void addReceipt_shouldAddValidReceipt() {
        ReceiptManager manager = new ReceiptManager(new ArrayList<>());

        manager.addReceipt(r1());

        assertEquals(1, manager.getReceipts().size());
        assertEquals("B003", manager.getReceipts().get(0).getBillNo());
        assertTrue(manager.getReceipts().get(0).getAmount() > 0);
    }

    @Test
    void addReceipt_shouldRejectDuplicateBillNumber() {
        ReceiptManager manager = new ReceiptManager(new ArrayList<>());

        manager.addReceipt(r1());

        assertThrows(IllegalArgumentException.class, () -> {
            manager.addReceipt(r1());
        });
    }

    @Test
    void searchKeyword_shouldFindMatchingReceipt() {
        List<Receipt> receipts = new ArrayList<>();
        receipts.add(r1());
        receipts.add(r2());
        receipts.add(r3());

        ReceiptManager manager = new ReceiptManager(receipts);

        List<Receipt> result = manager.searchKeyword("grocery");

        assertEquals(1, result.size());
        assertEquals("B001", result.get(0).getBillNo());
        assertTrue(result.get(0).getCategory().equalsIgnoreCase("Grocery"));
    }

    @Test
    void sortByAmount_shouldReturnReceiptsInAscendingOrder() {
        List<Receipt> receipts = new ArrayList<>();
        receipts.add(r3());
        receipts.add(r1());
        receipts.add(r2());

        ReceiptManager manager = new ReceiptManager(receipts);

        List<Receipt> sorted = manager.sortByAmount();

        assertEquals("B003", sorted.get(0).getBillNo());
        assertEquals("B001", sorted.get(1).getBillNo());
        assertEquals("B002", sorted.get(2).getBillNo());

        assertTrue(sorted.get(0).getAmount() <= sorted.get(1).getAmount());
        assertTrue(sorted.get(1).getAmount() <= sorted.get(2).getAmount());
    }

    @Test
    void binarySearchByBillNo_shouldFindCorrectReceipt() {
        List<Receipt> receipts = new ArrayList<>();
        receipts.add(r3());
        receipts.add(r1());
        receipts.add(r2());

        ReceiptManager manager = new ReceiptManager(receipts);

        Receipt found = manager.binarySearchByBillNo("B002");

        assertNotNull(found);
        assertEquals("Kmart", found.getShopName());
        assertEquals("Shopping", found.getCategory());
    }

    @Test
    void monthlyTotal_shouldCalculateCorrectTotal() {
        List<Receipt> receipts = new ArrayList<>();
        receipts.add(r1());
        receipts.add(r2());
        receipts.add(r3());

        ReceiptManager manager = new ReceiptManager(receipts);

        double total = manager.getMonthlyTotal(2026, 5);

        assertEquals(150.50, total);
        assertNotEquals(0, total);
    }
}