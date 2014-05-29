package okapied.util;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

public class StayzParserCallback extends ParserCallback 
{
	
	boolean inName = false;
	boolean inNext = false;
	BufferedWriter out;
	StayzEnquiry parent;
	
	public StayzParserCallback(BufferedWriter out, StayzEnquiry parent)
	{
		this.out = out;
		this.parent = parent;
	}
	
	@Override
	public void handleComment(char[] data,int pos)
	{
		
	}
	
	@Override
	public void handleEndTag(HTML.Tag t,int pos)
	{
		if( t==HTML.Tag.DIV && inName ) inName = false;
		if( t==HTML.Tag.SPAN && inNext ) inNext = false;
	}
	
	@Override
	public void handleError(String errorMsg,int pos)
	{
//		System.out.println("Error: " + errorMsg);
	}
	
	@Override
	public void handleSimpleTag(HTML.Tag t,MutableAttributeSet a,int pos)
	{
//		System.out.println("Simple tag: " + toString().toString());
	}
	
	@Override
	public void handleStartTag(HTML.Tag t,MutableAttributeSet a,int pos)
	{
//		System.out.println("Start tag: " + t.toString());
		if( t==HTML.Tag.DIV )
		{
			if( a.toString().contains("class=Name"))
			{
				inName = true;
			}
		}
		else
		{
			if( inName && t==HTML.Tag.A )
			{
				String number = a.toString().substring(6);
				
				try {
					out.append(number);
					out.newLine();
				} catch (IOException e) {
					System.out.println("Failed to write number: " + number);
				}
			}
			
			if( t==HTML.Tag.SPAN )
			{
				if( a.toString().contains("class=next"))
				{
					inNext = true;
				}
			}
			
			if( inNext && t==HTML.Tag.A )
			{
				String url = a.toString().substring(6);
				parent.setNextURL("http://www.stayz.com.au/" + url);
			}
		}
	}
	
	@Override
	public void handleText(char[] data,int pos)
	{
		//
	}
}
