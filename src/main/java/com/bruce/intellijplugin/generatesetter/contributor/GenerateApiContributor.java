package com.bruce.intellijplugin.generatesetter.contributor;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import icons.EditorBasicsIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GenerateApiContributor extends RunLineMarkerContributor {
	private String baseText = "Generate postman api";
	@Nullable
	@Override
	public Info getInfo(@NotNull PsiElement psiElement) {
		AnAction genCodeAction = ActionManager.getInstance().getAction("GenerateApiAction");
		PsiClass clazz = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
		if (psiElement instanceof PsiKeyword && Objects.equals("interface", psiElement.getText())) {
			if (clazz != null) {
				String qualifiedName = clazz.getQualifiedName();
				if (StringUtils.endsWithAny(qualifiedName, "Controller", "Api")) {
					return new Info(EditorBasicsIcons.gen_api_icon,
							(element) -> baseText,
							genCodeAction);
				}
			}
		}
		if (psiElement instanceof PsiIdentifier) {
			String text = ((PsiIdentifier) psiElement).getText();
			if (Objects.equals(text,"ReturnMsg") && clazz != null ) {
				String name = Objects.requireNonNull(PsiTreeUtil.getContextOfType(psiElement, PsiMethod.class)).getName();
				return new Info(EditorBasicsIcons.gen_api_icon,
						(element) -> baseText + ":"+name,
						genCodeAction);
			}
		}

		return null;
	}
}