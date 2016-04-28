<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html> 
<html data-ng-app='myApp'>
 	<head>
		<%@ page import = "java.util.ArrayList"%>
		<%@ page import = "java.util.List"%>
		<%@ page import = "java.util.Map" %>
		<%@ page import = "org.json.JSONArray" %>
		<%@ page import = "org.json.JSONException" %>
		<%@ page import = "org.json.JSONObject" %>
		<%@ page import = "java.util.Collection" %>
		<meta name="viewport" content="width=device-width, initial-scale=1.0,user-scalable=0" />

		<link href="../css/bootstrap.min.css" rel="stylesheet" />
		<link href="../css/style.css" rel="stylesheet" />
	</head>
	
	<body data-ng-controller="nativeFeaturesAppController">
		<% 
			JSONArray array = new JSONArray(requestMap.get("contacts"));
			String str = (String)array.get(0);
			JSONArray contactsArray = new JSONArray(str);
	
		%>

		<h1>Testing successful!!!!</h1>

		<%= "Size of contactsArray: ------> "+contactsArray.length() %>

		<script src="../js/Controller.js"></script>
		<script src="../js/jquery-1.7.1.js"></script>
		<script src="../js/angular.min.js"></script>
	</body>
</html>