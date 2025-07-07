package kr.paragonnov.annotation.processor;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import kr.paragonnov.annotation.CommonCodeBuilder;
import lombok.Getter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CommonCode Generator
 * @author paragonnov
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("kr.paragonnov.annotation.CommonCodeBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CommonCodesProcessor extends AbstractProcessor {

    @Getter
    private static class CommonCodeData {

        @SerializedName("grp_com_cd")
        private String groupCode;

        @SerializedName("grp_com_nm")
        private String groupName;

        @SerializedName("grp_com_desc")
        private String groupDescription;

        @SerializedName("com_cd")
        private String code;

        @SerializedName("com_cd_nm")
        private String name;

        @SerializedName("com_cd_desc")
        private String description;

    }

    public record GroupCodeData(String groupCode, String groupName, String groupDescription) {
    }

    public record CodeData(String code, String name, String description) {
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(CommonCodeBuilder.class)) {
            CommonCodeBuilder annotation = annotatedElement.getAnnotation(CommonCodeBuilder.class);
            String jsonFileName = annotation.value();

            InputStream inputStream = null;
            try {
                inputStream = processingEnv.getFiler()
                        .getResource(StandardLocation.CLASS_OUTPUT, "", jsonFileName)
                        .openInputStream();
            } catch (IOException e) {
                error(annotatedElement, "Cannot find resource: %s", jsonFileName);
                continue;
            }

            try {
                List<CommonCodeData> commonCodeDataList = new Gson().fromJson(
                        new InputStreamReader(inputStream),
                        new TypeToken<List<CommonCodeData>>() {
                        }.getType());

                Map<GroupCodeData, List<CodeData>> commonCodeDataMap = commonCodeDataList.stream()
                        .collect(Collectors.groupingBy(
                                c -> new GroupCodeData(c.getGroupCode(), c.getGroupName(), c.getGroupDescription()),
                                Collectors.mapping(
                                        c -> new CodeData(c.getCode(), c.getName(), c.getDescription()),
                                        Collectors.toList()
                                )
                        ));

                StringBuilder builder = new StringBuilder();

                builder.append("/**\n")
                        .append(" * Auto Generated Common code<br/><br/>\n");

                for (GroupCodeData groupCodeData : commonCodeDataMap.keySet()) {
                    builder.append(" *  - {@code [").append(groupCodeData.groupCode()).append("]} ").append(groupCodeData.groupName()).append("<br/>\n")
                            .append(" *      ").append(groupCodeData.groupDescription()).append("<br/><br/>\n");
                }

                builder.append(" */\n");

                builder.append("public class CommonCodes {\n")
                        .append(this.generateInterface()).append("\n\n");

                for (Map.Entry<GroupCodeData, List<CodeData>> entry : commonCodeDataMap.entrySet()) {
                    builder.append(generateEnum(entry.getKey(), entry.getValue())).append("\n\n");
                }

                builder.append("}\n");

                JavaFileObject file = processingEnv.getFiler()
                        .createSourceFile("generated.CommonCodes");
                try (Writer writer = file.openWriter()) {
                    writer.write("package generated;\n\n" + builder);
                }

            } catch (IOException | JsonParseException e) {
                error(annotatedElement, "Failed to parse JSON: %s", e.getMessage());
            }
        }
        return true;
    }

    private String generateInterface() throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append("    public interface CommonCodeInterface {\n")
                .append("        /* Common code */\n")
                .append("        String getCode();\n\n")
                .append("        /* Common code name */\n")
                .append("        String getName();\n    }");

        return builder.toString();
    }

    private String generateEnum(GroupCodeData groupCodeData, List<CodeData> codeDataList) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("    /**\n")
                .append("     * ").append(groupCodeData.groupName()).append("(").append(groupCodeData.groupCode()).append(")<br/><br/>\n")
                .append("     * ").append(groupCodeData.groupDescription()).append(")<br/><br/>\n");

        for (CodeData codeData : codeDataList) {
            builder.append("     *  - {@code [").append(codeData.code()).append("]} ").append(codeData.name()).append("<br/>\n")
                    .append("     *      ").append(codeData.description()).append("<br/><br/>\n");
        }

        builder.append("     */\n");

        builder.append("    public enum ").append(groupCodeData.groupCode()).append(" implements CommonCodeInterface {\n");

        for (CodeData codeData : codeDataList) {
            String constantName = codeData.code();
            try {
                Integer.parseInt(constantName);
                constantName = "_" + constantName;
            } catch (NumberFormatException ignored) {
            }

            builder.append("        ").append(constantName)
                    .append("(\"").append(codeData.name()).append("\", \"").append(codeData.code()).append("\", \"").append(codeData.description())
                    .append("\"),\n");
        }
        builder.deleteCharAt(builder.lastIndexOf(",\n"))
                .append("        ;\n\n");

        builder.append("        private final String code;\n")
                .append("        private final String name;\n\n")
                .append("        private final String desc;\n\n")
                .append("        ").append(groupCodeData.groupCode()).append("(String name, String code, String desc) {\n")
                .append("            this.name = name;\n")
                .append("            this.code = code;\n")
                .append("            this.desc = desc;\n        }\n\n")
                .append("        public static String getGroupCode() {\n")
                .append("            return \"").append(groupCodeData.groupCode()).append("\";\n        }\n\n")
                .append("        @Override\n")
                .append("        public String getName() {\n")
                .append("            return name;\n        }\n\n")
                .append("        @Override\n")
                .append("        public String getCode() {\n")
                .append("            return code;\n        }\n\n")
                .append("        public static ").append(groupCodeData.groupCode()).append(" fromCode(String code) {\n")
                .append("            for (").append(groupCodeData.groupCode()).append(" b : ").append(groupCodeData.groupCode()).append(".values()) {\n")
                .append("                if(b.getCode().equals(code)) {\n")
                .append("                    return b;\n")
                .append("                }\n")
                .append("            }\n")
                .append("            return null;\n        }\n\n")
                .append("        public static ").append(groupCodeData.groupCode()).append(" fromName(String code) {\n")
                .append("            for (").append(groupCodeData.groupCode()).append(" b : ").append(groupCodeData.groupCode()).append(".values()) {\n")
                .append("                if(b.getName().equals(code)) {\n")
                .append("                    return b;\n")
                .append("                }\n")
                .append("            }\n")
                .append("            return null;\n        }\n\n    }");

        return builder.toString();
    }

    private void error(Element e, String msg, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
