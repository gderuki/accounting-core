package lv.gderuki.accountingcore.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceLineTest {

    @ParameterizedTest
    @CsvSource({
            "3, 0.335, 1.01",
            "2.5, 12.345, 30.86",
            "10, 50, 500.00",
            "1, 0, 0.00"
    })
    void calculatesTotalWithHalfUpRoundingWithoutRoundingUnitPriceFirst(BigDecimal quantity, BigDecimal unitPrice, BigDecimal expectedTotal) {
        var line = new InvoiceLine(UUID.randomUUID(), "Development", quantity, "HOUR", unitPrice);

        assertEquals(expectedTotal, line.getCalculatedTotal());
    }

    @ParameterizedTest
    @CsvSource({",50", "3,", ","})
    void leavesTotalAbsentWhenCalculationInputsAreMissing(BigDecimal quantity, BigDecimal unitPrice) {
        var line = new InvoiceLine(UUID.randomUUID(), null, quantity, null, unitPrice);

        assertNull(line.getCalculatedTotal());
    }
}
