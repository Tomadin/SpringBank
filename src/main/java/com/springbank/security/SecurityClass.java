
package com.springbank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //Anotacion que nos dice que vamos a proporcionar un bean de configuracion, en esta caso la seguridad
public class SecurityClass {
    
    @Bean //Registramos el metodo como un bean, utilizará esta configuración para la seguridad.
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{ //Inyecta el objeto de tipo HttpSecurity para poder configurar toda la seguridad a nivel del protocolo http. Luego el HttpSecurity devolvera una cadena de filtros de seguridad aplicables a cada petición web
        http
                .csrf(csrf -> csrf.disable()) //Al pasarle esto como lamba estamos deshabilitando la proteccion contra el tipo de ataque csrf. Se suele desactivar al no usar tokens en APIs REST
                .authorizeHttpRequests(auth->auth.anyRequest() //hicimos un authorizeHttpRequests pasandole un lamba que invoca el metodo anyRequest() para reemplazar al metodo obsoleto autorizedRequest
                        .authenticated()).httpBasic(httpBasic -> {}); //habilita la autotenticacion con credenciales
        return http.build(); //retornamos los nuevos filtros/reglas.
        
    }

}
