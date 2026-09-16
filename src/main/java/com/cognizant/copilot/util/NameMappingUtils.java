package com.cognizant.copilot.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Name mapping utility for normalizing different name formats.
 */
public class NameMappingUtils {

    private static final Map<String, String> NAME_MAPPING = new HashMap<>();

    static {
        // Initialize name mappings from different formats to standard format
        NAME_MAPPING.put(StringUtils.normalize("Gilbert Roy P"), StringUtils.normalize("P, Gilbert Roy"));
        NAME_MAPPING.put(StringUtils.normalize("P,Gilbert Roy"), StringUtils.normalize("P, Gilbert Roy"));

        NAME_MAPPING.put(StringUtils.normalize("Jerold Joe"), StringUtils.normalize("Joe, Jerold R"));
        NAME_MAPPING.put(StringUtils.normalize("Jerold R Joe"), StringUtils.normalize("Joe, Jerold R"));
        NAME_MAPPING.put(StringUtils.normalize("Joe,Jerold R"), StringUtils.normalize("Joe, Jerold R"));

        NAME_MAPPING.put(StringUtils.normalize("Mounika Bethu"), StringUtils.normalize("Mounika, Bethu"));
        NAME_MAPPING.put(StringUtils.normalize("MounikaBethu"), StringUtils.normalize("Mounika, Bethu"));

        NAME_MAPPING.put(StringUtils.normalize("Gokulraaju K L"), StringUtils.normalize("K L, Gokulraaju"));
        NAME_MAPPING.put(StringUtils.normalize("K L,Gokulraaju"), StringUtils.normalize("K L, Gokulraaju"));

        NAME_MAPPING.put(StringUtils.normalize("Vijay Kartheeswaran M"), StringUtils.normalize("M, Vijay Kartheeswaran"));
        NAME_MAPPING.put(StringUtils.normalize("Flaming George"), StringUtils.normalize("P, Flaming George"));
        NAME_MAPPING.put(StringUtils.normalize("Sathishkumar P"), StringUtils.normalize("P, Sathishkumar"));
        NAME_MAPPING.put(StringUtils.normalize("Priyadharshini Ravi"), StringUtils.normalize("PRIYADHARSHINI, R"));
        NAME_MAPPING.put(StringUtils.normalize("Monesha R R"), StringUtils.normalize("R.R, Monesha"));
        NAME_MAPPING.put(StringUtils.normalize("Keerthigapriya V"), StringUtils.normalize("V, Keerthigapriya"));
        NAME_MAPPING.put(StringUtils.normalize("Amar Jha"), StringUtils.normalize("Jha, Amar"));
        NAME_MAPPING.put(StringUtils.normalize("Aishwarya Reshame"), StringUtils.normalize("Reshame, Aishwa"));
        NAME_MAPPING.put(StringUtils.normalize("Victorsamuvel S"), StringUtils.normalize("Samuthiram, Victorsamuvel"));
        NAME_MAPPING.put(StringUtils.normalize("S, Victorsamuvel"), StringUtils.normalize("Samuthiram, Victorsamuvel"));
        NAME_MAPPING.put(StringUtils.normalize("Devika M S"), StringUtils.normalize("M S, Devika"));
        NAME_MAPPING.put(StringUtils.normalize("Mansi Awachat"), StringUtils.normalize("Awachat, Mansi"));
        NAME_MAPPING.put(StringUtils.normalize("Sowmiya C"), StringUtils.normalize("Chinnusamy, Sowmiya"));
        NAME_MAPPING.put(StringUtils.normalize("Rajesh Selvaraj"), StringUtils.normalize("Selvaraj, Rajesh"));

        // Updated resource list mappings
        NAME_MAPPING.put(StringUtils.normalize("Aishwarya Reshame"), StringUtils.normalize("Reshame, Aishwa"));
        NAME_MAPPING.put(StringUtils.normalize("Logavani K"), StringUtils.normalize("K, Logavani"));
        NAME_MAPPING.put(StringUtils.normalize("Peter,Gilbert Roy"), StringUtils.normalize("P, Gilbert Roy"));
        NAME_MAPPING.put(StringUtils.normalize("M Ramya"), StringUtils.normalize("M, Ramya"));
        NAME_MAPPING.put(StringUtils.normalize("Victor Samuel"), StringUtils.normalize("Samuel, Victor"));
        NAME_MAPPING.put(StringUtils.normalize("Akash Arumugam"), StringUtils.normalize("Arumugam, Akash"));
        NAME_MAPPING.put(StringUtils.normalize("Jha Amar Kumar"), StringUtils.normalize("Jha, Amar Kumar"));
        NAME_MAPPING.put(StringUtils.normalize("Selvaraj, Rajesh"), StringUtils.normalize("Selvaraj, Rajesh"));
    }

    /**
     * Apply name mapping to normalize different name formats
     */
    public static String applyNameMapping(String name) {
        String normalized = StringUtils.normalize(name);
        return NAME_MAPPING.getOrDefault(normalized, normalized);
    }

    /**
     * Add a custom name mapping
     */
    public static void addMapping(String from, String to) {
        NAME_MAPPING.put(StringUtils.normalize(from), StringUtils.normalize(to));
    }

    /**
     * Get all mappings
     */
    public static Map<String, String> getMappings() {
        return new HashMap<>(NAME_MAPPING);
    }
}
