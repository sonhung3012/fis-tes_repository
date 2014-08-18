package com.contact.jpa.ws;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Contact implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String familyName;
	private String middleName;
	private String firstName;
	private String groupName;
	private String sentiment;
	private String mobilePhone;
	private String homeEmail;
	private String street;
	private String city;
	private String zip;
	private String country;
	private String status;

	public Contact(){}
	
	public Contact(String familyName, String middleName, String firstName, String groupName, String sentiment, 
			String mobilePhone, String homeEmail, String street, String city,
			String zip, String country, String status) {
		
		this.familyName = familyName;
		this.middleName = middleName;
		this.firstName = firstName;
		this.groupName = groupName;
		this.sentiment = sentiment;
		this.mobilePhone = mobilePhone;
		this.homeEmail = homeEmail;
		this.street = street;
		this.city = city;
		this.zip = zip;
		this.country = country;
		this.status = status;
		if("active".equalsIgnoreCase(status)) {
			
			this.status = "1";
		} else {

			this.status = "0";
		}

	}

	
	public Contact(int id, String familyName, String middleName, String firstName, String groupName, String sentiment,  
			String mobilePhone, String homeEmail, String street, String city,
			String zip, String country,  String status) {
		
		this(familyName, middleName, firstName, groupName, sentiment,  
			mobilePhone, homeEmail, street, city, zip, country, status);
		this.id = id;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getHomeEmail() {
		return homeEmail;
	}
	public void setHomeEmail(String homeEmail) {
		this.homeEmail = homeEmail;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	
	@Override
	public String toString() {
		return familyName + " " + middleName + " " + firstName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSentiment() {
		return sentiment;
	}

	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	
}