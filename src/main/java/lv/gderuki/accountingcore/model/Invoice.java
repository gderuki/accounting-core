package lv.gderuki.accountingcore.model;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable snapshot. Draft edits produce a new snapshot with the same internal ID.
 * Lifecycle operations and business validation are not implemented yet.
 */
@Getter
public final class Invoice {
    private final UUID id;
    private final UUID organizationId;
    @Nullable
    private final String buyer;
    @Nullable
    private final String seller;
    private final InvoiceStatus status;
    @Nullable
    private final String documentNumber;
    @Nullable
    private final LocalDate issueDate;
    private final String currency = "EUR";
    @Nullable
    private final BigDecimal calculatedTotal;
    private final List<InvoiceLine> invoiceLines;

    public Invoice(UUID id, UUID organizationId, @Nullable String buyer, @Nullable String seller,
                   InvoiceStatus status, @Nullable String documentNumber, @Nullable LocalDate issueDate,
                   List<InvoiceLine> invoiceLines) {
        this.id = Objects.requireNonNull(id, "id");
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.buyer = buyer;
        this.seller = seller;
        this.status = Objects.requireNonNull(status, "status");
        this.documentNumber = documentNumber;
        this.issueDate = issueDate;
        var linesSnapshot = List.copyOf(invoiceLines);
        this.invoiceLines = linesSnapshot;

        if (linesSnapshot.isEmpty()) {
            this.calculatedTotal = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        } else if (linesSnapshot.stream().anyMatch(i -> i.getCalculatedTotal() == null)) {
            this.calculatedTotal = null;
        } else {
            this.calculatedTotal = linesSnapshot.stream()
                    .map(InvoiceLine::getCalculatedTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public List<InvoiceValidationError> validateDraft() {
        var errors = new ArrayList<InvoiceValidationError>();

        for (var line : invoiceLines) {
            var quantity = line.getQuantity();

            if (quantity != null && quantity.signum() <= 0) {
                errors.add(new InvoiceValidationError(
                        "AC_01", // TBD
                        "quantity",
                        line.getId(),
                        "Quantity must be greater than zero"
                ));
            }

            var unitPrice = line.getUnitPrice();
            if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                errors.add(new InvoiceValidationError(
                        "AC_02", // TBD
                        "unitPrice",
                        line.getId(),
                        "Unit price must be greater than or equal to zero"
                ));
            }
        }

        return List.copyOf(errors);
    }
}
