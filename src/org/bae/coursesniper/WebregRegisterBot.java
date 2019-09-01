package org.bae.coursesniper;

import org.openqa.selenium.WebElement;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

public class WebregRegisterBot 
{

	private final static String username = "";
	private final static String password = "";
	
	private WebregRegisterBot() {}
	
	public static void register(String index) throws Exception
	{
		JBrowserDriver driver = new JBrowserDriver(Settings.builder()
				.timezone(Timezone.AMERICA_NEWYORK).build());
		
		driver.get("https://sims.rutgers.edu/webreg/editSchedule.htm?login=cas&semesterSelection=92019&indexList=" + index);
		
		System.out.println("Going to url: " + driver.getCurrentUrl());
		
		// fill in netid
		driver.findElementByName("username").sendKeys(username);
		driver.findElementByName("password").sendKeys(password);
		driver.findElementByName("submit").submit();
		
		System.out.println(driver.getCurrentUrl());
		
		driver.pageWait();
		
		WebElement submit = driver.findElementById("submit");
		if(submit == null)
		{
			System.out.println("Submit button does not exist");
		}else
		{
			submit.click();
		}
		
		// find out if there was an error
		try
		{
			WebElement error = driver.findElementByClassName("error");
			if(error == null)
			{
				System.out.println("register successfull!");
			}else
			{
				System.out.println("error with registering!\n" + error.getText());
			}

			driver.findElementById("logout").click();
			driver.navigate().to("https://cas.rutgers.edu/logout");
		}catch(Exception e)
		{
			System.out.println("error was not found! :)");
		}
		
		driver.quit();
	}
	
}
