/*
 *  Copyright (c) 2017-2019, bruce.ge.
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; version 2 of
 *    the License.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program;
 */

package com.bruce.intellijplugin.generatesetter.utils;

import com.bruce.intellijplugin.generatesetter.Parameters;
import com.bruce.intellijplugin.generatesetter.RealParam;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PsiToolUtils {

    public static boolean checkGuavaExist(Project project, @NotNull PsiElement element) {
        Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(element);
        if(moduleForPsiElement==null){
            return false;
        }
        PsiClass[] listss = PsiShortNamesCache.getInstance(project).getClassesByName("Lists", GlobalSearchScope.moduleRuntimeScope(moduleForPsiElement, false));
        for (PsiClass psiClass : listss) {
            if (psiClass.getQualifiedName().equals("com.google.common.collect.Lists")) ;
            return true;
        }

        return false;
    }

    @NotNull
    public static Parameters extractParamInfo(PsiType psiType) {
        String typeFullName = psiType.getCanonicalText();
        Parameters info = new Parameters();
        info.setReturnType(PsiTypesUtil.getPsiClass(psiType));
        int u = typeFullName.indexOf("<");
        if (u == -1) {
            List<RealParam> realParamList = new ArrayList<>();
            RealParam real = new RealParam();
            real.setRealName(extractShortName(typeFullName));
            real.setRealPackage(typeFullName);
            realParamList.add(real);
            info.setParams(realParamList);
        } else {
            String collectpart = typeFullName.substring(0, u);
            String realClassPart = typeFullName.substring(u + 1, typeFullName.length() - 1);
            info.setCollectName(extractShortName(collectpart));
            info.setCollectPackege(collectpart);
            String[] split = realClassPart.split(",");
            List<RealParam> params = new ArrayList<>();
            if (split.length > 0) {
                for (String m : split) {
                    RealParam param = new RealParam();
                    param.setRealPackage(m);
                    param.setRealName(extractShortName(m));
                    params.add(param);
                }
            }
            info.setParams(params);
        }
        return info;
    }

    public static void addImportToFile(PsiDocumentManager psiDocumentManager, PsiJavaFile containingFile, Document document, Set<String> newImportList) {
        if (newImportList.size() > 0) {
            Iterator<String> iterator = newImportList.iterator();
            while (iterator.hasNext()) {
                String u = iterator.next();
                if (u.startsWith("java.lang")) {
                    iterator.remove();
                }
            }
        }

        if (newImportList.size() > 0) {
            PsiJavaFile javaFile = containingFile;
            PsiImportStatement[] importStatements = javaFile.getImportList().getImportStatements();
            Set<String> containedSet = new HashSet<>();
            for (PsiImportStatement s : importStatements) {
                containedSet.add(s.getQualifiedName());
            }
            StringBuilder newImportText = new StringBuilder();
            for (String newImport : newImportList) {
                if (!containedSet.contains(newImport)) {
                    newImportText.append("\nimport " + newImport + ";");
                }
            }
            PsiPackageStatement packageStatement = javaFile.getPackageStatement();
            int start = 0;
            if (packageStatement != null) {
                start = packageStatement.getTextLength() + packageStatement.getTextOffset();
            }
            String insertText = newImportText.toString();
            if (StringUtils.isNotBlank(insertText)) {
                document.insertString(start, insertText);
                PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
            }
        }
    }

    @NotNull
    public static String lowerStart(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

//    public static WrapInfo extractWrappInfo(String typeFullName) {
    private static String extractShortName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1);
    }

    @NotNull
    public static String calculateSplitText(Document document, int statementOffset) {
        String splitText = "";
        int cur = statementOffset;
        String text = document.getText(new TextRange(cur - 1, cur));
        while (text.equals(" ") || text.equals("\t")) {
            splitText = text + splitText;
            cur--;
            if (cur < 1) {
                break;
            }
            text = document.getText(new TextRange(cur - 1, cur));
        }
        splitText = "\n" + splitText;
        return splitText;
    }

    @NotNull
    public static String extractSplitText(PsiMethod method,
                                          Document document) {
        int startOffset = method.getTextRange().getStartOffset();
        int lastLine = startOffset - 1;
        String text = document.getText(new TextRange(lastLine, lastLine + 1));
        boolean isTable = false;
        while (!text.equals("\n")) {
            if (text.equals('\t')) {
                isTable = true;
            }
            lastLine--;
            text = document.getText(new TextRange(lastLine, lastLine + 1));
        }
        String methodStartToLastLineText = document
                .getText(new TextRange(lastLine, startOffset));
        String splitText = null;
        if (isTable) {
            splitText += methodStartToLastLineText + "\t";
        } else {
            splitText = methodStartToLastLineText + "    ";
        }
        return splitText;
    }

    /**
     * 通过类内容判断是否为带setter getter类
     */
    public static boolean isSetterGetter(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        String set = "set(\\w+)\\(";
        Matcher matcher = Pattern.compile(set, Pattern.MULTILINE).matcher(text);
        if (matcher.find()) {
            String get = matcher.group(1);
            if (Pattern.compile("get" + get + "\\(", Pattern.MULTILINE).matcher(text).find()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String s = "package com.gmmsj.modules.user.model;\n" +
                "\n" +
                "import com.baomidou.mybatisplus.annotation.TableName;\n" +
                "import com.gmmsj.modules.base.BaseEntity;\n" +
                "\n" +
                "@TableName(value = \"test_user\",schema = \"UserCenter\")\n" +
                "public class User extends BaseEntity {\n" +
                "\n" +
                "\tprivate String name;\n" +
                "\n" +
                "\tpublic String getName() {\n" +
                "\t\treturn name;\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void setName(String name) {\n" +
                "\t\tthis.name = name;\n" +
                "\t}\n" +
                "}";

        boolean setterGetter = isSetterGetter(s);
        System.out.println(setterGetter);

    }
}
