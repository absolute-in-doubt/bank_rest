package com.example.bankcards.config;

import com.example.bankcards.security.JwtVerificationFilter;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.hibernate.id.insert.Binder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Environment env;
    @Value("${secret.jwt}")
    private byte[] secretKey;

    public SecurityConfig(Environment env) {
        this.env = env;
    }

//TODO set a path to /authenticate endpoint around JwtVerificationFilter (set excluding path)


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtVerificationFilter jwtVerificationFilter) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                .securityContext(Customizer.withDefaults())
                .headers(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .servletApi(Customizer.withDefaults())
                .addFilterBefore(jwtVerificationFilter, AnonymousAuthenticationFilter.class)
                .anonymous(Customizer.withDefaults())
                .exceptionHandling(Customizer.withDefaults());
        return http.build();
    }

//    @Bean
//    public SecretKey secretKey(@Value("${secret.jwt}") byte[] secretKey){
//        return new SecretKeySpec(secretKey, "TripleDES");
//    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec originalKey = new SecretKeySpec(secretKey,0,secretKey.length,"HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(originalKey).macAlgorithm(MacAlgorithm.HS256).build();
    }


//    @Bean
//    public JwtDecoder decoder(SecretKey secretKey){
//        return NimbusJwtDecoder.withSecretKey(secretKey).build();
//    }
//
//    @Bean
//    public JwtEncoder encoder(SecretKey secretKey){
//
//        String jwkKeyID = "JWT-1";
//
//        JWK jwk = new OctetSequenceKey.Builder(secretKey)
//               .keyID(jwkKeyID).build();
//
//
//
//        NimbusJwtEncoder encoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
//        encoder.setJwkSelector(jwks -> jwks.get(0));
////        encoder.setJwkSelector(jwks -> jwks
////                .stream()
////                .filter(
////                        j -> j.getKeyID().equals(jwkKeyID)
////                ).findFirst()
////                .get());
//        return encoder;
//    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder decoder){
        return new JwtAuthenticationProvider(decoder);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService){
        return new DaoAuthenticationProvider(userDetailsService);
    }

    @Bean
    public AuthenticationManager authManager(JwtAuthenticationProvider jwtAuthProvider, DaoAuthenticationProvider daoAuthProvider){
        return new ProviderManager(jwtAuthProvider, daoAuthProvider);
    }


    @Bean
    public DefaultBearerTokenResolver defaultBearerTokenResolver(){
        return new DefaultBearerTokenResolver();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    public JwtVerificationFilter jwtVerificationFilter(AuthenticationManager authManager, DefaultBearerTokenResolver defaultBearerTokenResolver){
//        String[] paths = env.getRequiredProperty("spring.security.paths.public", );
//        System.out.println("Paths: ================================================================================================================");
//        Arrays.stream(paths).forEach(System.out::println);
//        return new JwtVerificationFilter(authManager, defaultBearerTokenResolver, paths);
//    }
}
