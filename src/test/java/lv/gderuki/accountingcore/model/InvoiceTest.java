package lv.gderuki.accountingcore.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class InvoiceTest {

    @Test
    void changingSourceListDoesNotChangeInvoiceLines() {
        var invoiceLines = new ArrayList<InvoiceLine>();
        final var testLineUUID = UUID.randomUUID();
        var testLine = new InvoiceLine(testLineUUID, null, null, null, null, null);
        invoiceLines.add(testLine);
        var invoice = new Invoice(UUID.randomUUID(), UUID.randomUUID(), null, null, InvoiceStatus.DRAFT, null, null, BigDecimal.ZERO, invoiceLines);

        invoiceLines.clear();

        assertEquals(testLineUUID, invoice.getLines().getFirst().getId());
        assertEquals(1, invoice.getLines().size());
    }

    @Test
    void changingSourceListViaGetterThrowsAnException() {
        var invoiceLines = new ArrayList<InvoiceLine>();
        final var testLineUUID = UUID.randomUUID();
        var testLine = new InvoiceLine(testLineUUID, null, null, null, null, null);
        invoiceLines.add(testLine);
        var invoice = new Invoice(UUID.randomUUID(), UUID.randomUUID(), null, null, InvoiceStatus.DRAFT, null, null, BigDecimal.ZERO, invoiceLines);

        assertThrows(UnsupportedOperationException.class, () -> invoice.getLines().add(testLine));
        assertEquals(testLineUUID, invoice.getLines().getFirst().getId());
        assertEquals(1, invoice.getLines().size());
    }

}
