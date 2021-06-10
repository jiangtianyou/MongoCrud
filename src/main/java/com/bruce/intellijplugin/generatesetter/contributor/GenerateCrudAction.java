package com.bruce.intellijplugin.generatesetter.contributor;

import com.bruce.intellijplugin.generatesetter.bean.BeanInfo;
import com.bruce.intellijplugin.generatesetter.bean.FieldInfo;
import com.bruce.intellijplugin.generatesetter.utils.MyUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiClassUtils;
import com.bruce.intellijplugin.generatesetter.utils.PsiFileUtils;
import com.bruce.intellijplugin.generatesetter.utils.freemarker.FreemarkerTool;
import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 生成Crud代码Action
 */
public class GenerateCrudAction extends AnAction {

	@Override
	public void actionPerformed(@NotNull final AnActionEvent e) {
		Project project = e.getProject();
		assert project != null;
		PsiDocumentManager dm = PsiDocumentManager.getInstance(project);
		PsiJavaFile beanFile = (PsiJavaFile) e.getDataContext().getData(DataKeys.PSI_FILE);


		assert beanFile != null;
		PsiDirectory containingDirectory = beanFile.getContainingDirectory();
		BeanInfo beanInfo = PsiClassUtils.getBeanInfo(beanFile.getClasses()[0]);
		if (beanInfo == null) {
			return;
		}
		//添加上moduleName
		beanInfo.setModuleName(PsiFileUtils.getModuleName(project, beanFile));
		// 是否已经生成过，对应的Crud
		if (isDirHasExist(beanFile)) {
			MyUtils.notifyMsg("文件已存在提示",String.format("文件夹已存在，如果覆盖请先手动删除【%s】文件夹",
					StringUtils.uncapitalize(beanInfo.getShortBeanName())));
			return;
		}

		String baseController = PsiClassUtils.getBaseController(project, beanFile);
		beanInfo.setBaseController(baseController);

		// 准备生成的数据
		List<PsiJavaFile> finalFiles = getFinalFiles(project, beanInfo);
		CommandProcessor.getInstance().executeCommand(project, () -> {
			ApplicationManager.getApplication().runWriteAction(() -> {
				String folderName = StringUtils.uncapitalize(beanInfo.getShortBeanName());
				PsiDirectory dir = DirectoryUtil.createSubdirectories(folderName, containingDirectory, "/");
				for (PsiJavaFile file : finalFiles) {
					dir.add(file);
				}
				MyUtils.notifyMsg("生成信息","文件生成成功");
				dm.commitAllDocuments();
			});
		}, "Foo", "Bar");

	}


	/**
	 * beanFile对应生成文件所在目录是否已存在
	 */
	private boolean isDirHasExist(PsiJavaFile beanFile) {
		PsiDirectory containingDirectory = beanFile.getContainingDirectory();
		List<PsiDirectory> childrenDirs = PsiTreeUtil.getChildrenOfTypeAsList(containingDirectory, PsiDirectory.class);
		List<String> dirs = childrenDirs.stream().map(PsiDirectory::getName).collect(Collectors.toList());
		String name = beanFile.getName();
		// 类名作为目标文件的文件夹
		String className = name.substring(0, name.lastIndexOf("."));
		if (dirs.contains(StringUtils.uncapitalize(className))) {
			return true;
		}
		return false;
	}




	private List<PsiJavaFile> getFinalFiles(Project project, BeanInfo beanInfo) {
		PsiJavaFile entity = this._createJavaFile(project, "entity.ftl", beanInfo);
		PsiJavaFile pageParamVo = this._createJavaFile(project, "page_param.ftl", beanInfo);
		PsiJavaFile entityParamVo = this._createJavaFile(project, "entity_param.ftl", beanInfo);
		PsiJavaFile dao = this._createJavaFile(project, "dao.ftl", beanInfo);
		PsiJavaFile service = this._createJavaFile(project, "service.ftl", beanInfo);
		PsiJavaFile controllerApi = this._createJavaFile(project, "controller_api.ftl", beanInfo);
		PsiJavaFile controller = this._createJavaFile(project, "controller.ftl", beanInfo);
		PsiJavaFile client = this._createJavaFile(project, "client.ftl", beanInfo);
		return Arrays.asList(entity, entityParamVo, pageParamVo, dao, service, controllerApi, controller, client);
	}




	private PsiJavaFile _createJavaFile(Project project, String templateName, BeanInfo beanInfo) {

		Map<String, String> map = new HashMap();
		String bean = beanInfo.getShortBeanName();
		map.put("controller.ftl", bean + "Controller.java");
		map.put("controller_api.ftl", bean + "Api.java");
		map.put("dao.ftl", bean + "Dao.java");
		map.put("entity.ftl", bean + "Entity.java");
		map.put("page_param.ftl", bean + "PageVo.java");
		map.put("client.ftl", bean + "Client.java");
		map.put("entity_param.ftl", bean + "Vo.java");
		map.put("service.ftl", bean + "Service.java");
		String fileName = (String)map.get(templateName);
		if (fileName == null) {
			throw new IllegalArgumentException("无法识别模板名称");
		} else {
			FreemarkerTool tool = new FreemarkerTool();
			String text = tool.processString(templateName, beanInfo);
			text = text.replaceAll("\r", "");
			PsiJavaFile psiFile = (PsiJavaFile)PsiFileFactory.getInstance(project).createFileFromText(fileName, StdFileTypes.JAVA, text);
			PsiJavaFile javaFile = (PsiJavaFile)PsiFileUtils.beautify(project, psiFile);
			return javaFile;
		}
	}

	public static void main(String[] args) {
		FreemarkerTool tool = new FreemarkerTool();
		List<FieldInfo> fileds = new ArrayList<>();

		FieldInfo myDate = new FieldInfo("testField", "test_field", "Date", true);
		fileds.add(myDate);
		BeanInfo beanInfo = new BeanInfo("stream","com.base.BaseController","com.package.test","com.pageage.test.testController","com.test.People","People",fileds);
		String text = tool.processString("service.ftl", beanInfo);
		text = text.replaceAll("\r", "");
		System.out.println(text);
	}

}