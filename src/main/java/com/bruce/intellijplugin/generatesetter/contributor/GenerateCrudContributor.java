package com.bruce.intellijplugin.generatesetter.contributor;

import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.util.PsiTreeUtil;
import icons.EditorBasicsIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GenerateCrudContributor extends RunLineMarkerContributor {
	@Nullable
	@Override
	public Info getInfo(@NotNull PsiElement psiElement) {
		AnAction genCodeAction = ActionManager.getInstance().getAction("GenCodeAction");
		if (psiElement instanceof PsiKeyword && Objects.equals("class", psiElement.getText())) {
			// 这是出于class行
			PsiClass clazz = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
			if (clazz != null) {
				String qualifiedName = clazz.getQualifiedName();
				if (!qualifiedName.contains("entity")) {
					return  null;
				}
				if (!StringUtils.endsWithAny(qualifiedName, "vo", "Vo", "Entity", "entity","Controller")) {
					if (PsiToolUtils.isSetterGetter(clazz.getText())) {
						return new Info(EditorBasicsIcons.gen_code_icon,
								(element) -> "Generate Crud Code",
								genCodeAction);
					}
				}
			}
		}
		return null;
	}
}