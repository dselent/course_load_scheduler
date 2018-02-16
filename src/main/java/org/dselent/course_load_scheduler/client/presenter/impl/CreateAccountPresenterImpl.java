package org.dselent.course_load_scheduler.client.presenter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dselent.course_load_scheduler.client.action.InvalidLoginAction;
import org.dselent.course_load_scheduler.client.action.SendLoginAction;
import org.dselent.course_load_scheduler.client.errorstring.InvalidAccountCreationStrings;
import org.dselent.course_load_scheduler.client.errorstring.InvalidLoginStrings;
import org.dselent.course_load_scheduler.client.event.InvalidLoginEvent;
import org.dselent.course_load_scheduler.client.event.SendLoginEvent;
import org.dselent.course_load_scheduler.client.exceptions.EmptyStringException;
import org.dselent.course_load_scheduler.client.exceptions.PasswordCharacterException;
import org.dselent.course_load_scheduler.client.exceptions.PasswordLengthException;
import org.dselent.course_load_scheduler.client.presenter.CreateAccountPresenter;
import org.dselent.course_load_scheduler.client.presenter.IndexPresenter;
import org.dselent.course_load_scheduler.client.view.CreateAccountView;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;

public class CreateAccountPresenterImpl extends BasePresenterImpl implements CreateAccountPresenter {

	private CreateAccountView view;
	private IndexPresenter parentPresenter;
	private boolean createAccountClickInProgress;

	@Inject
	public CreateAccountPresenterImpl(IndexPresenter parentPresenter, CreateAccountView view) {
		this.view = view;
		this.parentPresenter = parentPresenter;
		view.setPresenter(this);
		createAccountClickInProgress = false;
	}

	@Override
	public void init()
	{
		bind();
	}

	@Override
	public void bind()
	{
		HandlerRegistration registration;

		//registration = eventBus.addHandler(InvalidLoginEvent.TYPE, this);
		//eventBusRegistration.put(InvalidLoginEvent.TYPE, registration);
	}

	@Override
	public void go(HasWidgets container)
	{
		container.clear();
		container.add(view.getWidgetContainer());
	}

	public CreateAccountView getView() {
		return view;
	}

	@Override
	public IndexPresenter getParentPresenter() {
		return parentPresenter;
	}

	@Override
	public void setParentPresenter(IndexPresenter parentPresenter) {
		this.parentPresenter = parentPresenter;
	}

	@Override
	public void createAccount() {

		if(!createAccountClickInProgress)
		{
			createAccountClickInProgress = true;
			view.getSubmitButton().setEnabled(false);
			parentPresenter.showLoadScreen();

			String firstName = view.getFirstNameTextBox().getText();
			String lastName = view.getLastNameTextBox().getText();
			String email = view.getEmailTextBox().getText();
			ListBox ftBox = view.getFacultyTypeComboBox();
			String facultyType = ftBox.getValue(ftBox.getSelectedIndex());
			String userName = view.getUserNameTextBox().getText();
			String password = view.getPasswordTextBox().getText();
			String confirmPassword = view.getConfirmPasswordTextBox().getText();

			boolean validFirstName = true;
			boolean validLastName = true;
			boolean validEmail = true;
			boolean validUserName = true;
			boolean validPassword = true;

			List<String> invalidReasonList = new ArrayList<>();

			//Add checks for null firstName, lastName, and Email, and check for pw and confirmPW to match
			try
			{
				validateUserName(userName);
			}
			catch(EmptyStringException e)
			{
				invalidReasonList.add(InvalidLoginStrings.NULL_USER_NAME);
				validUserName = false;
			}

			try
			{
				validatePassword(password);
			}
			catch(EmptyStringException e)
			{
				invalidReasonList.add(InvalidLoginStrings.NULL_PASSWORD);
				validPassword = false;
			}catch(PasswordLengthException e) {
				invalidReasonList.add(InvalidAccountCreationStrings.TOO_SHORT);
				validPassword = false;
			}catch(PasswordCharacterException e) {
				invalidReasonList.add(InvalidAccountCreationStrings.NO_SPECIAL_CHARACTERS);
				validPassword = false;

			}
			if(validUserName && validPassword)
			{
				sendCreateAccount(userName, password);
			}
			else
			{
				InvalidLoginAction ila = new InvalidLoginAction(invalidReasonList);
				InvalidLoginEvent ile = new InvalidLoginEvent(ila);
				eventBus.fireEvent(ile);
			}
		}
	}

	private void validateUserName(String userName) throws EmptyStringException {
		checkEmptyString(userName);
	}

	private void validatePassword(String password) throws EmptyStringException, PasswordLengthException, PasswordCharacterException{
		checkEmptyString(password);
		checkForSpecialCharacter(password);
		if(password.length() < 8) {
			throw new PasswordLengthException();
		}
	}

	//check for a non-alphanumeric character
	private void checkForSpecialCharacter(String password) throws PasswordCharacterException {
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(password);
		if(!m.find()) {
			throw new PasswordCharacterException();
		}
	}

	private void checkEmptyString(String string) throws EmptyStringException{
		if(string == null || string.equals(""))
		{
			throw new EmptyStringException();
		}		
	}

	private void sendCreateAccount(String userName, String password) {
		SendCreateAccountAction scaa = new SendCreateAccountAction(userName, password);
		SendCreateAccountEvent scae = new SendCreateAccountEvent(scaa);
		eventBus.fireEvent(scae);
	}
}