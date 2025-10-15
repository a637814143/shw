package com.example.housekeeping.converter;

import com.example.housekeeping.enums.AccountRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountRoleConverter implements AttributeConverter<AccountRole, String> {

    @Override
    public String convertToDatabaseColumn(AccountRole attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AccountRole convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AccountRole.fromValue(dbData);
    }
}
