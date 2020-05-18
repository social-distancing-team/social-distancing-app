package com.social_distancing.app;

public class HelperClass {
	public static class LOG{
		public static String ERROR = "ERROR";
		public static String SUCCESS = "SUCCESS";
		public static String WARNING = "WARNING";
		public static String INFORMATION = "INFO";
		//	D/(SUCCESS|ERROR|WARNING|INFO)
	}
	
	public static class COLLECTION{
		public static String USERS = "Users";
		public static String LISTS = "Lists";
	}
	
	public static class USER{
		public static String FIRSTNAME = "FirstName";
		public static String LASTNAME = "LastName";
		public static String EMAIL = "Email";
		public static String FRIENDS = "Friends";
		public static String LISTS = "Lists";
	}
	
	public static class LIST{
		public static String USERS = "Users";
		public static String ITEMS = "Items";
	}
}
