package in.ybuilds.veritrust.backend.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NumericBooleanConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean value) {
        if (value == null) {
            return null;
        }

        return value ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }

        return value == 1;
    }
}