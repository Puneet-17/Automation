package com.huseby.framework.core;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

public class RestUtil {
		
	public static String path;

	/**
	 * Sets Base URI*** Before starting the test, we should set the
	 * @param baseURI
	 */
	public static void setBaseURI(String baseURI) {
		RestAssured.baseURI = baseURI;
	}

	/**
	 * Reset Base URI (after test)*** After the test, we should reset the
	 * RestAssured.baseURI
	 */
	public static void resetBaseURI() {
		RestAssured.baseURI = null;
	}
	
	/**
	 * Get reposne using Get method
	 * @return
	 */
	public static Response getResponse(String path) {
		return get(path);
	}

	
	/**
	 * Post method method
	 * @param contentType
	 * @param body
	 * @param path
	 * @return
	 */
	public static Response postData(String contentType, String body, String path){
		return given().contentType(contentType).body(body).when().post(path);		
	}

	/**
	 * Update date using put method
	 * @param contentType
	 * @param body
	 * @param path
	 * @return
	 */
	public static Response putData(String contentType, String body, String path){
		return given().contentType(contentType).body(body).when().put(path);
	}
	
	/**
	 * delete method
	 * @param path 
	 * @param value
	 * @return
	 */
	public static Response deleteMethod(String path, long value){
		return given().when().delete(path + "/" + value);
	}
	
	/**
	 * Get method call
	 * @param path
	 * @param value
	 * @return
	 */
	public static Response getData(String path){
		return given().when().get(path);
	}
	
	/**
	 * Verify response code
	 * @param response
	 * @param code
	 */
	public static void verifyResponseCode(Response response, int code){
		response.then().statusCode(code);
	}
	
	public static void verifyResponseContentType(Response response, String contentType){
		response.then().contentType(contentType);
	}
}



