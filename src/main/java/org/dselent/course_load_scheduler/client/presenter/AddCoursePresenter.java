package org.dselent.course_load_scheduler.client.presenter;

import java.util.List;

import org.dselent.course_load_scheduler.client.model.Courses;
import org.dselent.course_load_scheduler.client.model.Frequency;

public interface AddCoursePresenter extends BasePresenter{
	//helpful functions, like managing event requests to move to different pages and loading information into the dropdowns
	
	//parent info
	public IndexPresenter getParentPresenter();
	public void setParentPresenter(IndexPresenter parentPresenter);
	
	public void fillFrequencies();
	
	public void returnToViewCourses();
	
	public boolean submitNewCourse();
	
	public List<Frequency> retrieveFequencies();
}
