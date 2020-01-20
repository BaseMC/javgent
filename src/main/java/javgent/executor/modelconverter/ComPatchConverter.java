package javgent.executor.modelconverter;

import javgent.ASMAC;
import javgent.executor.model.PatchClass;
import javgent.executor.model.PatchField;
import javgent.executor.model.PatchMethod;
import javgent.executor.model.PatchParameter;
import javgent.commodel.ComPatchClass;

/**
 * Converts communication patch models to the internal patch models
 */
public class ComPatchConverter {
    private ComPatchConverter() {
        //No impl pls
    }

    public static PatchClass convert(ComPatchClass comPatchClass) {

        var patchClass = new PatchClass();

        patchClass.Name = ASMAC.toPackageClassConform(comPatchClass.Name);
        patchClass.ObfName = ASMAC.toPackageClassConform(comPatchClass.ObfName);

        comPatchClass.Fields.forEach(comPatchField -> {
                    var patchField = new PatchField();

                    patchField.Name = comPatchField.Name;
                    patchField.ObfName = comPatchField.ObfName;
                    patchField.Type = ASMAC.toPackageClassConform(comPatchField.Type);

                    patchClass.Fields.add(patchField);
                });

        comPatchClass.Methods.forEach(comPatchMethod -> {
                    var patchMethod = new PatchMethod();

                    patchMethod.Name = comPatchMethod.Name;
                    patchMethod.ObfName = comPatchMethod.ObfName;

                    patchMethod.ReturnType = ASMAC.toPackageClassConform(comPatchMethod.ReturnType);
                    comPatchMethod.Parameters.forEach(comPatchParameter -> {

                        var patchParameter = new PatchParameter();

                        patchParameter.Type = ASMAC.toPackageClassConform(comPatchParameter.Type);

                        patchMethod.Parameters.add(patchParameter);
                    });


                    patchClass.Methods.add(patchMethod);
                });


        return patchClass;
    }
}
