package com.contact.jpa.wsimport.client;

import com.contact.jpa.wsimport.ws.Contact;
import com.contact.jpa.wsimport.ws.JpaContactUtils;
import com.contact.jpa.wsimport.ws.JpaContactUtilsImplService;

public class JpaContactUtilsClient {
	
	public static void main(String[] args) {
		
		JpaContactUtilsImplService contactService = new JpaContactUtilsImplService();
		JpaContactUtils contactPort = contactService.getJpaContactUtilsImplPort();
		
		Contact contact = new Contact();
		contact.setFamilyName("Nguyen");
		
		contactPort.addContactToDatabase(contact);
		
//		contactPort.updateContactToDatabase(1, "Tran", "", "", "", "", "", "", "", "", "", "", "");
		
//		contactPort.deleteContactToDatabase(1);

	}
}
