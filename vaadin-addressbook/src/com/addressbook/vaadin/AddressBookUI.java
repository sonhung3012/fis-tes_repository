package com.addressbook.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import com.addressbook.vaadin.webservice.Contact;
import com.addressbook.vaadin.webservice.JpaContactUtils;
import com.addressbook.vaadin.webservice.JpaContactUtilsImplService;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
@Theme("addressbook")
public class AddressBookUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AddressBookUI.class)
	public static class Servlet extends VaadinServlet {
	}

	public static final String PERSISTENCE_UNIT = "addressbook";
	
	JpaContactUtilsImplService contactService;
	JpaContactUtils contactPort;

//	private EntityManagerFactory factory;
//	private EntityManager em;
//	private EntityTransaction transaction;

	private Tree contactTree = new Tree();

	/* User interface components are stored in session. */
	private Table contactList = new Table() {

		protected String formatPropertyValue(Object rowId, Object colId,
				Property<?> property) {

			Object val = property.getValue();
			if (val instanceof String) {

				String valueStatus = (String) val;
				if ("status".equalsIgnoreCase(String.valueOf(colId))) {

					if ("1".equalsIgnoreCase(valueStatus)) {

						valueStatus = "active";
					} else {

						valueStatus = "inactive";
					}
				}

				return valueStatus;
			}
			return super.formatPropertyValue(rowId, colId, property);
		};
	};
	private Button searchButton = new Button("Search");
	private Button showAllButton = new Button("Show All");
	private Button addNewContactButton = new Button("New");
	private Button removeContactButton = new Button("Remove this contact");
	private FormLayout editorLayout = new FormLayout();
	private FieldGroup editorFields = new FieldGroup();

	private static final String FAMILYNAME = "Family Name";
	private static final String MIDDLENAME = "Middle Name";
	private static final String FIRSTNAME = "First Name";
	private static final String GROUP = "Group";
	private static final String SENTIMENT = "Sentiment";
	private static final String STATUS = "Status";

	private static final String[] fieldCaptions = new String[] { FAMILYNAME,
			MIDDLENAME, FIRSTNAME, GROUP, SENTIMENT, "Mobile Phone",
			"Home Email", "Street",	"City", "Zip", "Country", STATUS };

	private static final String[] fieldNames = new String[] { "familyName",
			"middleName", "firstName", "groupName", "sentiment", "mobilePhone",
			"homeEmail", "street", "city", "zip", "country", "status" };

	private String[] groupNames = { "Family", "Company", "Classmate",
			"Football" };

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

	/*
	 * Any component can be bound to an external data source. This example uses
	 * just a dummy in-memory list, but there are many more practical
	 * implementations.
	 */

	BeanItemContainer<Contact> contactContainer;
	Map<String, Set<Contact>> contacts = new HashMap<String, Set<Contact>>();

	int count = 0;

	/*
	 * After UI class is created, init() is executed. You should build and wire
	 * up your user interface here.
	 */

	@Override
	protected void init(VaadinRequest request) {
		contactService = new JpaContactUtilsImplService();
		contactPort = contactService.getJpaContactUtilsImplPort();
		contactContainer = createDummyDatasource();
		initLayout();
		initTree();
		initContactList();
		initEditor();
		initSearch();
		initAddRemoveButtons();
	}

	private Contact addNewContact(int id, String familyName, String middleName,
			String firstName, String groupName, String sentiment,
			String mobilePhone, String homeEmail, String street, String city,
			String zip, String country, String status) {

//		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
//		em = factory.createEntityManager();
//		transaction = em.getTransaction();
//		transaction.begin();

//		Contact contact = new Contact(familyName, middleName, firstName,
//				groupName, sentiment, mobilePhone, homeEmail, street, city, zip, country, status);
		
		Contact contact = new Contact();
		contact.setId(id);
		contact.setFamilyName(familyName);
		contact.setMiddleName(middleName);
		contact.setFirstName(firstName);
		contact.setGroupName(groupName);
		contact.setSentiment(sentiment);
		contact.setMobilePhone(mobilePhone);
		contact.setHomeEmail(homeEmail);
		contact.setStreet(street);
		contact.setCity(city);
		contact.setZip(zip);
		contact.setCountry(country);
		contact.setStatus(status);
		
		String contactNewCaption = familyName + " " + middleName + " "
				+ firstName;
		String groSen = groupName + " " + sentiment;

		contactTree.addItem(contact);
		contactTree.setParent(contact, groSen);
		contactTree.setChildrenAllowed(contact, false);
		contactTree.setItemCaption(contact, contactNewCaption);
		
		contactPort.addContactToDatabase(contact);
		
//		em.persist(contact);
//		transaction.commit();
//		em.close();
//		factory.close();
		
		return contact;
	}

	private void updateContact(int id, String familyName, String middleName,
			String firstName, String groupName, String sentiment,
			String mobilePhone, String homeEmail, String street, String city,
			String zip, String country, String status) {

//		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
//		em = factory.createEntityManager();
//		transaction = em.getTransaction();
//		transaction.begin();

//		Contact contact = em.find(Contact.class, id);
		
//		if(contact != null){
//			contact.setFamilyName(familyName);
//			contact.setMiddleName(middleName);
//			contact.setFirstName(firstName);
//			contact.setGroupName(groupName);
//			contact.setSentiment(sentiment);
//			contact.setMobilePhone(mobilePhone);
//			contact.setWorkPhone(workPhone);
//			contact.setHomePhone(homePhone);
//			contact.setWorkEmail(workEmail);
//			contact.setHomeEmail(homeEmail);
//			contact.setStreet(street);
//			contact.setCity(city);
//			contact.setZip(zip);
//			contact.setCountry(country);
//			contact.setStatus(status);
//		}
		
		contactPort.updateContactToDatabase(id, familyName, middleName, firstName,
				groupName, sentiment, mobilePhone, homeEmail, street, city, zip, country, status);
		
//		transaction.commit();
//		em.close();
//		factory.close();
	}

	private void deleteContact(int id) {

//		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
//		em = factory.createEntityManager();
//		transaction = em.getTransaction();
//		transaction.begin();
//
//		Contact contact = em.find(Contact.class, id);
//
//		em.remove(contact);
//		transaction.commit();
//		em.close();
//		factory.close();
		
		contactPort.deleteContactToDatabase(id);
	}

	/*
	 * In this example layouts are programmed in Java. You may choose use a
	 * visual editor, CSS or HTML templates for layout instead.
	 */
	private void initLayout() {

		/* Root of the user interface component tree is set */
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		setContent(splitPanel);

		splitPanel.addComponent(contactTree);

		HorizontalSplitPanel splitPanel2 = new HorizontalSplitPanel();
		splitPanel.addComponent(splitPanel2);

		splitPanel.setSplitPosition(20);

		/* Build the component tree */
		VerticalLayout leftLayout = new VerticalLayout();
		splitPanel2.addComponent(leftLayout);
		splitPanel2.addComponent(editorLayout);
		leftLayout.addComponent(contactList);
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		leftLayout.addComponent(bottomLeftLayout);
		bottomLeftLayout.addComponent(searchButton);
		bottomLeftLayout.addComponent(showAllButton);
		bottomLeftLayout.addComponent(addNewContactButton);

		/* Set the contents in the left of the split panel to use all the space */
		leftLayout.setSizeFull();

		/*
		 * On the left side, expand the size of the contactList so that it uses
		 * all the space left after from bottomLeftLayout
		 */
		leftLayout.setExpandRatio(contactList, 1);
		contactList.setSizeFull();

		/*
		 * In the bottomLeftLayout, searchField takes all the width there is
		 * after adding addNewContactButton. The height of the layout is defined
		 * by the tallest component.
		 */

		/* Put a little margin around the fields in the right side editor */
		editorLayout.setMargin(true);
		editorLayout.setVisible(false);
	}

	private void initTree() {

		for (String group : groupNames) {

			contactTree.addItem(group);

			for (String sentiment : sentiments) {

				String groSen = group + " " + sentiment;

				contactTree.addItem(groSen);
				contactTree.setParent(groSen, group);
				contactTree.setItemCaption(groSen, sentiment);

				for (Contact contact : contacts.get(groSen)) {

					String contactCaption = contact.getFamilyName() + " "
							+ contact.getMiddleName() + " "
							+ contact.getFirstName();

					contactTree.addItem(contact);
					contactTree.setParent(contact, groSen);
					contactTree.setChildrenAllowed(contact, false);
					contactTree.setItemCaption(contact, contactCaption);
				}
			}
		}

		contactTree.setImmediate(true);

		// expand all items
		for (Iterator<?> it = contactTree.rootItemIds().iterator(); it
				.hasNext();)
			contactTree.expandItemsRecursively(it.next());

		// Set the tree in drag source mode
		contactTree.setDragMode(TreeDragMode.NODE);

		// Allow the tree to receive drag drops and handle them
		contactTree.setDropHandler(new DropHandler() {

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

			@Override
			public void drop(DragAndDropEvent event) {

				// Wrapper for the object that is dragged
				Transferable t = event.getTransferable();

				// Make sure the drag source is the same tree
				if (t.getSourceComponent() != contactTree) {

					return;
				}

				TreeTargetDetails target = (TreeTargetDetails) event
						.getTargetDetails();

				// get ids of the dragged item and the target item
				Object sourceItemId = t.getData("itemId");
				Object targetItemId = target.getItemIdOver();

				if (sourceItemId instanceof Contact && targetItemId instanceof Contact) {
					
					Contact contactSource = (Contact) sourceItemId;
					Contact contactTarget = (Contact) targetItemId;
					
					for(Object contactObject : contactList.getItemIds()){
						
						Contact contact = (Contact) contactObject;
						if(contact.getId() == contactSource.getId()){
							
							contact.setGroupName(contactTarget.getGroupName());
							contact.setSentiment(contactTarget.getSentiment());
						}
					}
	
					updateContact(contactSource.getId(),
							contactSource.getFamilyName(),
							contactSource.getMiddleName(),
							contactSource.getFirstName(),
							contactTarget.getGroupName(),
							contactTarget.getSentiment(),
							contactSource.getMobilePhone(),
							contactSource.getHomeEmail(),
							contactSource.getStreet(), contactSource.getCity(),
							contactSource.getZip(), contactSource.getCountry(),
							contactSource.getStatus());

					// on which side of the target the item was dropped
					VerticalDropLocation location = target.getDropLocation();
	
					HierarchicalContainer container = (HierarchicalContainer) contactTree.getContainerDataSource();
	
					// Drop right on an item -> make it a child
					if (location == VerticalDropLocation.MIDDLE) {
	
						contactTree.setParent(sourceItemId, targetItemId);
					}
	
					// Drop at the top of a subtree -> make it previous
					else if (location == VerticalDropLocation.TOP) {
	
						Object parentId = container.getParent(targetItemId);
						container.setParent(sourceItemId, parentId);
	
						container.moveAfterSibling(sourceItemId, targetItemId);
						container.moveAfterSibling(targetItemId, sourceItemId);
					}
	
					// Drop bellow another item -> make it next
					else if (location == VerticalDropLocation.BOTTOM) {
	
						Object parentId = container.getParent(targetItemId);
						container.setParent(sourceItemId, parentId);
						container.moveAfterSibling(sourceItemId, targetItemId);
					}
				}
			}

		});

		contactTree.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				Object valueSelected = event.getProperty().getValue();
				
				if(valueSelected != null) {
					
					if (valueSelected instanceof String) {
						
						String textFilter = (String) valueSelected;
						
						for (String group : groupNames) {
							
							for (String sentiment : sentiments) {
								
								String groSen = group + " " + sentiment;
								if (textFilter.equalsIgnoreCase(group)) {
									
									List<String> arrayGroupFilter = new ArrayList<String>();
									arrayGroupFilter.add("groupName");
		
									contactContainer.removeAllContainerFilters();
									contactContainer.addContainerFilter(new GroupFilter(textFilter, arrayGroupFilter));
									
								} else if (textFilter.equalsIgnoreCase(groSen)) {
									
									List<String> arrayGroupFilter = new ArrayList<String>();
									arrayGroupFilter.add("groupName");
									arrayGroupFilter.add("sentiment");
									String[] textFilterSplit = textFilter.split(" ");
									String textFilter2 = "";
		
									for (String text : textFilterSplit) {
		
										textFilter2 += text + " ";
									}
									contactContainer.removeAllContainerFilters();
									contactContainer.addContainerFilter(new GroupFilter(textFilter2, arrayGroupFilter));
								}
							}
						}
					} else if(valueSelected instanceof Contact) {
				
						Contact contactSelected = (Contact) valueSelected;
						String textFilter = contactSelected.toString();
			
						List<String> arrayFieldFilter = new ArrayList<String>();
			
						arrayFieldFilter.add("groupName");
						arrayFieldFilter.add("sentiment");
						arrayFieldFilter.add("familyName");
						arrayFieldFilter.add("middleName");
						arrayFieldFilter.add("firstName");
			
						contactContainer.removeAllContainerFilters();
						contactContainer.addContainerFilter(new GroupFilter(textFilter, arrayFieldFilter));
					}
				}
			}
		});
	}

	/*
	 * A custom filter for searching names and companies in the
	 * contactContainer.
	 */
	private class GroupFilter implements Filter {

		private String textFilter;
		private List<String> listField;

		public GroupFilter(String textFilter, List<String> listField) {

			this.textFilter = textFilter.toLowerCase();
			this.listField = listField;
		}

		public boolean passesFilter(Object itemId, Item item) {

			String itemString = "";

			for (String field : listField) {

				if (Arrays.asList(fieldNames).contains(field)) {

					itemString += (item.getItemProperty(field).getValue() + " ");
				}
			}
			return itemString.toLowerCase().contains(textFilter);
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}

	private void initEditor() {

		editorLayout.addComponent(removeContactButton);

		/* User interface can be created dynamically to reflect underlying data. */
		for (String fieldName : fieldNames) {

			int i = Arrays.asList(fieldNames).indexOf(fieldName);

			if ("groupName".equalsIgnoreCase(fieldName)) {

				ComboBox comboGroup = new ComboBox(fieldName);
				comboGroup.setCaption(fieldCaptions[i]);
				comboGroup.addValidator(new NullValidator(
						"You must choose a group", false));

				for (String group : groupNames) {
					comboGroup.addItem(group);
				}
				editorLayout.addComponent(comboGroup);
				comboGroup.setWidth("100%");
				editorFields.bind(comboGroup, fieldName);

			} else if ("sentiment".equalsIgnoreCase(fieldName)) {

				ComboBox comboSentiment = new ComboBox(fieldName);
				comboSentiment.setCaption(fieldCaptions[i]);
				comboSentiment.addValidator(new NullValidator(
						"You must choose a sentiment", false));

				for (String sentiment : sentiments) {
					comboSentiment.addItem(sentiment);
				}

				editorLayout.addComponent(comboSentiment);
				comboSentiment.setWidth("100%");
				editorFields.bind(comboSentiment, fieldName);

			} else if ("status".equalsIgnoreCase(fieldName)) {

				ComboBox comboStatus = new ComboBox(fieldName);

				comboStatus.setCaption(fieldCaptions[i]);

				comboStatus.addItem("0");
				comboStatus.addItem("1");

				comboStatus.setItemCaption("0", "inactive");
				comboStatus.setItemCaption("1", "active");

				editorLayout.addComponent(comboStatus);
				comboStatus.setWidth("100%");
				editorFields.bind(comboStatus, fieldName);
			} else {

				TextField field = new TextField(fieldName);

				field.addValidator(new StringLengthValidator(
						"You can only enter a maximum of 50 characters", 0, 50,
						true));
				field.setImmediate(true);

				if ("familyName".equalsIgnoreCase(fieldName)) {

					field.addValidator(new NullValidator(
							"You must enter family name", false));
				}

				if ("middleName".equalsIgnoreCase(fieldName)) {

					field.addValidator(new NullValidator(
							"You must enter middle name", false));
				}

				if ("firstName".equalsIgnoreCase(fieldName)) {

					field.addValidator(new NullValidator(
							"You must enter first name", false));
				}

				if ("mobilePhone".equalsIgnoreCase(fieldName)) {

					field.addValidator(new RegexpValidator("\\d*?",
							"You entered an invalid phone number"));
					field.setImmediate(true);

				} else if ("homeEmail".equalsIgnoreCase(fieldName)) {

					field.addValidator(new EmailValidator(
							"You entered an invalid email"));
					field.setImmediate(true);
				}

				field.setCaption(fieldCaptions[i]);
				editorLayout.addComponent(field);
				field.setWidth("100%");

				/*
				 * We use a FieldGroup to connect multiple components to a data
				 * source at once.
				 */
				editorFields.bind(field, fieldName);
			}
		}

		// if(comboGroup != null){
		// System.out.println("===============================================");
		//
		// System.out.println(comboGroup.getValue().toString());
		// }

		/*
		 * Data can be buffered in the user interface. When doing so, commit()
		 * writes the changes to the data source. Here we choose to write the
		 * changes automatically without calling commit().
		 */
		editorFields.setBuffered(false);
	}

	private void initSearch() {

		searchButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				SearchWindow searchWindow = new SearchWindow();
				UI.getCurrent().addWindow(searchWindow);
			}
		});

		showAllButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				contactContainer.removeAllContainerFilters();

				contactContainer.addContainerFilter(new ContactFilter("", "", "", ""));
			}
		});
	}

	private void initAddRemoveButtons() {

		addNewContactButton.addClickListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				count++;
				/*
				 * Rows in the Container data model are called Item. Here we add
				 * a new row in the beginning of the list.
				 */
				contactContainer.removeAllContainerFilters();
				Object contactId = contactContainer.addItemAt(0, addNewContact(count + 100, 
								"Your", "New", "Contact " + count,
								"Family", "Friendly", "", "", "",
								"", "", "", "0"));

				/*
				 * Each Item has a set of Properties that hold values. Here we
				 * set a couple of those.
				 */

				/* Lets choose the newly created contact to edit it. */
				contactList.select(contactId);
			}
		});

		removeContactButton.addClickListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				Object contactId = contactList.getValue();
				Contact contact = (Contact) contactId;
				
				String groSen = contact.getGroupName() + " " + contact.getSentiment();
				
				for (Object child : contactTree.getChildren(groSen)) {

					if (child instanceof Contact) {
						Contact contactChild = (Contact) child;
						if (contactChild.toString().equalsIgnoreCase(contact.toString())) {

							contactTree.removeItem(contactChild);
							break;
						}
					}
				}
				
				contactList.removeItem(contactId);
				deleteContact(contact.getId());
			}
		});
	}

	private class SearchWindow extends Window {

		public SearchWindow() {

			super("Search"); // set window caption
			center();

			FormLayout searchForm = new FormLayout();

			HorizontalLayout groupSearchLayout = new HorizontalLayout();
			final ComboBox comboGroup = new ComboBox("Group");

			comboGroup.addItem("All");
			for (String group : groupNames) {
				comboGroup.addItem(group);

			}
			comboGroup.setWidth("100%");
			groupSearchLayout.addComponent(comboGroup);
			final ComboBox comboSentiment = new ComboBox("Sentiment");

			comboSentiment.addItem("All");
			for (String sentiment : sentiments) {
				comboSentiment.addItem(sentiment);

			}
			comboSentiment.setWidth("100%");
			groupSearchLayout.addComponent(comboSentiment);

			searchForm.addComponent(groupSearchLayout);

			final ComboBox comboField = new ComboBox("Field Search");

			comboField.addItem("All");
			for (String field : fieldNames) {
				if (!("groupName".equalsIgnoreCase(field) || "sentiment"
						.equalsIgnoreCase(field))) {
					comboField.addItem(field);

					int i = Arrays.asList(fieldNames).indexOf(field);
					comboField.setItemCaption(field, fieldCaptions[i]);
				}
			}
			comboField.setWidth("100%");
			searchForm.addComponent(comboField);

			final TextField keyField = new TextField("Key Search");
			searchForm.addComponent(keyField);
			keyField.setWidth("100%");

			searchForm.setMargin(true);
			setContent(searchForm);

			// setClosable(false);

			Button okButton = new Button("Ok");
			okButton.addClickListener(new ClickListener() {

				@Override
				public void buttonClick(ClickEvent event) {

					String groupSearch = (String) comboGroup.getValue();
					String sentimentSearch = (String) comboSentiment.getValue();
					String fieldSearch = (String) comboField.getValue();
					String textFilter = (String) keyField.getValue();

					contactContainer.removeAllContainerFilters();
					contactContainer.addContainerFilter(new ContactFilter(
							textFilter, fieldSearch, sentimentSearch,
							groupSearch));
					close();
				}
			});

			searchForm.addComponent(okButton);
		}
	}

	/*
	 * A custom filter for searching names and companies in the
	 * contactContainer.
	 */
	private class ContactFilter implements Filter {
		private String keySearch;
		private String fieldSearch;
		private String sentimentSearch;
		private String groupSearch;

		public ContactFilter(String keySearch, String fieldSearch,
				String sentimentSearch, String groupSearch) {
			this.keySearch = keySearch.toLowerCase();

			if (fieldSearch == null
					|| !Arrays.asList(fieldNames).contains(fieldSearch))
				fieldSearch = "All";
			if (groupSearch == null
					|| !Arrays.asList(groupNames).contains(groupSearch))
				groupSearch = "All";
			if (sentimentSearch == null
					|| !Arrays.asList(sentiments).contains(sentimentSearch))
				sentimentSearch = "All";

			this.fieldSearch = fieldSearch;
			this.sentimentSearch = sentimentSearch;
			this.groupSearch = groupSearch;
		}

		public boolean passesFilter(Object itemId, Item item) {

			String itemString = "";
			String itemSentimentString = "";
			String itemGroupString = "";

			if ("All".equalsIgnoreCase(groupSearch)
					&& "All".equalsIgnoreCase(sentimentSearch)
					&& "All".equalsIgnoreCase(fieldSearch)) {

				for (String field : fieldNames) {

					if (!(field.equalsIgnoreCase("groupName") && field
							.equalsIgnoreCase("sentiment"))) {

						itemString += " "
								+ item.getItemProperty(field).getValue();
					}
				}
				return itemString.toLowerCase().contains(keySearch);

			} else if ("All".equalsIgnoreCase(groupSearch)
					&& "All".equalsIgnoreCase(sentimentSearch)
					&& !"All".equalsIgnoreCase(fieldSearch)) {

				itemString = (item.getItemProperty(fieldSearch).getValue())
						+ " ";

				return itemString.toLowerCase().contains(keySearch);

			} else if (!"All".equalsIgnoreCase(groupSearch)
					&& "All".equalsIgnoreCase(sentimentSearch)
					&& "All".equalsIgnoreCase(fieldSearch)) {

				for (String field : fieldNames) {
					if (!field.equalsIgnoreCase("groupName")
							&& !field.equalsIgnoreCase("sentiment")) {

						itemString += item.getItemProperty(field).getValue()
								+ " ";
					}
				}
				itemGroupString = ""
						+ item.getItemProperty("groupName").getValue();

				return itemString.toLowerCase().contains(keySearch)
						&& itemGroupString.equalsIgnoreCase(groupSearch);

			} else if ("All".equalsIgnoreCase(groupSearch)
					&& !"All".equalsIgnoreCase(sentimentSearch)
					&& "All".equalsIgnoreCase(fieldSearch)) {

				for (String field : fieldNames) {
					if (!field.equalsIgnoreCase("groupName")
							&& !field.equalsIgnoreCase("sentiment")) {

						itemString += item.getItemProperty(field).getValue()
								+ " ";
					}
				}

				itemSentimentString = ""
						+ item.getItemProperty("sentiment").getValue();

				return itemString.toLowerCase().contains(keySearch)
						&& itemSentimentString
								.equalsIgnoreCase(sentimentSearch);

			} else if (!"All".equalsIgnoreCase(groupSearch)
					&& !"All".equalsIgnoreCase(sentimentSearch)
					&& "All".equalsIgnoreCase(fieldSearch)) {

				for (String field : fieldNames) {
					if (!field.equalsIgnoreCase("groupName")
							&& !field.equalsIgnoreCase("sentiment")) {

						itemString += item.getItemProperty(field).getValue()
								+ " ";
					}
				}
				itemGroupString = ""
						+ item.getItemProperty("groupName").getValue();
				itemSentimentString = ""
						+ item.getItemProperty("sentiment").getValue();

				return itemString.toLowerCase().contains(keySearch)
						&& itemSentimentString
								.equalsIgnoreCase(sentimentSearch)
						&& itemGroupString.equalsIgnoreCase(groupSearch);

			} else if ("All".equalsIgnoreCase(groupSearch)
					&& !"All".equalsIgnoreCase(sentimentSearch)
					&& !"All".equalsIgnoreCase(fieldSearch)) {

				itemString = item.getItemProperty(fieldSearch).getValue() + " ";
				itemSentimentString = ""
						+ item.getItemProperty("sentiment").getValue();

				return itemString.toLowerCase().contains(keySearch)
						&& itemSentimentString
								.equalsIgnoreCase(sentimentSearch);

			} else if (!"All".equalsIgnoreCase(groupSearch)
					&& "All".equalsIgnoreCase(sentimentSearch)
					&& !"All".equalsIgnoreCase(fieldSearch)) {

				itemString = item.getItemProperty(fieldSearch).getValue() + " ";
				itemGroupString = ""
						+ item.getItemProperty("groupName").getValue();

				return itemString.toLowerCase().contains(keySearch)
						&& itemGroupString.equalsIgnoreCase(groupSearch);

			} else {

				itemString = item.getItemProperty(fieldSearch).getValue() + " ";
				itemGroupString = ""
						+ item.getItemProperty("groupName").getValue();
				itemSentimentString = ""
						+ item.getItemProperty("sentiment").getValue();

				return itemString.toLowerCase().contains(keySearch)
						&& itemGroupString.equalsIgnoreCase(groupSearch)
						&& itemSentimentString
								.equalsIgnoreCase(sentimentSearch);
			}
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}

	private Object contactId = null;
	private String groSenOld = null;

	private void initContactList() {
		contactList.setContainerDataSource(contactContainer);

		contactList.setColumnHeader("familyName", FAMILYNAME);
		contactList.setColumnHeader("middleName", MIDDLENAME);
		contactList.setColumnHeader("firstName", FIRSTNAME);
		contactList.setColumnHeader("groupName", GROUP);

		contactList
				.setVisibleColumns(new Object[] { "familyName", "middleName",
						"firstName", "groupName", "sentiment", "status" });

		contactList.setSelectable(true);
		contactList.setImmediate(true);

		contactList.addValueChangeListener(new Property.ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if (contactId != null) {

					Contact contact = (Contact) contactId;
					
					updateContact(contact.getId(), contact.getFamilyName(),
							contact.getMiddleName(), contact.getFirstName(),
							contact.getGroupName(), contact.getSentiment(),
							contact.getMobilePhone(),
							contact.getHomeEmail(), contact.getStreet(),
							contact.getCity(), contact.getZip(),
							contact.getCountry(), contact.getStatus());

					String groSenUpdate = contact.getGroupName() + " " + contact.getSentiment();
					String contactCaptionUpdate = contact.getFamilyName() + " "	+ contact.getMiddleName() + " "	+ contact.getFirstName();

					for (Object child : contactTree.getChildren(groSenOld)) {

						if (child instanceof Contact) {
							Contact contactChild = (Contact) child;
							if (contactChild.toString().equalsIgnoreCase(contact.toString())) {
								contactTree.setItemCaption(contactChild, contactCaptionUpdate);
								if (!groSenOld.equalsIgnoreCase(groSenUpdate)) {

									contactTree.setParent(contactChild,	groSenUpdate);
									break;
								}
							}
						}
					}

				}
				contactId = contactList.getValue();
				/*
				 * When a contact is selected from the list, we want to show
				 * that in our editor on the right. This is nicely done by the
				 * FieldGroup that binds all the fields to the corresponding
				 * Properties in our contact at once.
				 */
				if (contactId != null) {
					
					Contact contact = (Contact) contactId;
					groSenOld = contact.getGroupName() + " " + contact.getSentiment();
					editorFields.setItemDataSource(contactList.getItem(contactId));
				}

				editorLayout.setVisible(contactId != null);
			}
		});
	}

	/*
	 * Generate some in-memory example data to play with. In a real application
	 * we could be using SQLContainer, JPAContainer or some other to persist the
	 * data.
	 */
	private BeanItemContainer<Contact> createDummyDatasource() {

//		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
//		em = factory.createEntityManager();
//		transaction = em.getTransaction();
//		transaction.begin();

		BeanItemContainer<Contact> contactsItem = new BeanItemContainer<Contact>(
				Contact.class);

		Set<Contact> contactsFriendlyFamilySet = new HashSet<Contact>();
		Set<Contact> contactsFriendlyCompanySet = new HashSet<Contact>();

		Set<Contact> contactsFriendlyClassmateSet = new HashSet<Contact>();
		Set<Contact> contactsFriendlyFootballSet = new HashSet<Contact>();

		Set<Contact> contactsUnFriendlyFamilySet = new HashSet<Contact>();
		Set<Contact> contactsUnFriendlyCompanySet = new HashSet<Contact>();

		Set<Contact> contactsUnFriendlyClassmateSet = new HashSet<Contact>();
		Set<Contact> contactsUnFriendlyFootballSet = new HashSet<Contact>();

		for (int i = 1; i <= 100; i++) {

			int number = (int) (9999 * Math.random());
			number = number > 999 ? number : number + 1000;

			String familyName = familyNames[(int) (familyNames.length * Math.random())];
			String middleName = middleNames[(int) (middleNames.length * Math.random())];
			String firstName = firstNames[(int) (firstNames.length * Math.random())];
			String groupName = groupNames[(int) (groupNames.length * Math.random())];
			String sentiment = sentiments[(int) (sentiments.length * Math.random())];
			String mobilePhone = "098765" + number;
			String homeEmail = familyName + middleName + firstName + "@gmail.com";
			String city = cityNames[(int) (cityNames.length * Math.random())];

			String zip = "" + number;
			String street = streets[(int) (streets.length * Math.random())];
			String country = "Vietnam";
			String status = statusNames[(int) (statusNames.length * Math
					.random())];

//			Contact contact = new Contact(familyName, middleName, firstName,
//					groupName, sentiment, mobilePhone, homeEmail, street, city, zip, country, status);
			
			Contact contact = new Contact();
			
			contact.setId(i);
			contact.setFamilyName(familyName);
			contact.setMiddleName(middleName);
			contact.setFirstName(firstName);
			contact.setGroupName(groupName);
			contact.setSentiment(sentiment);
			contact.setMobilePhone(mobilePhone);
			contact.setHomeEmail(homeEmail);
			contact.setStreet(street);
			contact.setCity(city);
			contact.setZip(zip);
			contact.setCountry(country);
			contact.setStatus(status);
			
			String groSen = groupName + " " + sentiment;

			if ("Family".equalsIgnoreCase(groupName)) {

				if ("Friendly".equalsIgnoreCase(sentiment)) {

					contactsFriendlyFamilySet.add(contact);
					this.contacts.put(groSen, contactsFriendlyFamilySet);
				} else if ("UnFriendly".equalsIgnoreCase(sentiment)) {

					contactsUnFriendlyFamilySet.add(contact);
					this.contacts.put(groSen, contactsUnFriendlyFamilySet);
				}
			} else if ("Company".equalsIgnoreCase(groupName)) {

				if ("Friendly".equalsIgnoreCase(sentiment)) {

					contactsFriendlyCompanySet.add(contact);
					this.contacts.put(groSen, contactsFriendlyCompanySet);
				} else if ("UnFriendly".equalsIgnoreCase(sentiment)) {

					contactsUnFriendlyCompanySet.add(contact);
					this.contacts.put(groSen, contactsUnFriendlyCompanySet);
				}
			} else if ("Classmate".equalsIgnoreCase(groupName)) {

				if ("Friendly".equalsIgnoreCase(sentiment)) {

					contactsFriendlyClassmateSet.add(contact);
					this.contacts.put(groSen, contactsFriendlyClassmateSet);
				} else if ("UnFriendly".equalsIgnoreCase(sentiment)) {

					contactsUnFriendlyClassmateSet.add(contact);
					this.contacts.put(groSen, contactsUnFriendlyClassmateSet);
				}
			} else if ("Football".equalsIgnoreCase(groupName)) {

				if ("Friendly".equalsIgnoreCase(sentiment)) {

					contactsFriendlyFootballSet.add(contact);
					this.contacts.put(groSen, contactsFriendlyFootballSet);
				} else if ("UnFriendly".equalsIgnoreCase(sentiment)) {

					contactsUnFriendlyFootballSet.add(contact);
					this.contacts.put(groSen, contactsUnFriendlyFootballSet);
				}
			}
			contactPort.addContactToDatabase(contact);
			
//			em.persist(contact);
			contactsItem.addBean(contact);
		}

//		transaction.commit();

		// em.close();
		// factory.close();

		return contactsItem;
	}
}