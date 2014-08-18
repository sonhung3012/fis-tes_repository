package com.contact.jpa.ws;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

@WebService(endpointInterface = "com.contact.jpa.ws.JpaContactUtils")
public class JpaContactUtilsImpl implements JpaContactUtils{
	
	EntityManagerFactory factory = null;
	EntityManager em = null;
	public JpaContactUtilsImpl(){
		
		if(em != null){
			
			em.close();
		}
		
		if(factory != null){
			factory.close();
		}
		
		factory = Persistence.createEntityManagerFactory("addressbook");
		em = factory.createEntityManager();
		
	}
	
	public void addContactToDatabase(Contact contact) {
		
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		em.persist(contact);
		transaction.commit();
		
	}

	public void updateContactToDatabase(int id, String familyName, String middleName,
			String firstName, String groupName, String sentiment,
			String mobilePhone, String homeEmail, String street, String city,
			String zip, String country, String status) {
		
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		Contact contactUpdate = em.find(Contact.class, id);

		if(contactUpdate != null){
			contactUpdate.setFamilyName(familyName);
			contactUpdate.setMiddleName(middleName);
			contactUpdate.setFirstName(firstName);
			contactUpdate.setGroupName(groupName);
			contactUpdate.setSentiment(sentiment);
			contactUpdate.setMobilePhone(mobilePhone);
			contactUpdate.setHomeEmail(homeEmail);
			contactUpdate.setStreet(street);
			contactUpdate.setCity(city);
			contactUpdate.setZip(zip);
			contactUpdate.setCountry(country);
			contactUpdate.setStatus(status);
		}
		
		transaction.commit();

	}

	public void deleteContactToDatabase(int id) {
		
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		Contact contactDelete = em.find(Contact.class, id);
		
		if(contactDelete != null) {
			em.remove(contactDelete);
		}
		
		transaction.commit();
	}

}
