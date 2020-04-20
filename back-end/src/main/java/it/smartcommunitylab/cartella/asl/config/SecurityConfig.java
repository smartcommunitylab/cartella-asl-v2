package it.smartcommunitylab.cartella.asl.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

@Configuration
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {
	
	@Value("${security.oauth2.resource.id}")
	private String resourceId;
	@Value("${security.oauth2.resource.jwk.keySetUri}")
	private String jwkSetUri;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/manage/**").permitAll()
		.antMatchers("/api/**", "/admin/**").authenticated()
		.and().cors().and().csrf().disable();
	}
	
	/**
	 * Configures the resource ID to the application's client ID. Without this,
	 * authentication would fail.
	 */
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(resourceId);
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwkTokenStore(jwkSetUri, jwtAccessTokenConverter());
	}

	@Bean
	public AccessTokenConverter accessTokenConverter() {
		return new ClaimAwareAccessTokenConverter();
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//		converter.setAccessTokenConverter(accessTokenConverter());
		return converter;
	}

	public static class ClaimAwareAccessTokenConverter extends DefaultAccessTokenConverter {
		@Override
		public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
			HashMap<String, Object> copy = new HashMap<>(claims);
			if (!claims.containsKey(AUTHORITIES))
				copy.put(AUTHORITIES, claims.get("roles"));

			OAuth2Authentication authentication = super.extractAuthentication(copy);
			((UsernamePasswordAuthenticationToken) authentication.getUserAuthentication()).setDetails(claims);
			return authentication;
		}
	}	
}
