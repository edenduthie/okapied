package okapied.util;

import java.io.File;


public class JarOutputScript 
{

	public static void main(String[] args)
	{
		File file = new File("lib");
		if( file.isDirectory() )
		{
			File[] files = file.listFiles();
			for( File jarFile : files )
			{
//				String line = 
//				    "<property name=\"" + jarFile.getName() + 
//				    "\" value=\"lib/" + jarFile.getName() + "\" />";
//				
//				String line = 
//			        "<pathelement location=\"${" +
//			        jarFile.getName() + "}\"/>";
				
				String line = 
					"<copy todir=\"${build.home}/WEB-INF/lib\" file=\"${" +
					jarFile.getName() +
					"}\"/>";
					
				
				System.out.println(line);
			}
		}
	}

}
