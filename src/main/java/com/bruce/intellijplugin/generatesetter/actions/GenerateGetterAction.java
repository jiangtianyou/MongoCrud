package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiDocumentUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiMethodUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.bruce.intellijplugin.generatesetter.utils.PsiMethodUtils.getterStatement;

public class GenerateGetterAction extends PsiElementBaseIntentionAction {


	@Override
	public void invoke(@NotNull Project project, Editor editor,
	                   @NotNull PsiElement element) throws IncorrectOperationException {
		PsiElement psiParent = PsiTreeUtil.getParentOfType(element,
				PsiLocalVariable.class, PsiMethod.class);
		if (psiParent == null) {
			return;
		}
		// 情况一： 从localVariable生成所有setter
		if (psiParent instanceof PsiLocalVariable) {
			PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
			if (!(psiLocal.getParent() instanceof PsiDeclarationStatement)) {
				return;
			}
			handleWithLocalVariable(psiLocal, project, psiLocal);
		}
		// 情况2： 直接从参数列表中生成setter
		if (psiParent instanceof PsiMethod) {
			PsiMethod psiMethod = (PsiMethod) psiParent;
			handleWithMethod(psiMethod, project, element);
		}
	}

	@NotNull
	private String generateStringForNoParam(String generateName,
	                                        List<PsiMethod> methodList, String splitText) {
		StringBuilder builder = new StringBuilder();
		builder.append(splitText);
		for (PsiMethod method : methodList) {
			String getterStatement = getterStatement(generateName,method);
			builder.append(getterStatement);
			builder.append(splitText);
		}
		return builder.toString();
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor,
	                           @NotNull PsiElement element) {
		Boolean isHasGetterObject = isValidAsLocalVariableWithSetterOrGetterMethod(element);
		if (!isHasGetterObject) {
			return PsiMethodUtils.isValidAsMethodWithSetterMethod(element);
		}
		return true;
	}

	@NotNull
	private Boolean isValidAsLocalVariableWithSetterOrGetterMethod(@NotNull PsiElement element) {
		PsiLocalVariable psiParent = PsiTreeUtil.getParentOfType(element,
				PsiLocalVariable.class);
		if (psiParent == null) {
			return false;
		}
		if (!(psiParent.getParent() instanceof PsiDeclarationStatement)) {
			return false;
		}
		PsiClass psiClass = PsiTypesUtil.getPsiClass(psiParent.getType());
		return PsiClassUtils.checkClasHasValidGetMethod(psiClass);
	}


	@Nls
	@NotNull
	@Override
	public String getFamilyName() {
		return CommonConstants.GENERATE_GETTER_METHOD;
	}

	@NotNull
	@Override
	public String getText() {
		return CommonConstants.GENERATE_GETTER_METHOD;
	}


	private void handleWithMethod(PsiMethod method, Project project,
	                              PsiElement element) {
		PsiDocumentManager psiDocumentManager = PsiDocumentManager
				.getInstance(project);
		Document document = psiDocumentManager
				.getDocument(element.getContainingFile());
		PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class, false);
		if (parameter == null) {
			return;
		}
		PsiClass psiClass = PsiTypesUtil.getPsiClass(parameter.getType());
		List<PsiMethod> psiMethods = PsiClassUtils.extractGetMethod(psiClass);
		String generateName = ObjectUtils.defaultIfNull(element.getText(), "entity");
		assert document != null;
		String splitText = PsiToolUtils.extractSplitText(method, document);
		String buildString = generateStringForNoParam(generateName, psiMethods,
				splitText);
		document.insertString(Objects.requireNonNull(method.getBody()).getTextOffset()+1,
				buildString);
		PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
	}


	private void handleWithLocalVariable(PsiLocalVariable localVariable,
	                                     Project project, PsiElement element) {
		PsiElement parent1 = localVariable.getParent();
		if (!(parent1 instanceof PsiDeclarationStatement)) {
			return;
		}
		PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
		String generateName = localVariable.getName();
		List<PsiMethod> methodList = PsiClassUtils.extractGetMethod(psiClass);
		if (methodList.size() == 0) {
			return;
		}
		PsiDocumentManager psiDocumentManager = PsiDocumentManager
				.getInstance(project);
		PsiFile containingFile = element.getContainingFile();
		Document document = psiDocumentManager.getDocument(containingFile);
		String splitText = PsiToolUtils.calculateSplitText(document, parent1.getTextOffset());

		Set<String> newImportList = new HashSet<>();
		String buildString = generateStringForNoParam(generateName, methodList,
				splitText);
		document.insertString(parent1.getTextOffset() + parent1.getText().length(),
				buildString);
		PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
		PsiToolUtils.addImportToFile(psiDocumentManager,
				(PsiJavaFile) containingFile, document, newImportList);
		return;
	}


}
