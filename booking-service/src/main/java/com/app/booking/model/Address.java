package com.app.booking.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

  private String addressLine1;
  private String city;
  private String state;
  private String zipCode;
}

