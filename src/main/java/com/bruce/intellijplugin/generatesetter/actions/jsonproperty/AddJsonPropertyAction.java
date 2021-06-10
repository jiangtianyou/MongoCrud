// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.bruce.intellijplugin.generatesetter.actions.jsonproperty;

import com.bruce.intellijplugin.generatesetter.utils.AddPropertyUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiElementUtil;
import com.bruce.intellijplugin.generatesetter.utils.PsiToolUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * Add @JsonProperty to entity
 */
public class AddJsonPropertyAction extends AnAction {

	/**
	 * Replaces the run of text selected by the primary caret with a fixed string.
	 *
	 * @param e Event related to this action
	 */
	@Override
	public void actionPerformed(@NotNull final AnActionEvent e) {
		Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
		Project project = e.getRequiredData(CommonDataKeys.PROJECT);
		Document document = editor.getDocument();
		PsiDocumentManager dm = PsiDocumentManager.getInstance(project);
		PsiFile psiFile = dm.getPsiFile(document);

		String text = document.getText();
		text = AddPropertyUtils.addPropertyHeader(text);
		text = AddPropertyUtils.addJsonProperty(text);
		String finalText = text;
		WriteCommandAction.runWriteCommandAction(project, () -> {
			document.setText(finalText);
			if (!(psiFile instanceof PsiJavaFile)) {
				return;
			}
			Set<String> importList = Sets.newHashSet(
					"com.fasterxml.jackson.annotation.JsonIgnoreProperties",
					"com.fasterxml.jackson.annotation.JsonInclude",
					"com.fasterxml.jackson.annotation.JsonProperty",
					"com.fasterxml.jackson.annotation.JsonFormat"
			);
			PsiToolUtils.addImportToFile(dm, (PsiJavaFile) psiFile, document, importList);
			dm.commitAllDocuments(); //重要
			PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
			PsiJavaFile javaFile = (PsiJavaFile) e.getRequiredData(CommonDataKeys.PSI_FILE);
			PsiClass javaClass = javaFile.getClasses()[0];
			PsiField[] allFields = javaClass.getAllFields();
			for (PsiField field : allFields) {
				if (Objects.equals(field.getType().getPresentableText(), "Date")) {
					// 如果有@JsonFormat就不添加了
					if (field.hasAnnotation("com.fasterxml.jackson.annotation.JsonFormat")) {
						continue;
					}
					PsiElement annotationFromText = factory.createAnnotationFromText("@JsonFormat( pattern = \"yyyy-MM-dd HH:mm:ss\" ,timezone = \"GMT+0\")", javaClass);
					PsiModifierList modifierList = field.getModifierList();
					assert modifierList != null;
					modifierList.addBefore(annotationFromText, modifierList.getFirstChild());
				}
			}
			JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
			styleManager.optimizeImports(psiFile);
			styleManager.shortenClassReferences(psiFile);
			CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
			codeStyleManager.reformat(psiFile);
		});
	}

	/**
	 * Sets visibility and enables this action menu item if:
	 * A project is open,
	 * An editor is active,
	 * Some characters are selected
	 *
	 * @param e Event related to this action
	 */
	@Override
	public void update(@NotNull final AnActionEvent e) {
		// Get required data keys
		final Project project = e.getProject();
		final Editor editor = e.getData(CommonDataKeys.EDITOR);
		String text = null;
		if (editor != null) {
			Document document = editor.getDocument();
			text = document.getText();
		}
		boolean enabled = project != null && PsiToolUtils.isSetterGetter(text);
		e.getPresentation().setEnabledAndVisible(enabled);
	}


}