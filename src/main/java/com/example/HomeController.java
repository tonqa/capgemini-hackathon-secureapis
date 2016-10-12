package com.example;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String greeting(Model model) {
		//Redirect Login to API Manager
		model.addAttribute("loginURL", "https://localhost:8243/authorize?response_type=code&client_id=_boieRhpfT4GPa8Buoz3T8jUD34a&redirect_uri=http://localhost:8081/login");
		return "home";
	}

	@RequestMapping("/login")
    public String login(@RequestParam(value="code", required=true) String code, RestTemplate rest, HttpServletResponse httpResponse) {

		//prepare request to API manager
		String body = "grant_type=authorization_code&code="+code+"&redirect_uri=http://localhost:8081/login";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Authorization", "Basic "+Base64Utils.encodeToString("_boieRhpfT4GPa8Buoz3T8jUD34a:ecrDq2fvUusf2Gp8Pttx9pL3Ncoa".getBytes()));
		HttpEntity<String> entity = new HttpEntity<String>(body,headers);

		//send request and get token
		AccessTokenResponse response = rest.postForObject("https://localhost:8243/token", entity, AccessTokenResponse.class);

		//set cookie with token
		Cookie cookie = new Cookie("token", response.getAccess_token());
		httpResponse.addCookie(cookie);

        return "login";
    }

	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public String products(@CookieValue(value = "token") String currentToken, Model model, RestTemplate rest) {

		//REST request (via API manager)
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer "+currentToken);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		//send request
		ResponseEntity<Product[]> responseEntity = rest.exchange("http://10.40.164.64:8280/restaurantapp/products", HttpMethod.GET, entity, Product[].class);

		//save products to scope
		model.addAttribute("productList", responseEntity.getBody());
		return "products";
	}

}



