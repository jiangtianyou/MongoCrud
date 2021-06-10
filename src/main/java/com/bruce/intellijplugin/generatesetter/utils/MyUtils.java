package com.bruce.intellijplugin.generatesetter.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;

public class MyUtils {

	public static void notifyMsg(String title, String content) {
		Notifications.Bus.notify(
				new Notification("", title, content, NotificationType.INFORMATION)
		);
	}

	public static void notifyPop(String title, String content) {
		Messages.showInfoMessage(content, title);
	}

	public static PsiClass findClazz(Project project, String classQualifiedName) {
		return JavaPsiFacade.getInstance(project).findClass(classQualifiedName,
				GlobalSearchScope.projectScope(project));
	}


}
