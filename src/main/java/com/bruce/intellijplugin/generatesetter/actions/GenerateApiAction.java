package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.utils.MyUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnActionEventVisitor;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * 生成Crud代码Action
 */
public class GenerateApiAction extends AnAction {

	@Override
	public void actionPerformed(@NotNull final AnActionEvent e) {
		e.getPresentation();
		Project project = e.getProject();
		String text = e.getPresentation().getText();
		assert project != null;
		PsiJavaFile beanFile = (PsiJavaFile) e.getDataContext().getData(DataKeys.PSI_FILE);
		assert beanFile != null;
		String filePath = beanFile.getVirtualFile().getPresentableUrl();
		String cmd = "cmd /c api " + filePath;
		if (text.contains(":")) {
			cmd = cmd + " -s " + text.substring(text.indexOf(":"));
		}
		try {
			Process pro = Runtime.getRuntime().exec(cmd);
			pro.waitFor(2, TimeUnit.MINUTES);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		MyUtils.notifyMsg("提示信息","生成 Postman api成功");
		// 调用npm命令行
	}

}