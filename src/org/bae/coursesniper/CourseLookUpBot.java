package org.bae.coursesniper;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.io.ShutdownType;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CourseLookUpBot 
{
	
	public static interface OnCourseOpenListener { void onCourseOpen(String index); }
	
	private Course[] courses;
	private OnCourseOpenListener openListener;
	
	public CourseLookUpBot(Course[] courses)
	{
		this.courses = courses;
	}
	
	public void setOnCourseOpen(OnCourseOpenListener listener)
	{
		this.openListener = listener;
	}
	
	public void lookUp() throws Exception
	{
		final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
				.setSoTimeout(Timeout.ofSeconds(5))
				.build();
		final List<Header> headers = new ArrayList<>(1);
		headers.add(new BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.60 Safari/537.1"));
		final CloseableHttpAsyncClient client = HttpAsyncClients.custom()
				.setIOReactorConfig(ioReactorConfig)
				.setDefaultHeaders(headers)
				.build();
		client.start();
		
		final JSONParser parser = new JSONParser();
		final HttpHost target = new HttpHost("sis.rutgers.edu");
		for(int l = 0; l < courses.length; l++)
		{
			final Course course = courses[l];
			final int courseL = l;
			if(course == null) {
				System.err.println("Accessing null... Are you sure it is intentional or a class may have opened and taken!");
				return;
			}
			
			final SimpleHttpRequest httpget = SimpleHttpRequest.get(target, "/oldsoc/courses.json?subject=" + course.getSubject() + "&semester=92019&campus=NB&level=U");
			final Future<SimpleHttpResponse> future = client.execute(httpget,
					new FutureCallback<SimpleHttpResponse>() {
				public void completed(SimpleHttpResponse response) 
				{
					if(response.getCode() == 200)
					{
						String jsonResponse = response.getBodyText();
						try {
							boolean foundCourse = false;
							JSONArray courses = (JSONArray) parser.parse(jsonResponse);
							for(int i = 0; i < courses.size(); i++)
							{
								JSONObject jsonCourse = (JSONObject) courses.get(i);
								if(jsonCourse.get("courseNumber").equals(course.getCourse()))
								{
									// go to sections
									JSONArray sections = (JSONArray) jsonCourse.get("sections");
									for(int j = 0; j < sections.size(); j++)
									{
										JSONObject section = (JSONObject) sections.get(j);
										if(section.get("number").equals(course.getSection()))
										{
											boolean isOpen = (Boolean) section.get("openStatus");
											
											if(isOpen)
											{
												System.out.println(course.toString() + " is open!");
												// call out another bot to go to webreg!
												Toolkit.getDefaultToolkit().beep();
												try {
													String indexStr = (String) section.get("index");
													WebregRegisterBot.register(indexStr);
													
													//CourseLookUpBot.this.courses[courseL] = null;
													
													if(openListener != null)
														openListener.onCourseOpen(indexStr);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}else
											{
												System.out.println(course.toString() + " is closed!");
											}
											
											foundCourse = true;
											break;
										}
									}
									
									if(foundCourse) break;
								}
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				
				public void failed(Exception e) {
					System.out.println("Error: " + e);
				}
				
				public void cancelled() {
					System.out.println("Cancelled");
				}
			});
			
			try {
			future.get();
			}catch(Exception e) { e.printStackTrace(); }
		}
		
		client.shutdown(ShutdownType.GRACEFUL);
	}

}
