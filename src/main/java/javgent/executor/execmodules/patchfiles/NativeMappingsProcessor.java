package javgent.executor.execmodules.patchfiles;

import javgent.commodel.ComPatchClass;
import javgent.commodel.ComPatchField;
import javgent.commodel.ComPatchMethod;
import javgent.commodel.ComPatchParameter;
import javgent.executor.model.PatchClass;
import javgent.executor.modelconverter.ComPatchConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Load mappings directly from a mappings file (.txt)
 *
 * Implemented from C# (Aves Project; PatchFileMaker)
 */
public class NativeMappingsProcessor {

    private static final Logger Log = LoggerFactory.getLogger(NativeMappingsProcessor.class);

    private static final String CLASS_SPLIT_PATTERN = Pattern.quote(" -> ");

    public Set<PatchClass> readPatchFiles(String mappingFile) {
        Set<ComPatchClass> comPatchClasses = new HashSet<>();

        ComPatchClass last = null;

        try {
            List<String> lines = new ArrayList<>();
            try (Stream<String> stream = Files.lines(Paths.get(mappingFile))) {
                lines = stream.collect(Collectors.toList());
            }
            for (var line : lines) {
                if (line.startsWith("#")) {
                    Log.info("Comment: {}", line);
                    continue;
                }

                if (!line.startsWith("    ")) { //Class
                    last = getComPatchClassFromLine(line);
                    comPatchClasses.add(last);
                } else if (last == null)
                    throw new IllegalArgumentException("Didn't find a class for line: " + line);
                else  //Sub
                    processPayloadOfLine(line, last);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return comPatchClasses.stream().map(ComPatchConverter::convert).collect(Collectors.toSet());
    }

    private ComPatchClass getComPatchClassFromLine(String line) {
        //Class
        var comClass = new ComPatchClass();

        var tmps = line.split(CLASS_SPLIT_PATTERN);

        comClass.Name = tmps[0];
        //Remove :
        comClass.ObfName = tmps[1].substring(0, tmps[1].length() - 1);

        return comClass;
    }

    private void processPayloadOfLine(String line, ComPatchClass last) {
        var tmp1 = line.split(CLASS_SPLIT_PATTERN);
        if (tmp1.length != 2)
            throw new IllegalArgumentException(tmp1.length + " == " + 2);

        String obfName = tmp1[1];

        //Method with LineNumbers
        if (tmp1[0].contains(":")) {
            var method = new ComPatchMethod();
            method.ObfName = obfName;

            var tmp2 = tmp1[0].split(Pattern.quote(":"));
            if (tmp2.length != 3)
                throw new IllegalArgumentException(tmp2.length + " == " + 3);

            var tmp3 = tmp2[2].split(" ");
            if (tmp3.length != 2)
                throw new IllegalArgumentException(tmp3.length + " == " + 2);

            method.ReturnType = tmp3[0];

            var nameAndParametersComObj = getNameAndComPatchParameters(tmp3[1]);
            method.Name = nameAndParametersComObj.getName();
            method.Parameters = nameAndParametersComObj.getParameters();

            last.Methods.add(method);
        } else //Field or Method with no line numbers (rare)
        {
            var tmp2 = Arrays.stream(tmp1[0].split(" ")).filter(str -> !str.isEmpty()).toArray(String[]::new);
            if (tmp2.length != 2)
                throw new IllegalArgumentException(tmp2.length + " == " + 2);

            //Method with no line numbers
            if (tmp2[1].contains("(") && tmp2[1].contains(")")) {
                var method = new ComPatchMethod();
                method.ObfName = obfName;
                method.ReturnType = tmp2[0];

                var nameAndParametersComObj = getNameAndComPatchParameters(tmp2[1]);
                method.Name = nameAndParametersComObj.getName();
                method.Parameters = nameAndParametersComObj.getParameters();

                last.Methods.add(method);
            } else //Field
            {
                var field = new ComPatchField();
                field.ObfName = obfName;
                field.Type = tmp2[0];
                field.Name = tmp2[1];

                last.Fields.add(field);
            }
        }
    }

    private NameAndParametersComObj getNameAndComPatchParameters(String nameAndPars) {
        var indexOfParsStart = nameAndPars.indexOf('(');
        var name = nameAndPars.substring(0, indexOfParsStart);


        var parStr = nameAndPars.substring(indexOfParsStart + 1);
        parStr = parStr.substring(0, parStr.indexOf(')'));

        List<ComPatchParameter> pars = new ArrayList<>();
        if (!"".equals(parStr)) {
            pars = Arrays.stream(parStr.split(","))
                    .map(parType -> {
                        var comPar = new ComPatchParameter();
                        comPar.Type = parType;
                        return comPar;
                    })
                    .collect(Collectors.toList());
        }

        return new NameAndParametersComObj(name, pars);
    }


    class NameAndParametersComObj {
        private final String name;
        private final List<ComPatchParameter> parameters;

        public NameAndParametersComObj(String name, List<ComPatchParameter> parameters) {
            this.name = name;
            this.parameters = parameters;
        }

        public String getName() {
            return name;
        }

        public List<ComPatchParameter> getParameters() {
            return parameters;
        }
    }
}
