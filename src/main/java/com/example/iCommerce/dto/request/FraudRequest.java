package com.example.iCommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FraudRequest {

    String user_id;

    @JsonProperty("Transaction Amount")
    double transactionAmount;

    @JsonProperty("Quantity")
    int quantity;

    @JsonProperty("Customer Age")
    int customerAge;

    @JsonProperty("Account Age Days")
    int accountAgeDays;

    @JsonProperty("Transaction Hour")
    int transactionHour;

    @JsonProperty("Transaction Date")
    String transactionDate;

    @JsonProperty("Payment Method")
    String paymentMethod;

    @JsonProperty("Device Used")
    String deviceUsed;
}
