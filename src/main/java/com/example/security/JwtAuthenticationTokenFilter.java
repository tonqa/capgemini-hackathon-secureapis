package com.example.security;

import com.example.security.exception.JwtTokenMissingException;
import com.example.security.model.JwtAuthenticationToken;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.internal.filter.ValueNode.JsonNode;

import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Base64Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

/**
 * Filter that orchestrates authentication by using supplied JWT token
 *
 * @author pascal alma
 */
public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthenticationTokenFilter() {
        super("/**");
    }

    /**
     * Attempt to authenticate request - basically just pass over to another method to authenticate request headers
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {

    	Enumeration headerNames = request.getHeaderNames();
    	while(headerNames.hasMoreElements()) {
    		String headerName = (String)headerNames.nextElement();
    		System.out.println("" + headerName);
    		System.out.println("" + request.getHeader(headerName));
    	}

        String header = request.getHeader(this.tokenHeader);

        if (header == null) {
            throw new JwtTokenMissingException("No JWT token found in request headers");
        }

        if (!header.startsWith("Bearer ")) {
        	header = "Bearer " + header;
        }

        String authToken = header.substring(7);
        String[] tokenArr = authToken.split("\\.");
        String body = new String(Base64Utils.decodeFromString(tokenArr[1]), "UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode bodyObj = mapper.readValue(body, JsonNode.class);
        // TODO: parse token to obj and get user obj and put into seperate class

        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(authToken);

        return getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Make sure the rest of the filterchain is satisfied
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        chain.doFilter(request, response);
    }
}