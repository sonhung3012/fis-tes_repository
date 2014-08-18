package com.contact.jpa.ws;

import javax.xml.ws.Endpoint;

//Endpoint Publisher
public class JpaContactUtilsPublisher {

	public static void main(String[] args) {
		
		Endpoint.publish("http://localhost:9999/ws/jpa/contact", new JpaContactUtilsImpl());
	}
}
