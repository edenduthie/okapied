package okapied.web;

import org.springframework.web.util.HtmlUtils;

public class WebUtils 
{
    public static String nl2lb(String text)
    {
		String newText = HtmlUtils.htmlEscape(text);
		newText = newText.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
		return newText;
    }
}
