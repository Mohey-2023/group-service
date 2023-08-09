package com.mohey.groupservice.interprocess.dto;

public enum GenderEnum {
    MAN("남"), WOMAN("여");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
