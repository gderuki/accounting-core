package lv.gderuki.accountingcore.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class InvoiceTest {

    @Test
    void changingSourceListDoesNotChangeInvoiceLines() {
        var invoiceLines = new ArrayList<InvoiceLine>();
        final var testLineUUID = UUID.randomUUID();
        var testLine = new InvoiceLine(testLineUUID, null, null, null, null);
        invoiceLines.add(testLine);
        var invoice = new Invoice(UUID.randomUUID(), UUID.randomUUID(), null, null, InvoiceStatus.DRAFT, null, null, invoiceLines);

        invoiceLines.clear();

        assertEquals(testLineUUID, invoice.getInvoiceLines().getFirst().getId());
        assertEquals(1, invoice.getInvoiceLines().size());
    }

    @Test
    void changingSourceListViaGetterThrowsAnException() {
        var invoiceLines = new ArrayList<InvoiceLine>();
        final var testLineUUID = UUID.randomUUID();
        var testLine = new InvoiceLine(testLineUUID, null, null, null, null);
        invoiceLines.add(testLine);
        var invoice = new Invoice(UUID.randomUUID(), UUID.randomUUID(), null, null, InvoiceStatus.DRAFT, null, null, invoiceLines);

        assertThrows(UnsupportedOperationException.class, () -> invoice.getInvoiceLines().add(testLine));
        assertEquals(testLineUUID, invoice.getInvoiceLines().getFirst().getId());
        assertEquals(1, invoice.getInvoiceLines().size());
    }

    @ParameterizedTest
    @MethodSource("invoiceTotalCases")
    void calculatesTotalBasedOnInvoiceLines(List<InvoiceLine> invoiceLines, BigDecimal expectedTotal) {
        var invoice = new Invoice(UUID.randomUUID(), UUID.randomUUID(), null, null, InvoiceStatus.DRAFT, null, null, invoiceLines);
        assertEquals(expectedTotal, invoice.getCalculatedTotal());
    }

    private static Stream<Arguments> invoiceTotalCases() {
        return Stream.of(
                Arguments.of(List.<InvoiceLine>of(), new BigDecimal("0.00")),
                Arguments.of(
                        List.of(
                                new InvoiceLine(UUID.randomUUID(), null, new BigDecimal(2), null, null),
                                new InvoiceLine(UUID.randomUUID(), null, new BigDecimal(2), null, new BigDecimal("100.50"))
                        ), null),
                Arguments.of(
                        List.of(
                                new InvoiceLine(UUID.randomUUID(), null, new BigDecimal(2), null, new BigDecimal("50.00")),
                                new InvoiceLine(UUID.randomUUID(), null, new BigDecimal(8), null, new BigDecimal("50.00"))
                        ), new BigDecimal("500.00")),
                Arguments.of(
                        List.of(
                                new InvoiceLine(UUID.randomUUID(), null, new BigDecimal(1), null, BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY))
                        ), BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY)),
                Arguments.of(
                        List.of(
                                new InvoiceLine(UUID.randomUUID(), null, new BigDecimal(3), null, new BigDecimal("0.335")),
                                new InvoiceLine(UUID.randomUUID(), null, new BigDecimal(3), null, new BigDecimal("0.335"))
                        ), new BigDecimal("2.02"))
        );
    }

    @ParameterizedTest
    @MethodSource("draftValidationCases")
    void validatesDraftBasedOnSuppliedLineValues(
            List<InvoiceLine> lines,
            List<InvoiceValidationError> expectedErrors) {

        var invoice = new Invoice(
                UUID.randomUUID(), UUID.randomUUID(),
                null, null, InvoiceStatus.DRAFT, null, null, lines);

        assertEquals(expectedErrors, invoice.validateDraft());
    }

    private static Stream<Arguments> draftValidationCases() {
        var lineId = UUID.randomUUID();
        var secondLineId = UUID.randomUUID();

        return Stream.of(
                Arguments.of(
                        List.of(

                        ), List.<InvoiceValidationError>of()
                ),
                Arguments.of(
                        List.of(
                                new InvoiceLine(lineId, null, BigDecimal.ONE, null, BigDecimal.ONE.setScale(2, RoundingMode.UNNECESSARY))
                        ),
                        List.<InvoiceValidationError>of()
                ),
                Arguments.of(
                        List.of(
                                new InvoiceLine(lineId, null, BigDecimal.ONE, null, BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY))
                        ),
                        List.<InvoiceValidationError>of()
                ),
                Arguments.of(
                        List.of(new InvoiceLine(
                                lineId, null, BigDecimal.ONE.negate(),
                                null, new BigDecimal("10.00"))),
                        List.of(new InvoiceValidationError(
                                "AC_01",
                                "quantity",
                                lineId,
                                "Quantity must be greater than zero"
                        ))
                ),
                Arguments.of(
                        List.of(
                                new InvoiceLine(
                                        lineId, null, null,
                                        null, BigDecimal.ONE.negate())
                        ),
                        List.of(
                                new InvoiceValidationError(
                                        "AC_02",
                                        "unitPrice",
                                        lineId,
                                        "Unit price must be greater than or equal to zero"
                                )
                        )
                ),
                Arguments.of(
                        List.of(
                                new InvoiceLine(
                                        lineId, null, BigDecimal.ONE.negate(),
                                        null, null)
                        ),
                        List.of(
                                new InvoiceValidationError(
                                        "AC_01",
                                        "quantity",
                                        lineId,
                                        "Quantity must be greater than zero"
                                )
                        )
                ),
                Arguments.of(
                        List.of(
                                new InvoiceLine(
                                        lineId, null, BigDecimal.ZERO,
                                        null, BigDecimal.ONE.setScale(2, RoundingMode.UNNECESSARY)),
                                new InvoiceLine(
                                        secondLineId, null, BigDecimal.ONE,
                                        null, BigDecimal.ONE.negate().setScale(2, RoundingMode.UNNECESSARY))
                        ),
                        List.of(
                                new InvoiceValidationError(
                                        "AC_01",
                                        "quantity",
                                        lineId,
                                        "Quantity must be greater than zero"
                                ),
                                new InvoiceValidationError(
                                        "AC_02",
                                        "unitPrice",
                                        secondLineId,
                                        "Unit price must be greater than or equal to zero"
                                )
                        )
                ),
                Arguments.of(
                        List.of(new InvoiceLine(
                                lineId, null, BigDecimal.ONE,
                                null, new BigDecimal("-10.00"))),
                        List.of(new InvoiceValidationError(
                                "AC_02",
                                "unitPrice",
                                lineId,
                                "Unit price must be greater than or equal to zero"
                        ))
                ),
                Arguments.of(
                        List.of(
                                new InvoiceLine(
                                        lineId, null, BigDecimal.ZERO,
                                        null, BigDecimal.ONE.negate())
                        ),
                        List.of(
                                new InvoiceValidationError(
                                        "AC_01",
                                        "quantity",
                                        lineId,
                                        "Quantity must be greater than zero"
                                ),
                                new InvoiceValidationError(
                                        "AC_02",
                                        "unitPrice",
                                        lineId,
                                        "Unit price must be greater than or equal to zero"
                                )
                        )
                ),
                Arguments.of(
                        List.of(
                                new InvoiceLine(
                                        lineId, null, null,
                                        null, null)
                        ),
                        List.<InvoiceValidationError>of()
                )
        );
    }

    @Test
    void testCreateDraftInvoiceFactory() {
        UUID organizationId = UUID.randomUUID();

        Invoice draftInvoice = Invoice.createDraftInvoice(organizationId);

        assertNotNull(draftInvoice.getId());
        assertEquals(organizationId, draftInvoice.getOrganizationId());
        assertNull(draftInvoice.getBuyer());
        assertNull(draftInvoice.getSeller());
        assertEquals(InvoiceStatus.DRAFT, draftInvoice.getStatus());
        assertNull(draftInvoice.getDocumentNumber());
        assertNull(draftInvoice.getIssueDate());
        assertEquals(List.of(), draftInvoice.getInvoiceLines());
        assertEquals(new BigDecimal("0.00"), draftInvoice.getCalculatedTotal());
    }

    @Test
    void sameOrganizationIdProvidedWillCreateDifferentInvoices() {
        UUID organizationId = UUID.randomUUID();

        Invoice draftInvoice1 = Invoice.createDraftInvoice(organizationId);
        Invoice draftInvoice2 = Invoice.createDraftInvoice(organizationId);

        assertNotEquals(draftInvoice1.getId(), draftInvoice2.getId());
    }

    @Test
    void nullOrganizationIdWillThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> Invoice.createDraftInvoice(null));
    }
}
