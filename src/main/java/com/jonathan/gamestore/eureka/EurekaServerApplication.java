package com.jonathan.gamestore.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 🌟 CLASE PRINCIPAL: EurekaServerApplication
 *
 * ¿Qué es y qué función cumple este proyecto en la arquitectura?
 * Eureka Server actúa como el  Directorio Telefónico o Centralita de Contactos
 * de toda nuestra arquitectura de microservicios (patrón Service Discovery).
 *
 * Explicación para un programador Junior:
 * 1. En entornos reales o en la nube (AWS, Azure, Docker, Kubernetes), los microservicios
 *    se apagan, se reinician o cambian de dirección IP y de puerto dinámicamente.
 * 2. Si un microservicio llamara a otro con una URL fija como http://192.168.1.50:8082,
 *    el día que cambie la IP, todo el sistema dejaría de funcionar.
 * 3. Gracias a esta aplicación:
 *    - 'sales-service' (puerto 8081) se conecta aquí y dice: Hola soy SALES-SERVICE.
 *    - 'catalog-service' (puerto 8082) se conecta aquí y dice: Hola soy CATALOG-SERVICE.
 *    - Cuando Ventas necesita comunicarse con Catálogo, no le pregunta a una IP fija;
 *      le pregunta a Eureka: ¿En qué IP y puerto está vivo ahora mismo CATALOG-SERVICE?.
 *
 * Anotaciones clave:
 * - @SpringBootApplication: Enciende Spring Boot y levanta el servidor web en el puerto 8761.
 * - @EnableEurekaServer: ¡La anotación mágica! Transforma este proyecto de un simple Spring Boot
 *   a un servidor de registro de servicios Netflix Eureka completo, habilitando el panel web
 *   de monitoreo en http://localhost:8761.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        // Arranca el servidor central de descubrimiento
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
