package com.jakub.bone.domain;

import lombok.Getter;

@Getter
public class SwiftCode {
    private String countryIso2;
    private String swiftCode;
    private String bankName;
    private String address;
    private String town;
    private String country;
    private boolean isHeadquarter;

    public SwiftCode(String countryIso2, String swiftCode, String bankName,
                     String address, String town, String country) {
        this.countryIso2 = countryIso2;
        this.swiftCode = swiftCode;
        this.bankName = bankName;
        this.address = address;
        this.town = town;
        this.country = country;
        this.isHeadquarter = swiftCode.endsWith("XXX");
    }
}
