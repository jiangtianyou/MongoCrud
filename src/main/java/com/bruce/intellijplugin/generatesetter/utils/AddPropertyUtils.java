package com.bruce.intellijplugin.generatesetter.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * vo添加property功能工具类
 */
public class AddPropertyUtils {
	/**
	 * 添加@JsonProperty注解
	 */
	public static String addJsonProperty(String text){
		//先进行移除
		String removeRegex = "^[\\s&&[^\\n]]+\\S+\\)\\R";
		text = Pattern.compile(removeRegex, Pattern.MULTILINE)
				.matcher(text)
				.replaceAll("");

		//然后添加
		String find = "(^([\\s&&[^\\n]]+)pri.*\\s+[A-Z][a-zA-Z<>]+\\s+(\\w+);)";
		String replace = "$2@JsonProperty(\"$3\")\n$1";
		Matcher matcher = Pattern.compile(find, Pattern.MULTILINE).matcher(text);
		return matcher.replaceAll(replace);
	}

	/**
	 * 类上添加
	 * @JsonInclude 和@JsonIgnoreProperties注解
	 */
	public static String addPropertyHeader(String text){
		//先进行移除
		String removeRegex = "((@JsonInclude)|(@JsonIgnoreProperties)).*\\R";
		text = Pattern.compile(removeRegex, Pattern.MULTILINE)
				.matcher(text)
				.replaceAll("");

		String regex = "^(public class.*\\R)";
		String header = "@JsonInclude( JsonInclude.Include.NON_NULL )\n@JsonIgnoreProperties( ignoreUnknown = true )\n$1";
		Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(text);
		return matcher.replaceAll(header);
	}
}
