package com.bruce.intellijplugin.generatesetter.utils;

import com.bruce.intellijplugin.generatesetter.Parameters;
import com.bruce.intellijplugin.generatesetter.actions.GenerateAllSetterBase;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class PsiMethodUtils {
	public static String getterStatement(String generateName,PsiMethod getter) {
		PsiType returnType = getter.getReturnType();
		String getXXX = getter.getName();
		String localVariableName = StringUtils.uncapitalize(getXXX.replaceAll("get", ""));
		String text = returnType.getPresentableText();
		return text + " " + localVariableName + " = "+ generateName+"."+ getXXX + "();";
	}

	@NotNull
	public static Boolean isValidAsMethodWithSetterMethod(@NotNull PsiElement element) {
		PsiClass psiClass;
		PsiElement parentMethod = PsiTreeUtil.getParentOfType(element,
				PsiMethod.class);
		if (parentMethod == null) {
			return false;
		}
		PsiMethod method = (PsiMethod) parentMethod;
		// if is constructor the return type will be null.
		if (method.getReturnType() == null) {
			return false;
		}
		psiClass = PsiTypesUtil.getPsiClass(method.getReturnType());
		Parameters returnTypeInfo = PsiToolUtils
				.extractParamInfo(method.getReturnType());
		if (returnTypeInfo.getCollectPackege() != null && GenerateAllSetterBase.handlerMap
				.containsKey(returnTypeInfo.getCollectPackege())) {
			return true;
		}
		return PsiClassUtils.checkClassHasValidSetMethod(psiClass);
	}
}
