package org.netarchivesuite.heritrix3wrapper.jaxb;

import java.util.List;

import org.junit.Assert;
import org.netarchivesuite.heritrix3wrapper.jaxb.ConfigFile;
import org.netarchivesuite.heritrix3wrapper.jaxb.GlobalVariable;
import org.netarchivesuite.heritrix3wrapper.jaxb.Report;
import org.netarchivesuite.heritrix3wrapper.jaxb.ScriptEngine;

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

    public static void assertScriptsEngineList(String[][] expectedScriptEngines, List<ScriptEngine> scriptEngines) {
        Assert.assertNotNull(scriptEngines);
        Assert.assertEquals(expectedScriptEngines.length, scriptEngines.size());
        ScriptEngine scriptEngine;
        for (int i=0; i<expectedScriptEngines.length; ++i) {
            scriptEngine = scriptEngines.get(i);
            Assert.assertEquals(expectedScriptEngines[i][0], scriptEngine.engine);
            Assert.assertEquals(expectedScriptEngines[i][1], scriptEngine.language);
        }
    }

    public static void assertGlobalVariableList(String[][] expectedGlobalVariables, List<GlobalVariable> globalVariables) {
        Assert.assertNotNull(globalVariables);
        Assert.assertEquals(expectedGlobalVariables.length, globalVariables.size());
        GlobalVariable globalVariable;
        for (int i=0; i<expectedGlobalVariables.length; ++i) {
            globalVariable = globalVariables.get(i);
            Assert.assertEquals(expectedGlobalVariables[i][0], globalVariable.variable);
            Assert.assertEquals(expectedGlobalVariables[i][1], globalVariable.description);
        }
    }

}
