package com.cognizant.copilot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameMappingUtilsTest {

    @Test
    void shouldNormalizeUpdatedResourceListNames() {
        assertEquals("joejeroldr", NameMappingUtils.applyNameMapping("Joe,Jerold R"));
        assertEquals("rrmonesha", NameMappingUtils.applyNameMapping("R.R, Monesha"));
        assertEquals("reshameaishwa", NameMappingUtils.applyNameMapping("Aishwarya Reshame"));
        assertEquals("pgilbertroy", NameMappingUtils.applyNameMapping("Peter,Gilbert Roy"));
        assertEquals("mramya", NameMappingUtils.applyNameMapping("M Ramya"));
        assertEquals("klogavani", NameMappingUtils.applyNameMapping("Logavani K"));
        assertEquals("samuelvictor", NameMappingUtils.applyNameMapping("Victor Samuel"));
        assertEquals("chinnusamysowmiya", NameMappingUtils.applyNameMapping("Chinnusamy, Sowmiya"));
    }
}
