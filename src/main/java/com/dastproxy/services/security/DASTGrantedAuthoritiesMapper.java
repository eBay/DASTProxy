package com.dastproxy.services.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

public class DASTGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper{

	
	public Collection<? extends GrantedAuthority> mapAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		
		final List<GrantedAuthority> mappedAuthorities = new ArrayList<GrantedAuthority>();
		
		// Check for the existence of a particular DL
		/*for(final GrantedAuthority tempGrantedAuthority: authorities){
			if(tempGrantedAuthority.getAuthority().equalsIgnoreCase("")){
				mappedAuthorities.add(new GrantedAuthority(){
					private static final long serialVersionUID = 7379595534918951314L;
		            public String getAuthority() {
		                return "ROLE_USER";
		            } 
		        });
				break;
			}
		}*/
		
		mappedAuthorities.add(new GrantedAuthority(){
			private static final long serialVersionUID = 7379595534918951314L;
            public String getAuthority() {
                return "ROLE_USER";
            } 
        });
		
		
		return mappedAuthorities;
	}

}
