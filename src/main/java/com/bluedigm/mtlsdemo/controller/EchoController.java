package com.bluedigm.mtlsdemo.controller;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j;

@RestController
public class EchoController {
	
	@RequestMapping("**")
	@ResponseBody
	public ResponseEntity<String> echo( 
			HttpServletRequest request,
			@RequestBody @Nullable String reqBody, 
			@RequestHeader @Nullable MultiValueMap<String, String> reqHeader) {
		
		
		List<Map<String, Object>> certList = new ArrayList<>();
		
		X509Certificate[] certs = (X509Certificate[])request.getAttribute("javax.servlet.request.X509Certificate");
		for(X509Certificate cert : certs) {
			
			BigInteger clientSerialNumber = cert.getSerialNumber();
			String subject = cert.getSubjectX500Principal().toString();
			int certVersion = cert.getVersion();
			
			Map<String, Object> certInfo = new HashMap<>();
			certInfo.put("clientSerialNumber", clientSerialNumber);
			certInfo.put("subject", subject);
			certInfo.put("certVersion", certVersion);
			
			certList.add(certInfo);
		}
		
		String method = request.getMethod();
		String uri = request.getRequestURI();
		Map<String, String[]> param = request.getParameterMap();
		
		MultiValueMap<String, String> resHeader = reqHeader;
		String resBody = reqBody;
		
		Map<String, Object> resObj = new LinkedHashMap<>();
		
		resObj.put("method", method);
		resObj.put("uri", uri);
		resObj.put("params", param);
		resObj.put("certList", certList);
		resObj.put("resHeader", resHeader);
		resObj.put("resBody", resBody);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create();
		
		String responseString = gson.toJson(resObj);
		
		System.out.println(responseString);
		
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, HttpStatus.OK);
		
		return response;
	}
	
}
