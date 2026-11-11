package lv.gderuki.accountingcore.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceLineTest {

    @Test
    void calculatesTotalWithHalfUpRoundingWithoutRoundingUnitPriceFirst() {
        var quantity = new BigDecimal("3");
        var unitPrice = new BigDecimal("0.335");

        var line = new InvoiceLine(UUID.randomUUID(), "Development", quantity, "HOUR", unitPrice);

        assertEquals(new BigDecimal("1.01"), line.getCalculatedTotal());
    }

    @ParameterizedTest
    @CsvSource({",50", "3,", ","})
    void leavesTotalAbsentWhenCalculationInputsAreMissing(BigDecimal quantity, BigDecimal unitPrice) {
        var line = new InvoiceLine(UUID.randomUUID(), null, quantity, null, unitPrice);

        assertNull(line.getCalculatedTotal());
    }
}
