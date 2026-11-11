package lv.gderuki.accountingcore.model;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final BigDecimal calculatedTotal;
    private final List<InvoiceLine> lines;

    public Invoice(UUID id, UUID organizationId, @Nullable String buyer, @Nullable String seller,
                   InvoiceStatus status, @Nullable String documentNumber, @Nullable LocalDate issueDate,
                   BigDecimal calculatedTotal, List<InvoiceLine> lines) {
        this.id = Objects.requireNonNull(id, "id");
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.buyer = buyer;
        this.seller = seller;
        this.status = Objects.requireNonNull(status, "status");
        this.documentNumber = documentNumber;
        this.issueDate = issueDate;
        this.calculatedTotal = Objects.requireNonNull(calculatedTotal, "calculatedTotal");
        this.lines = List.copyOf(lines);
    }
}
