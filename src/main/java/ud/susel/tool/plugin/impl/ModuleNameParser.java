package ud.susel.tool.plugin.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParseStart;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.modules.ModuleDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.github.javaparser.Providers.provider;

public class ModuleNameParser {

    private final File sourceFolder;
    private final JavaParser javaParser;

    public ModuleNameParser(File sourceFolder) {
        this.sourceFolder = sourceFolder;
        this.javaParser = new JavaParser(new ParserConfiguration()
                // Let's use the LTS version of Java that supports "module-info" at the time of this writing.
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11));
    }

    /**
     * Parses the "module-info.java" class and returns the module name.
     *
     * @return the module name.
     */
    public String parse() {
        try {
            File moduleFile = new File(sourceFolder, "module-info.java");
            if (!moduleFile.exists()) {
                throw new RuntimeException("Unable to locate " + moduleFile);
            }

            ParseResult<ModuleDeclaration> parseResult = javaParser
                    .parse(ParseStart.MODULE_DECLARATION, provider(new FileInputStream(moduleFile)));

            if (parseResult.getResult().isPresent()) {
                return parseResult.getResult().get().getNameAsString();
            }

            throw new RuntimeException("Unable to parse module-info.java");

        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Unable to locate module-info.java", ex);
        }
    }
}
