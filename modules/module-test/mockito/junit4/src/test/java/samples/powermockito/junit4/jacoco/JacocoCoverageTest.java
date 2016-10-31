package samples.powermockito.junit4.jacoco;


import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class JacocoCoverageTest {

    public static final String[] TARGET = new String[]{
            TargetTest.class.getName(),
            StaticMethods.class.getName(),
            InstanceMethods.class.getName()
    };

    @Test
    public void jacocoOfflineInstShouldCalculateCoverageAfterPowerMockTransformation() throws Exception {
        final RuntimeData data = new RuntimeData();

        runTargetTest(data);

        final CoverageBuilder coverageBuilder = collectCoverage(getExecutionDataStore(data));

        assertCodeCoverage(coverageBuilder);
    }

    private void runTargetTest(RuntimeData data) throws Exception {IRuntime runtime = new LoggerRuntime();
        instrumentClasses(runtime);

        runtime.startup(data);

        JUnitCore.runClasses(TargetTest.class);

        runtime.shutdown();

        restoreOriginalClasses();
    }

    private void assertCodeCoverage(CoverageBuilder coverageBuilder) {
        for (IClassCoverage classCoverage : coverageBuilder.getClasses()) {
            for (IMethodCoverage methodCoverage : classCoverage.getMethods()) {
                if (methodCoverage.getName().equals("calculateSomething")) {
                    assertThat(methodCoverage.getLineCounter().getCoveredRatio()).isEqualTo(1.0);
                    assertThat(methodCoverage.getLineCounter().getCoveredCount()).isEqualTo(4);
                }
            }
        }
    }

    private CoverageBuilder collectCoverage(ExecutionDataStore executionData) throws IOException {
        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
        for (String className : TARGET) {
            analyzer.analyzeClass(getClass().getResourceAsStream(classNameToFileName(className)), className);
        }
        return coverageBuilder;
    }

    private ExecutionDataStore getExecutionDataStore(RuntimeData data) {
        final ExecutionDataStore executionData = new ExecutionDataStore();
        final SessionInfoStore sessionInfos = new SessionInfoStore();
        data.collect(executionData, sessionInfos, false);
        return executionData;
    }

    private void restoreOriginalClasses() throws URISyntaxException, IOException {
        for (String className : TARGET) {
            final String classResource = classNameToFileName(className);
            URL classResourceURL = getClass().getResource(classResource);
            File originalFile = new File(classResourceURL.toURI());
            restoreOriginalFile(originalFile);
        }
    }

    private void instrumentClasses(IRuntime runtime) throws URISyntaxException, IOException {
        Instrumenter instr = new Instrumenter(runtime);
        for (String className : TARGET) {
            instrumentClass(instr, className);
        }
    }


    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest, false);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    private void restoreOriginalFile(File originalFile) throws IOException {
        File backup = new File(originalFile.getAbsolutePath() + ".bak");
        if (originalFile.exists()) {
            originalFile.delete();
        }
        copyFileUsingStream(backup, originalFile);
    }

    private void instrumentClass(Instrumenter instr,
                                 String className) throws URISyntaxException, IOException {
        URL classResourceURL = getClass().getResource(classNameToFileName(className));
        File originalFile = new File(classResourceURL.toURI());

        copyOriginalFile(originalFile);

        final byte[] instrumented = instr.instrument(classResourceURL.openStream(), className);

        writeInstrumentedFile(originalFile, instrumented);
    }

    private void copyOriginalFile(File originalFile) throws IOException, URISyntaxException {
        File backup = new File(originalFile.getAbsolutePath() + ".bak");
        if (backup.exists()) {
            backup.delete();
        }
        copyFileUsingStream(originalFile, backup);
    }

    private void writeInstrumentedFile(File originalFile, byte[] instrumented) throws IOException {
        FileOutputStream fooStream = null;
        try {
            fooStream = new FileOutputStream(originalFile, false);
            fooStream.write(instrumented);
        } finally {
            if (fooStream != null) {
                fooStream.close();
            }
        }
    }


    private String classNameToFileName(String name) {return '/' + name.replace('.', '/') + ".class";}

}
