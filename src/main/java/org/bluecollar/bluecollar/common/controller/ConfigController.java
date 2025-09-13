package org.bluecollar.bluecollar.common.controller;

import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @GetMapping("/translations")
    public BlueCollarApiResponse<Map<String, String>> getTranslations(
            @RequestParam String pageType,
            @RequestHeader(value = "X-Accept-Language", defaultValue = "en") String language) {
        
        Query query = new Query(Criteria.where("pageType").is(pageType.toUpperCase())
                .and("language").is(language));
        
        Map<String, Object> result = mongoTemplate.findOne(query, Map.class, "config");
        
        if (result != null && result.containsKey("translations")) {
            Map<String, String> translations = (Map<String, String>) result.get("translations");
            return new BlueCollarApiResponse<>(translations, 200);
        }
        return new BlueCollarApiResponse<>(Map.of(), 404);
    }
}