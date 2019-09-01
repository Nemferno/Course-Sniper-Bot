package org.bae.coursesniper;

import javax.swing.JFrame;

import org.bae.coursesniper.CourseLookUpBot.OnCourseOpenListener;

public class Application extends JFrame
{

	public static void main(String[] args) throws Exception
	{
		Course courses[] = {
			new Course("960", "384", "01"),
			new Course("750", "206", "02"),
			new Course("750", "204", "03"),
			new Course("390", "410", "01"),
			new Course("630", "301", "06"),
			new Course("390", "420", "04"),
			new Course("390", "385", "02")
		};
		
		CourseLookUpBot bot = new CourseLookUpBot(courses);
		bot.setOnCourseOpen(new OnCourseOpenListener() {
			@Override
			public void onCourseOpen(String index) 
			{
				
			}
		});
		long curr = System.currentTimeMillis();
		while(true)
		{
			long time = System.currentTimeMillis();
			if(time - curr > 2000)
			{
				bot.lookUp();
				curr = System.currentTimeMillis();
			}
		}
	}
	
}
