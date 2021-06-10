package com.bruce.intellijplugin.generatesetter.bean;

public class FieldInfo {

    private String fieldName;
    private String fieldNameInUnderScore;
    private String fieldType;
    private boolean mustRequired;

    public FieldInfo(String fieldName, String fieldNameInUnderScore, String fieldType, boolean mustRequired) {
        this.fieldName = fieldName;
        this.fieldNameInUnderScore = fieldNameInUnderScore;
        this.fieldType = fieldType;
        this.mustRequired = mustRequired;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldNameInUnderScore() {
        return fieldNameInUnderScore;
    }

    public void setFieldNameInUnderScore(String fieldNameInUnderScore) {
        this.fieldNameInUnderScore = fieldNameInUnderScore;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isMustRequired() {
        return mustRequired;
    }

    public void setMustRequired(boolean mustRequired) {
        this.mustRequired = mustRequired;
    }
}
