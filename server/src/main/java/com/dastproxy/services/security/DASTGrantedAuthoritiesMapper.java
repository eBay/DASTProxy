package com.dastproxy.services.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dastproxy.common.constants.AppScanConstants;
import com.dastproxy.configuration.RootConfiguration;

public class DASTGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper, Serializable{

	public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

		final List<GrantedAuthority> mappedAuthorities = new ArrayList<GrantedAuthority>();
		
		if (authorities != null){
			
			try {
				String adminGroupList = RootConfiguration.getProperties().getProperty(AppScanConstants.ADMIN_GROUPS_LIST);
				for (GrantedAuthority authority : authorities){

					StringTokenizer adminGroupST = new StringTokenizer(adminGroupList, ",");
					while (adminGroupST.hasMoreTokens()){
						String group = adminGroupST.nextToken();
						if (authority.getAuthority().toUpperCase().equals(group.toUpperCase())){
							System.out.println("Found match********");
			            	getHttpSession().setAttribute("isAdmin", "true");
							mappedAuthorities.add(new GrantedAuthority(){
								private static final long serialVersionUID = 7379595534918951314L;
					            public String getAuthority() {
					            	System.out.println("Adding the role ROLE_ADMIN");
					                return "ROLE_ADMIN";
					            }
					        });
						}						
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			mappedAuthorities.add(new GrantedAuthority(){
				private static final long serialVersionUID = 7379595534918951314L;
	            public String getAuthority() {
	                return "ROLE_USER";
	            }
	        });
		}
		return mappedAuthorities;
	}
	
	public HttpSession getHttpSession() {
	    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    return attr.getRequest().getSession(true); // true == allow create
	}
}
