package lv.gderuki.accountingcore.model;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
public final class InvoiceLine {
    private final UUID id;
    @Nullable
    private final String description;
    @Nullable
    private final BigDecimal quantity;
    @Nullable
    private final String unitOfMeasure;
    @Nullable
    private final BigDecimal unitPrice;
    @Nullable
    private final BigDecimal calculatedTotal;

    public InvoiceLine(UUID id, @Nullable String description, @Nullable BigDecimal quantity,
                       @Nullable String unitOfMeasure, @Nullable BigDecimal unitPrice) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
        this.unitPrice = unitPrice;
        this.calculatedTotal = quantity == null || unitPrice == null
                ? null
                : quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
