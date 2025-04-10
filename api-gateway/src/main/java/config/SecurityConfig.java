//package config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//@EnableWebFluxSecurity
//// the apigateway is base on webflux instead of spring mvc so that is why we need webflux security
//public class SecurityConfig {
//    // create a Bean called securitywebfilterchain
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity)
//    {
//        //configure webflux security details
//        // allow the path eureak/** to pass without authentication and ask authentication for others
//        // so permitall for eureak and anyexhcange and authenticated for the rest call to be authenticated
//        serverHttpSecurity.csrf()
//                .disable()
//                .authorizeExchange(exchange->exchange
//                        .pathMatchers("/eureak/**")
//                        .permitAll()
//                        .anyExchange()
//                        .authenticated())
//                        .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt); // enable oauthresourceserver and enable its jwt capability
//                return   serverHttpSecurity.build();  // so we use build so that to create an object of type securitywebfilterchain
//
//    }
//}