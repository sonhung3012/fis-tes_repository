package com.contact.jpa.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.RPC)
public interface JpaContactUtils {

	@WebMethod(operationName = "addContactToDatabase")
	void addContactToDatabase(@WebParam Contact contact);

	@WebMethod(operationName = "updateContactToDatabase")
	void updateContactToDatabase(@WebParam int id, @WebParam String familyName, @WebParam String middleName,
			@WebParam String firstName, @WebParam String groupName, @WebParam String sentiment,
			@WebParam String mobilePhone, @WebParam String homeEmail, @WebParam String street, @WebParam String city,
			@WebParam String zip, @WebParam String country, @WebParam String status);
	
	@WebMethod(operationName = "deleteContactToDatabase")
	void deleteContactToDatabase(@WebParam int id);

}
