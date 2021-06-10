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

import com.bruce.intellijplugin.generatesetter.actions.GenerateAllSetterAction;
import com.bruce.intellijplugin.generatesetter.bean.BeanInfo;
import com.bruce.intellijplugin.generatesetter.bean.FieldInfo;
import com.google.common.base.CaseFormat;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PsiClassUtils {

    public static boolean isNotSystemClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null || qualifiedName.startsWith("java.")) {
            return false;
        }
        return true;
    }


    public static boolean isValidSetMethod(PsiMethod m) {
        return m.hasModifierProperty("public") && !m.hasModifierProperty("static") && m.getName().startsWith("set");
    }


    public static boolean isValidGetMethod(PsiMethod m) {
        return m.hasModifierProperty("public") && !m.hasModifierProperty("static") &&
                (m.getName().startsWith(GenerateAllSetterAction.GET) || m.getName().startsWith(GenerateAllSetterAction.IS));
    }


    public static void addSetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidSetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    public static void addGetMethodToList(PsiClass psiClass, List<PsiMethod> methodList) {
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            if (isValidGetMethod(method)) {
                methodList.add(method);
            }
        }
    }

    @NotNull
    public static List<PsiMethod> extractSetMethods(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isNotSystemClass(psiClass)) {
            addSetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }

    public static List<PsiMethod> extractGetMethod(PsiClass psiClass) {
        List<PsiMethod> methodList = new ArrayList<>();
        while (isNotSystemClass(psiClass)) {
            addGetMethodToList(psiClass, methodList);
            psiClass = psiClass.getSuperClass();
        }
        return methodList;
    }


    public static boolean checkClassHasValidSetMethod(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        while (isNotSystemClass(psiClass)) {
            for (PsiMethod m : psiClass.getMethods()) {
                if (isValidSetMethod(m)) {
                    return true;
                }
            }
            psiClass = psiClass.getSuperClass();
        }
        return false;
    }


    public static boolean checkClasHasValidGetMethod(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        while (isNotSystemClass(psiClass)) {
            for (PsiMethod m : psiClass.getMethods()) {
                if (isValidGetMethod(m)) {
                    return true;
                }
            }
            psiClass = psiClass.getSuperClass();
        }
        return false;
    }


    public static BeanInfo getBeanInfo(PsiClass beanClass) {
        if (!checkClasHasValidGetMethod(beanClass) || !checkClassHasValidSetMethod(beanClass)) {
            return null;
        }
        PsiField[] allFields = beanClass.getAllFields();
        List<PsiField> psiFields = Arrays.asList(allFields);
        //有效的字段
        psiFields = psiFields.stream().filter(item -> item.hasAnnotation("org.springframework.data.annotation.Id")
                || item.hasAnnotation("org.springframework.data.mongodb.core.mapping.Field")).collect(Collectors.toList());

        String packageName = getPackage(beanClass);
        String fullBeanName = beanClass.getQualifiedName();
        String shortBeanName = beanClass.getName();

        ArrayList<FieldInfo> fields = Lists.newArrayList();
        for (PsiField psiField : psiFields) {
            String fieldName = ObjectUtils.defaultIfNull(psiField.getName(),"");
            String fieldNameInUnderScore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
            fieldNameInUnderScore = Objects.equals(fieldNameInUnderScore, "id") ? "_id" : fieldNameInUnderScore;
            String fieldType = psiField.getType().getPresentableText();
            boolean required = psiField.hasAnnotation("javax.validation.constraints.NotNull") || fieldName.equals("id");
            fields.add(new FieldInfo(fieldName, fieldNameInUnderScore, fieldType, required));
        }

        return new BeanInfo(null, null,packageName,packageName+"."+StringUtils.uncapitalize(shortBeanName),
                fullBeanName, shortBeanName, fields);
    }




    public static String getPackage(PsiClass beanClass) {
        if (beanClass == null) {
            return "";
        }
        String qualifiedName = ObjectUtils.defaultIfNull(beanClass.getQualifiedName(),"") ;
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    public static String getBaseController(Project project,PsiJavaFile beanFile) {

        String moduleName = PsiFileUtils.getModuleName(project, beanFile);
        String controllerName = StringUtils.capitalize(moduleName) + "BaseController";
	    final ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
	    final Module module = index.getModuleForFile(beanFile.getVirtualFile());
        PsiClass[] classesByName = PsiShortNamesCache.getInstance(project).getClassesByName(controllerName,
		        GlobalSearchScope.moduleScope(module));

        String rtn = "com.wdit.modules.media.mongo.controller.base." + controllerName;
        if (classesByName.length > 0) {
            rtn =  classesByName[0].getQualifiedName();
        }
        return rtn;
    }



}
