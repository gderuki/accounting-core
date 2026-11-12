package lv.gderuki.accountingcore.model;

import java.util.UUID;

public record InvoiceValidationError(String code, String field, UUID lineId, String message) {

}
