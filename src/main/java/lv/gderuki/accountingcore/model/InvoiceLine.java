package lv.gderuki.accountingcore.model;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
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
}
