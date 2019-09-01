package org.bae.coursesniper;

public class Course 
{

	private String subject,
				   course,
				   section;
	
	public Course(String subject, String course, String section)
	{
		this.subject = subject;
		this.course = course;
		this.section = section;	
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	public String getCourse()
	{
		return course;
	}
	
	public String getSection()
	{
		return section;
	}
	
	public String toString()
	{
		return subject + ":" + course + ":" + section;
	}
	
}
