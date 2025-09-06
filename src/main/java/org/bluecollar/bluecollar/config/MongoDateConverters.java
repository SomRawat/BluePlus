package org.bluecollar.bluecollar.config;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Date;

/**
 * Converts legacy Google DateTime-like documents stored as
 * { value: <millis>, dateOnly: <bool>, tzShift: <int> } into java.util.Date.
 */
@ReadingConverter
public class MongoDateConverters {

    public static class GoogleDateTimeDocumentToDateConverter implements Converter<Document, Date> {
        @Override
        public Date convert(Document source) {
            if (source == null) return null;
            Object value = source.get("value");
            if (value instanceof Number) {
                return new Date(((Number) value).longValue());
            }
            return null;
        }
    }
}