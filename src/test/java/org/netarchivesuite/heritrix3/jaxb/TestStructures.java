package org.netarchivesuite.heritrix3.jaxb;

import java.util.List;

import org.junit.Assert;

public class TestStructures {

    public static void assertStringList(String[] expectedStrings, List<String> strList) {
        Assert.assertNotNull(strList);
        Assert.assertEquals(expectedStrings.length, strList.size());
        for (int i=0; i<expectedStrings.length; ++i) {
            Assert.assertEquals(expectedStrings[i], strList.get(i));
        }
    }

    public static void assertConfigFileList(Object[][] expectedConfigFiles, List<ConfigFile> configFiles) {
        Assert.assertNotNull(configFiles);
        Assert.assertEquals(expectedConfigFiles.length, configFiles.size());
        ConfigFile configFile;
        for (int i=0; i<expectedConfigFiles.length; ++i) {
            configFile = configFiles.get(i);
            Assert.assertEquals(expectedConfigFiles[i][0], configFile.key);
            Assert.assertEquals(expectedConfigFiles[i][1], configFile.name);
            Assert.assertEquals(expectedConfigFiles[i][2], configFile.path);
            Assert.assertEquals(expectedConfigFiles[i][3], configFile.url);
            Assert.assertEquals(expectedConfigFiles[i][4], configFile.editable);
        }
    }

    public static void assertReportsList(String[][] expectedReports, List<Report> reports) {
        Assert.assertNotNull(reports);
        Assert.assertEquals(expectedReports.length, reports.size());
        Report report;
        for (int i=0; i<expectedReports.length; ++i) {
            report = reports.get(i);
            Assert.assertEquals(expectedReports[i][0], report.className);
            Assert.assertEquals(expectedReports[i][1], report.shortName);
        }
    }

}
