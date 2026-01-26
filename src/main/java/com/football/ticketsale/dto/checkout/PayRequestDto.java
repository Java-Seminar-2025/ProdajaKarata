package com.football.ticketsale.dto.checkout;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PayRequestDto {
    private List<UUID> ticketIds;

    private String paymentMethod; // "MOCK_CARD" ili "PAYPAL"
    private String cardholderName;
    private String last4;
}
