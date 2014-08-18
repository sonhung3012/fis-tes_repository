package com.addressbook.primefaces.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.addressbook.primefaces.jsf.webservice.Contact;
import com.addressbook.primefaces.jsf.webservice.JpaContactUtils;
import com.addressbook.primefaces.jsf.webservice.JpaContactUtilsImplService;

@ManagedBean(name = "contactService")
@ApplicationScoped
public class ContactService implements Serializable {

	private static final long serialVersionUID = 1L;
	
//	public static final String PERSISTENCE_UNIT = "addressbook";
//	private EntityManagerFactory factory;
//	private EntityManager em;
//	private EntityTransaction transaction;

	JpaContactUtilsImplService service;
	JpaContactUtils contactPort;
	
	private int numberRandom = (int) (9999 * Math.random());
	private int number = numberRandom > 999 ? numberRandom : numberRandom + 1000;
	private String familyName;
	private String middleName;
	private String firstName;
	
	private String[] fileNames = {
			"Family Name", "Middle Name", "First Name",  
			"Mobile Phone", "Home Email", "Street", "City",
			"Zip", "Country", "Status"};
	
	private String[] groupNames = { "Family", "Company", "Classmate", "Football" };

	private String[] sentiments = { "Friendly", "Unfriendly" };
	
	private String[] cityNames = { "Hanoi", "Hai Phong", "Hai Duong",
		"Nam Dinh", "Vinh", "Da Nang", "Hue", "Binh Duong",
		"Ho Chi Minh City", "Can Tho" };
	
	private String[] familyNames = { "Nguyen", "Tran", "Le", "Ly", "Dinh",
		"Ngo", "Pham", "Duong", "Trinh", "Do" };
	
	private String[] statusNames = { "0", "1" };
	
	private String[] middleNames = { "Van", "Son", "Anh", "Xuan", "Duc", "Cao",
		"Tuan", "Quang", "Manh", "Duy" };
	
	private String[] firstNames = { "Hung", "Dung", "Cuong", "Chien", "Trung",
		"Long", "Hai", "Huy", "Hoang", "Thang" };
	
	private String[] streets = { "Ho Chi Minh", "Tran Hung Dao",
		"Vo Nguyen Giap", "Tran Phu", "Hai Ba Trung", "Ly Thuong Kiet",
		"Dai Co Viet", "Kim Ma", "Le Duan", "Giai Phong" };
	
	private List<Contact> contactsStore = new ArrayList<Contact>();
	
	public List<Contact> createContacts(int size) {
		
//		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
//		em = factory.createEntityManager();
//		transaction = em.getTransaction();
//		transaction.begin();

		service = new JpaContactUtilsImplService();
		contactPort = service.getJpaContactUtilsImplPort();
		
		List<Contact> listContact = new ArrayList<Contact>();
		
		for (int i = 1; i <= size; i++) {
			
			Contact contact = new Contact();
			
			contact.setId(i);
			contact.setFamilyName(getRandomFamilyName());
			contact.setMiddleName(getRandomMiddleName());
			contact.setFirstName(getRandomFirstName());
			contact.setGroupName(getRandomGroupName());
			contact.setSentiment(getRandomSentiment());
			contact.setMobilePhone(getRandomMobilePhone());
			contact.setHomeEmail(getRandomHomeEmail());
			contact.setStreet(getRandomStreet());
			contact.setCity(getRandomCity());
			contact.setZip(getRandomZip());
			contact.setCountry(getRandomCountry());
			contact.setStatus(getRandomStatus());
			
//			em.persist(contact);
			
			contactPort.addContactToDatabase(contact);
			listContact.add(contact);
		}
		
//		transaction.commit();
//		em.close();
//		factory.close();

		contactsStore.addAll(listContact);
		
		return listContact;
	}
	
	public TreeNode createTreeContacts() {
		
		TreeNode root = new DefaultTreeNode("Groups", null);
		
		TreeNode familyGroup = new DefaultTreeNode("Family", root);
		TreeNode companyGroup = new DefaultTreeNode("Company", root);
		TreeNode classmateGroup = new DefaultTreeNode("Classmate", root);
		TreeNode footballGroup = new DefaultTreeNode("Football", root);
		
		TreeNode familyFriendlyGroup = new DefaultTreeNode("Friendly", familyGroup);
		TreeNode familyUnfriendlyGroup = new DefaultTreeNode("Unfriendly", familyGroup);
		
		TreeNode companyFriendlyGroup = new DefaultTreeNode("Friendly", companyGroup);
		TreeNode companyUnfriendlyGroup = new DefaultTreeNode("Unfriendly", companyGroup);
		
		TreeNode classmateFriendlyGroup = new DefaultTreeNode("Friendly", classmateGroup);
		TreeNode classmateUnfriendlyGroup = new DefaultTreeNode("Unfriendly", classmateGroup);
		
		TreeNode footballFriendlyGroup = new DefaultTreeNode("Friendly", footballGroup);
		TreeNode footballUnfriendlyGroup = new DefaultTreeNode("Unfriendly", footballGroup);
		
		for(Contact contact : contactsStore){
			
			String contactName = contact.getFamilyName() + " " + contact.getMiddleName() + " " + contact.getFirstName();
			
			if("Family".equals(contact.getGroupName()) && "Friendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, familyFriendlyGroup);
			} else if("Family".equals(contact.getGroupName()) && "Unfriendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, familyUnfriendlyGroup);
			} else if("Company".equals(contact.getGroupName()) && "Friendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, companyFriendlyGroup);
			} else if("Company".equals(contact.getGroupName()) && "Unfriendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, companyUnfriendlyGroup);
			} else if("Classmate".equals(contact.getGroupName()) && "Friendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, classmateFriendlyGroup);
			} else if("Classmate".equals(contact.getGroupName()) && "Unfriendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, classmateUnfriendlyGroup);
			} else if("Football".equals(contact.getGroupName()) && "Friendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, footballFriendlyGroup);
			} else if("Football".equals(contact.getGroupName()) && "Unfriendly".equals(contact.getSentiment())){
				
				new DefaultTreeNode("document", contactName, footballUnfriendlyGroup);
			}
		}
		
		return root;
	}
	
	private String getRandomFamilyName(){
		
		return familyName = familyNames[(int) (familyNames.length * Math.random())];
	}

	private String getRandomMiddleName(){
		
		return middleName = middleNames[(int) (middleNames.length * Math.random())];
	}
	
	private String getRandomFirstName(){
		
		return firstName = firstNames[(int) (firstNames.length * Math.random())];
	}
	
	private String getRandomGroupName(){
		
		return groupNames[(int) (groupNames.length * Math.random())];
	}
	
	private String getRandomSentiment(){
		
		return sentiments[(int) (sentiments.length * Math.random())];
	}
	
	private String getRandomMobilePhone(){
		
		return "098765" + number;
	}
	
	private String getRandomHomeEmail(){
		
		return familyName + middleName + firstName + "@gmail.com";
	}
	
	private String getRandomCity(){
		
		return cityNames[(int) (cityNames.length * Math.random())];
	}
	
	private String getRandomZip(){
		
		return "" + number;
	}
	
	private String getRandomStreet(){
		
		return streets[(int) (streets.length * Math.random())];
	}

	private String getRandomCountry(){
		
		return "Vietnam";
	}
	
	private String getRandomStatus(){
		
		return statusNames[(int) (statusNames.length * Math.random())];
	}
	
	public List<String> getGroups(){
		
		return Arrays.asList(groupNames);
	}
	
	public List<String> getSentiments(){
		
		return Arrays.asList(sentiments);
	}
	
	public List<String> getFieldNames(){
		
		return Arrays.asList(fileNames);
	}
	
	public List<Contact> getContactsStore() {
		return contactsStore;
	}

	public void setContactsStore(List<Contact> contactsStore) {
		this.contactsStore = contactsStore;
	}

	public JpaContactUtils getContactPort() {
		return contactPort;
	}

	public void setContactPort(JpaContactUtils contactPort) {
		this.contactPort = contactPort;
	}

//	public EntityManagerFactory getFactory() {
//		return factory;
//	}
//
//	public void setFactory(EntityManagerFactory factory) {
//		this.factory = factory;
//	}
//
//	public EntityManager getEm() {
//		return em;
//	}
//
//	public void setEm(EntityManager em) {
//		this.em = em;
//	}
//
//	public EntityTransaction getTransaction() {
//		return transaction;
//	}
//
//	public void setTransaction(EntityTransaction transaction) {
//		this.transaction = transaction;
//	}
}
