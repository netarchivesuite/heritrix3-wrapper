package org.netarchivesuite.heritrix3.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestScript {

    protected static ClassLoader clsLdr = TestEngine.class.getClassLoader();

    public static final File getTestResourceFile(String fname) {
        URL url = clsLdr.getResource(fname);
        String path = url.getFile();
        path = path.replaceAll("%5b", "[");
        path = path.replaceAll("%5d", "]");
        File file = new File(path);
        return file;
    }

    @Test
    public void test_script() {
        File scriptXmlFile = getTestResourceFile("jaxb/script-example.xml");
        if (!scriptXmlFile.exists()) {
            Assert.fail("Test data missing, 'jaxbscript-example.xml'");
        }
        Script script;
        try {
            InputStream in = new FileInputStream(scriptXmlFile);
            script = Script.unmarshall(in);
            in.close();
            assertScript(script);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Script.marshall(script, out);
            out.close();
            byte[] xml = out.toByteArray();
            // debug
            //System.out.println(new String(xml, "UTF-8"));
            in = new ByteArrayInputStream(xml);
            script = Script.unmarshall(in);
            in.close();
            assertScript(script);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Unexpected exception!");
        }
    }

    public void assertScript(Script script) {
        Assert.assertNotNull(script);
        Assert.assertEquals("https://192.168.1.101:6443/engine/job/1414451864048/", script.crawlJobUrl);
        Assert.assertEquals("1414451864048", script.crawlJobShortName);
        String[][] expectedAvailableScriptEngines = {
                {
                    "beanshell",
                    "BeanShell"
                },
                {
                    "groovy",
                    "Groovy"
                },
                {
                    "nashorn",
                    "ECMAScript"
                }
        };
        TestStructures.assertScriptsEngineList(expectedAvailableScriptEngines, script.availableScriptEngines);
        // {}
        Assert.assertEquals("this.binding.getVariables().each{ rawOut.println(\"${it.key}= ${it.value}\") }", script.script);
        Assert.assertEquals(new Integer(1), script.linesExecuted);
        StringBuilder expectedRawOut = new StringBuilder();
        expectedRawOut.append("rawOut= java.io.PrintWriter@5ccaa7fc");
        expectedRawOut.append("\n");
        expectedRawOut.append("appCtx= org.archive.spring.PathSharingContext@47e69eb7: startup date [Mon Oct 27 23:17:44 GMT 2014]; root of context hierarchy");
        expectedRawOut.append("\n");
        expectedRawOut.append("scriptResource= org.archive.crawler.restlet.ScriptResource@3db002eb");
        expectedRawOut.append("\n");
        expectedRawOut.append("context= javax.script.SimpleScriptContext@6b87da3d");
        expectedRawOut.append("\n");
        expectedRawOut.append("job= org.archive.crawler.framework.CrawlJob@57db9aba");
        expectedRawOut.append("\n");
        expectedRawOut.append("htmlOut= java.io.PrintWriter@26823ade");
        expectedRawOut.append("\n");
        expectedRawOut.append("out= java.io.PrintWriter@2f7e9cb5");
        expectedRawOut.append("\n");
        Assert.assertEquals(expectedRawOut.toString(), script.rawOutput);
        String[][] expectedAvailableGlobalVariables = {
                {
                    "rawOut",
                    "a PrintWriter for arbitrary text output to this page"
                },
                {
                    "htmlOut",
                    "a PrintWriter for HTML output to this page"
                },
                {
                    "job",
                    "the current CrawlJob instance"
                },
                {
                    "appCtx",
                    "current job ApplicationContext, if any"
                },
                {
                    "scriptResource",
                    "the ScriptResource implementing this page, which offers utility methods"
                }
        };
        TestStructures.assertGlobalVariableList(expectedAvailableGlobalVariables, script.availableGlobalVariables);
        Assert.assertEquals(false, script.failure);
    }

}
