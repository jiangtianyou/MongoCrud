package com.bruce.intellijplugin.generatesetter.bean;

import java.util.List;


public class BeanInfo {

    private String moduleName;
    private String baseController;
    private String packageName;
    private String containPackageName;
    private String fullBeanName;
    private String shortBeanName;
	private List<FieldInfo> fields;

	public BeanInfo(String moduleName,String baseController,String packageName, String containPackageName, String fullBeanName, String shortBeanName, List<FieldInfo> fields) {
		this.moduleName = moduleName;
		this.baseController = baseController;
		this.packageName = packageName;
		this.containPackageName = containPackageName;
		this.fullBeanName = fullBeanName;
		this.shortBeanName = shortBeanName;
		this.fields = fields;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFullBeanName() {
		return fullBeanName;
	}

	public void setFullBeanName(String fullBeanName) {
		this.fullBeanName = fullBeanName;
	}

	public String getShortBeanName() {
		return shortBeanName;
	}

	public void setShortBeanName(String shortBeanName) {
		this.shortBeanName = shortBeanName;
	}

	public List<FieldInfo> getFields() {
		return fields;
	}

	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}

	public String getContainPackageName() {
		return containPackageName;
	}

	public void setContainPackageName(String containPackageName) {
		this.containPackageName = containPackageName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getBaseController() {
		return baseController;
	}

	public void setBaseController(String baseController) {
		this.baseController = baseController;
	}
}