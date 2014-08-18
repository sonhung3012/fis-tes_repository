package com.addressbook.primefaces.jsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.TreeNode;

import com.addressbook.primefaces.jsf.webservice.Contact;

@ManagedBean(name="contactsView")
@ViewScoped
public class ContactsView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Contact> contacts;
	
	private int count = 100;
	private Contact selectedContact;
	
	private String groupSearch = "All";
	private String sentimentSearch = "All";
	private String fieldSearch = "All";
	private String keySearch = "";
	
	private TreeNode root;
	private TreeNode selectedNode;
	
	@ManagedProperty("#{contactService}")
	private ContactService contactService;
	
	@PostConstruct
	public void init(){
		
		contacts = contactService.createContacts(count);
		root = contactService.createTreeContacts();
		selectedContact = contacts.get(0);
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	public List<String> getGroups(){
		
		return contactService.getGroups();
	}
	
	public List<String> getSentiments(){
		
		return contactService.getSentiments();
	}
	
	public List<String> getFieldNames(){
		
		return contactService.getFieldNames();
	}
	
	public Contact getSelectedContact() {
		return selectedContact;
	}

	public void setSelectedContact(Contact selectedContact) {
		
		this.selectedContact = selectedContact;
	}

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public String getGroupSearch() {
		return groupSearch;
	}

	public void setGroupSearch(String groupSearch) {
		this.groupSearch = groupSearch;
	}

	public String getSentimentSearch() {
		return sentimentSearch;
	}

	public void setSentimentSearch(String sentimentSearch) {
		this.sentimentSearch = sentimentSearch;
	}

	public String getFieldSearch() {
		return fieldSearch;
	}

	public void setFieldSearch(String fieldSearch) {
		this.fieldSearch = fieldSearch;
	}

	public String getKeySearch() {
		return keySearch;
	}

	public void setKeySearch(String keySearch) {
		this.keySearch = keySearch;
	}

	public void onRowSelect(SelectEvent event){
		
		Contact contact = (Contact) event.getObject();
		String contactName = contact.getFamilyName() + " " + contact.getMiddleName() + " " + contact.getFirstName();
		FacesMessage msg = new FacesMessage("Contact Selected", contactName);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void onRowUnselect(UnselectEvent event) {
		
        FacesMessage msg = new FacesMessage("Contact Unselected", ((Contact) event.getObject()).toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
	
	public void addNewContact(){
		
		count++;
		
		Contact newContact = new Contact();
		
		newContact.setId(count);
		newContact.setFamilyName("Your");
		newContact.setMiddleName("New");
		newContact.setFirstName("Contact");
		newContact.setGroupName("Family");
		newContact.setSentiment("Friendly");
		newContact.setStatus("0");
		
		Contact contact = addNewContactToDatabase(newContact);
		
		contacts.add(0, contact);
		
		contactService.getContactsStore().add(0, contact);
		setRoot(contactService.createTreeContacts());
		selectedContact = contacts.get(0);
	}

	public void showAllContact(){
		contacts.clear();
		contacts.addAll(contactService.getContactsStore());
		selectedContact = contacts.get(0);
	}

	public void updateContact(){
		
		updateContactToDatabase(
				selectedContact.getId(), selectedContact.getFamilyName(), selectedContact.getMiddleName(),
				selectedContact.getFirstName(), selectedContact.getGroupName(), selectedContact.getSentiment(),
				selectedContact.getMobilePhone(), selectedContact.getHomeEmail(), selectedContact.getStreet(),
				selectedContact.getCity(), selectedContact.getZip(), selectedContact.getCountry(), selectedContact.getStatus());

		for (Contact contact : contacts) {
			
			if(contact.getId() == selectedContact.getId()){
				
				contact.setFamilyName(selectedContact.getFamilyName());
				contact.setMiddleName(selectedContact.getMiddleName());
				contact.setFirstName(selectedContact.getFirstName());
				contact.setGroupName(selectedContact.getGroupName());
				contact.setSentiment(selectedContact.getSentiment());
				contact.setMobilePhone(selectedContact.getMobilePhone());
				contact.setHomeEmail(selectedContact.getHomeEmail());
				contact.setStreet(selectedContact.getStreet());
				contact.setCity(selectedContact.getCity());
				contact.setZip(selectedContact.getZip());
				contact.setCountry(selectedContact.getCountry());
				contact.setStatus(selectedContact.getStatus());
				
			}
		}
		
		for (Contact contact : contactService.getContactsStore()) {
			
			if(contact.getId() == selectedContact.getId()){
				
				contact.setFamilyName(selectedContact.getFamilyName());
				contact.setMiddleName(selectedContact.getMiddleName());
				contact.setFirstName(selectedContact.getFirstName());
				contact.setGroupName(selectedContact.getGroupName());
				contact.setSentiment(selectedContact.getSentiment());
				contact.setMobilePhone(selectedContact.getMobilePhone());
				contact.setHomeEmail(selectedContact.getHomeEmail());
				contact.setStreet(selectedContact.getStreet());
				contact.setCity(selectedContact.getCity());
				contact.setZip(selectedContact.getZip());
				contact.setCountry(selectedContact.getCountry());
				contact.setStatus(selectedContact.getStatus());
				
			}
		}
		
		setRoot(contactService.createTreeContacts());
		selectedContact = contacts.get(0);
	}
	
    public void deleteContact() {
    	
    	deleteContactToDatabase(selectedContact.getId());
    	
    	for (Contact contact : contacts) {
			
    		if(contact.getId() == selectedContact.getId()){
    			contacts.remove(contact);
    			break;
    		}
		}
    	
    	for (Contact contact : contactService.getContactsStore()) {
			
    		if(contact.getId() == selectedContact.getId()){
    			contactService.getContactsStore().remove(contact);
    			break;
    		}
		}
    	
    	setRoot(contactService.createTreeContacts());
    	selectedContact = contacts.get(0);
    }

    public void searchContact(){
    	
    	List<Contact> contactFind = new ArrayList<Contact>();
    	
    	keySearch = keySearch.trim();
    	
    	if("All".equals(groupSearch) && "All".equals(sentimentSearch) && "All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
				
    			String contactString = (contact.getFamilyName() + " " +  contact.getMiddleName() + " " +  contact.getFirstName() 
    					 + " " +  contact.getMobilePhone() + " " +  contact.getHomeEmail() + " " +  contact.getStreet() + " " +  contact.getCity() 
    					 + " " +  contact.getZip() + " " +  contact.getCountry() + " " +  contact.getStatus()).toLowerCase();
    			if(contactString.toLowerCase().contains(keySearch.toLowerCase())){
    				
    				contactFind.add(contact);
    			}
			}
    	} else if(!"All".equals(groupSearch) && "All".equals(sentimentSearch) && "All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
    			
				if(groupSearch.equals(contact.getGroupName())){
					
	    			String contactString = contact.getFamilyName() + " " +  contact.getMiddleName() + " " +  contact.getFirstName() 
	    					 + " " +  contact.getMobilePhone() + " " +  contact.getHomeEmail() + " " +  contact.getStreet() + " " +  contact.getCity() 
	    					 + " " +  contact.getZip() + " " +  contact.getCountry() + " " +  contact.getStatus();
	    			if(contactString.toLowerCase().contains(keySearch.toLowerCase())){
	    				
	    				contactFind.add(contact);
	    			}
				}
			}
    	} else if("All".equals(groupSearch) && !"All".equals(sentimentSearch) && "All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
    			
				if(sentimentSearch.equals(contact.getSentiment())){
					
	    			String contactString = contact.getFamilyName() + " " +  contact.getMiddleName() + " " +  contact.getFirstName() 
	    					 + " " +  contact.getMobilePhone() + " " +  contact.getHomeEmail() + " " +  contact.getStreet() + " " +  contact.getCity() 
	    					 + " " +  contact.getZip() + " " +  contact.getCountry() + " " +  contact.getStatus();
	    			
	    			if(contactString.toLowerCase().contains(keySearch.toLowerCase())){
	    				
	    				contactFind.add(contact);
	    			}
				}
			}
    	} else if("All".equals(groupSearch) && "All".equals(sentimentSearch) && !"All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
    			
    			String[] contactItems = {contact.getFamilyName(),  contact.getMiddleName(),  contact.getFirstName(), 
    					contact.getMobilePhone(), contact.getHomeEmail(), contact.getStreet(), contact.getCity(),  
    					contact.getZip(), contact.getCountry(), contact.getStatus()};
    			
    			int i = getFieldNames().indexOf(fieldSearch);
    			
    			if(Arrays.asList(contactItems).get(i).toLowerCase().contains(keySearch.toLowerCase())){
    				
    				contactFind.add(contact);
    			}
			}
    	} else if("All".equals(groupSearch) && !"All".equals(sentimentSearch) && !"All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
    			
    			if(sentimentSearch.equals(contact.getSentiment())){
    				
	    			String[] contactItems = {contact.getFamilyName(),  contact.getMiddleName(),  contact.getFirstName(), 
	    					contact.getMobilePhone(), contact.getHomeEmail(), contact.getStreet(), contact.getCity(),  
	    					contact.getZip(), contact.getCountry(), contact.getStatus()};
	    			
	    			int i = getFieldNames().indexOf(fieldSearch);
	    			
	    			if(Arrays.asList(contactItems).get(i).toLowerCase().contains(keySearch.toLowerCase())){
	    				
	    				contactFind.add(contact);
	    			}
    			}
			}
    	} else if(!"All".equals(groupSearch) && "All".equals(sentimentSearch) && !"All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
    			
    			if(groupSearch.equals(contact.getGroupName())){
    				
	    			String[] contactItems = {contact.getFamilyName(),  contact.getMiddleName(),  contact.getFirstName(), 
	    					contact.getMobilePhone(), contact.getHomeEmail(), contact.getStreet(), contact.getCity(),  
	    					contact.getZip(), contact.getCountry(), contact.getStatus()};
	    			
	    			int i = getFieldNames().indexOf(fieldSearch);
	    			
	    			if(Arrays.asList(contactItems).get(i).toLowerCase().contains(keySearch.toLowerCase())){
	    				
	    				contactFind.add(contact);
	    			}
    			}
			}
    	} else if(!"All".equals(groupSearch) && !"All".equals(sentimentSearch) && "All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
    			
				if(groupSearch.equals(contact.getGroupName()) && sentimentSearch.equals(contact.getSentiment())){
					
	    			String contactString = contact.getFamilyName() + " " +  contact.getMiddleName() + " " +  contact.getFirstName() 
	    					 + " " +  contact.getMobilePhone() + " " +  contact.getHomeEmail() + " " +  contact.getStreet() + " " +  contact.getCity() 
	    					 + " " +  contact.getZip() + " " +  contact.getCountry() + " " +  contact.getStatus();
	    			
	    			if(contactString.toLowerCase().contains(keySearch.toLowerCase())){
	    				
	    				contactFind.add(contact);
	    			}
				}
			}
    	} else if(!"All".equals(groupSearch) && !"All".equals(sentimentSearch) && !"All".equals(fieldSearch)){
    		
    		for (Contact contact : contactService.getContactsStore()) {
    			
    			if(groupSearch.equals(contact.getGroupName()) && sentimentSearch.equals(contact.getSentiment())){
    				
	    			String[] contactItems = {contact.getFamilyName(),  contact.getMiddleName(),  contact.getFirstName(), 
	    					contact.getMobilePhone(), contact.getHomeEmail(), contact.getStreet(), contact.getCity(),  
	    					contact.getZip(), contact.getCountry(), contact.getStatus()};
	    			
	    			int i = getFieldNames().indexOf(fieldSearch);
	    			
	    			if(Arrays.asList(contactItems).get(i).toLowerCase().contains(keySearch.toLowerCase())){
	    				
	    				contactFind.add(contact);
	    			}
    			}
			}
    	}
    	
    	if (contactFind.size() != 0) {
    		
			contacts.clear();
			contacts.addAll(contactFind);
			selectedContact = contacts.get(0);
		} else {
			
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Key search could not found", keySearch);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
    }
    
	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
    public void onNodeExpand(NodeExpandEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void onNodeCollapse(NodeCollapseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
 
    public void onNodeSelect(NodeSelectEvent event) {
    	
        TreeNode treeNodeSelect = event.getTreeNode();
        contacts.clear();
        
    	if(this.getGroups().contains((treeNodeSelect.toString()))){
    		
    		for(Contact contact : contactService.getContactsStore()){
    			if (treeNodeSelect.toString().equals(contact.getGroupName())) {
					
    				contacts.add(contact);
				}
    		}
    		
    	} else if(getGroups().contains((treeNodeSelect.getParent().toString())) 
    			&& getSentiments().contains(treeNodeSelect.toString())){
    		
    		for(Contact contact : contactService.getContactsStore()){
    			if (treeNodeSelect.getParent().toString().equals(contact.getGroupName())
    					&& treeNodeSelect.toString().equals(contact.getSentiment())) {
					
    				contacts.add(contact);
				}
    		}
    		
    	} else {
    		
    		for(Contact contact : contactService.getContactsStore()){
    			
    			String contactName = contact.getFamilyName() + " " + contact.getMiddleName() + " " + contact.getFirstName();
    			if (treeNodeSelect.toString().equals(contactName) 
    					&& treeNodeSelect.getParent().toString().equals(contact.getSentiment())
    					&& treeNodeSelect.getParent().getParent().toString().equals(contact.getGroupName())) {
					
    				contacts.add(contact);
				}
    		}
    	}
    	
    	if (contacts.size() != 0) {
    		
			selectedContact = contacts.get(0);
		} else {
			selectedContact = new Contact();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Node has no content", "");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}

    }
 
    public void onNodeUnselect(NodeUnselectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void onDragDrop(TreeDragDropEvent event) {
    	
    	TreeNode dragNode = event.getDragNode();
    	TreeNode dropNode = event.getDropNode();
    	
    	if("Groups".equals(dropNode.toString()) || getGroups().contains(dropNode.toString()) || dropNode.toString().contains(" ")
    			|| getGroups().contains(dragNode.toString()) || getSentiments().contains(dragNode.toString())){
    		
    		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Drag and drop action is denied", "");
    		FacesContext.getCurrentInstance().addMessage(null, message);
    		
    		setRoot(contactService.createTreeContacts());
    		
    	} else {
    	
			for(Contact contact : contactService.getContactsStore()){
				
				String contactName = contact.getFamilyName() + " " + contact.getMiddleName() + " " + contact.getFirstName();
				
				if (dragNode.toString().equals(contactName)) {
					
					contact.setSentiment(dragNode.getParent().toString());
					contact.setGroupName(dragNode.getParent().getParent().toString());
					
					updateContactToDatabase(
							contact.getId(), contact.getFamilyName(), contact.getMiddleName(),
							contact.getFirstName(), contact.getGroupName(), contact.getSentiment(),
							contact.getMobilePhone(), contact.getHomeEmail(), contact.getStreet(),
							contact.getCity(), contact.getZip(), contact.getCountry(), contact.getStatus());
					
					if (contacts.size() != 0) {
						
						for(Contact cont : contacts){
							
							if (cont.getId() == contact.getId()) {
								
								cont.setSentiment(dragNode.getParent().toString());
								cont.setGroupName(dragNode.getParent().getParent().toString());
							}
						}
					}
				}
			}
		}
    }
    
    
	private Contact addNewContactToDatabase(Contact contact) {

//		contactService.setFactory(Persistence.createEntityManagerFactory(ContactService.PERSISTENCE_UNIT));
//		contactService.setEm(contactService.getFactory().createEntityManager());
//		contactService.setTransaction(contactService.getEm().getTransaction());
//		contactService.getTransaction().begin();
//
//		contactService.getEm().persist(contact);
//		
//		contactService.getTransaction().commit();
//		contactService.getEm().close();
//		contactService.getFactory().close();
		
		contactService.getContactPort().addContactToDatabase(contact);
		return contact;
	}

	private void updateContactToDatabase(int id, String familyName, String middleName,
			String firstName, String groupName, String sentiment,
			String mobilePhone, String homeEmail, String street, String city,
			String zip, String country, String status) {

//		contactService.setFactory(Persistence.createEntityManagerFactory(ContactService.PERSISTENCE_UNIT));
//		contactService.setEm(contactService.getFactory().createEntityManager());
//		contactService.setTransaction(contactService.getEm().getTransaction());
//		contactService.getTransaction().begin();
//
//		Contact contact = contactService.getEm().find(Contact.class, id);
//
//		if(contact != null){
//			contact.setFamilyName(familyName);
//			contact.setMiddleName(middleName);
//			contact.setFirstName(firstName);
//			contact.setGroupName(groupName);
//			contact.setSentiment(sentiment);
//			contact.setMobilePhone(mobilePhone);
//			contact.setHomeEmail(homeEmail);
//			contact.setStreet(street);
//			contact.setCity(city);
//			contact.setZip(zip);
//			contact.setCountry(country);
//			contact.setStatus(status);
//		}
//		
//		contactService.getTransaction().commit();
//		contactService.getEm().close();
//		contactService.getFactory().close();
		
		
		contactService.getContactPort().updateContactToDatabase(
				id, familyName, middleName, firstName, groupName, sentiment,
				mobilePhone, homeEmail, street, city, zip, country, status);
	}

	private void deleteContactToDatabase(int id) {

//		contactService.setFactory(Persistence.createEntityManagerFactory(ContactService.PERSISTENCE_UNIT));
//		contactService.setEm(contactService.getFactory().createEntityManager());
//		contactService.setTransaction(contactService.getEm().getTransaction());
//		contactService.getTransaction().begin();
//
//		Contact contact = contactService.getEm().find(Contact.class, id);
//
//		contactService.getEm().remove(contact);
//		contactService.getTransaction().commit();
//		contactService.getEm().close();
//		contactService.getFactory().close();
		
		contactService.getContactPort().deleteContactToDatabase(id);
	}
}