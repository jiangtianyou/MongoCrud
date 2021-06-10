package com.bruce.intellijplugin.generatesetter.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.Objects;

public class PsiFileUtils {
	public static PsiFile beautify(Project project,PsiFile file) {
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
		styleManager.optimizeImports(file);
		PsiFile psiFile = (PsiFile)styleManager.shortenClassReferences(file);
		CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
		return (PsiFile) codeStyleManager.reformat(psiFile);
	}

	public static String getModuleName(Project project,PsiFile file) {
		ProjectRootManager pm = ProjectRootManager.getInstance(project);
		String moduleName = Objects.requireNonNull(pm.getFileIndex().getModuleForFile(file.getVirtualFile())).getName();
		if (moduleName.lastIndexOf("-") != -1) {
			moduleName = moduleName.substring(moduleName.lastIndexOf("-") + 1);
		}
		return moduleName;
	}
}
