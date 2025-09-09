package org.bluecollar.bluecollar.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class TranslationService {
    
    @Autowired
    private Translate translate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public String translateText(String text, String targetLanguage) {
        Translation translation = translate.translate(text, 
            Translate.TranslateOption.targetLanguage(targetLanguage));
        return translation.getTranslatedText();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T translateObject(T obj, String targetLanguage) {
        if (obj == null) return obj;
        
        try {
            T cloned = (T) objectMapper.readValue(
                objectMapper.writeValueAsString(obj), obj.getClass());
            translateFields(cloned, targetLanguage);
            return cloned;
        } catch (Exception e) {
            return obj;
        }
    }
    
    private void translateFields(Object obj, String targetLanguage) {
        if (obj == null) return;
        
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value instanceof String && !((String) value).isEmpty()) {
                    field.set(obj, translateText((String) value, targetLanguage));
                } else if (value instanceof List) {
                    ((List<?>) value).forEach(item -> translateFields(item, targetLanguage));
                } else if (value != null && !isPrimitiveOrWrapper(value.getClass())) {
                    translateFields(value, targetLanguage);
                }
            } catch (Exception ignored) {}
        }
    }
    
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == String.class || 
               clazz == Boolean.class || 
               clazz == Integer.class || 
               clazz == Long.class || 
               clazz == Double.class || 
               clazz == Float.class;
    }
}