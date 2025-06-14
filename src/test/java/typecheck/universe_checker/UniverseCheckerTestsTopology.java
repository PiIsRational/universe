package universe;

import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.List;

public class UniverseCheckerTestsTopology extends CheckerFrameworkPerDirectoryTest {
    public UniverseCheckerTestsTopology(List<File> testFiles) {
        super(
                testFiles,
                UniverseChecker.class,
                "",
                "-Anomsgtext",
                "-AconservativeUninferredTypeArguments",
                "-d",
                "testTmp",
                "-Alint=anyWritable");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"typecheck/topol"};
    }
}
