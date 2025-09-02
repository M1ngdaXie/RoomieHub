package com.campusnest.campusnest_platform.services.Impl;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class UniversityValidator {

    // Start with hardcoded list, move to database later
    private final Set<String> validDomains = Set.of(
            "stanford.edu",
            "berkeley.edu",
            "mit.edu",
            "harvard.edu",
            "yale.edu",
            "northeastern.edu"
            // Add more as needed
    );

    private final Map<String, String> domainToName = Map.of(
            "stanford.edu", "Stanford University",
            "berkeley.edu", "UC Berkeley",
            "mit.edu", "Massachusetts Institute of Technology",
            "harvard.edu", "Harvard University",
            "yale.edu", "Yale University",
            "northeastern.edu", "Northeastern University"
    );


    public boolean isValidUniversityDomain(String domain) {
        return validDomains.contains(domain.toLowerCase());
    }

    public String validateAndGetName(String domain) {
        if (validDomains.contains(domain.toLowerCase())) {
            return domainToName.getOrDefault(domain.toLowerCase(), domain);
        }
        return null;
    }

}