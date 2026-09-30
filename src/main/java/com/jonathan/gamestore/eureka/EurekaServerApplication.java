package com.jonathan.gamestore.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * CLASE PRINCIPAL: EurekaServerApplication
 *
 * Rol arquitectonico:
 * Servidor Central de Descubrimiento de Servicios (Service Discovery Registry).
 * Implementa el patron de diseno Service Registry para arquitecturas distribuidas.
 *
 * Conceptos clave para desarrollo profesional:
 * 1. Desacoplamiento de infraestructura: Evita el uso de direcciones IP y puertos estaticos
 *    hardcodeados en los clientes.
 * 2. Registro dinamico: Al iniciar, cada microservicio se registra con su identificador logico
 *    (ej: SALES-SERVICE, CATALOG-SERVICE).
 * 3. Monitoreo de salud (Heartbeats): Recibe senales periodicas cada 30 segundos para validar
 *    la disponibilidad de las instancias activas.
 * 4. Balanceo de carga del lado del cliente: Proporciona la lista de instancias vivas a clientes
 *    como OpenFeign y Spring Cloud LoadBalancer.
 *
 * Anotaciones:
 * - @SpringBootApplication: Inicializa el contexto de Spring Boot y el servidor web embebido.
 * - @EnableEurekaServer: Habilita los controladores de registro de Netflix Eureka y expone
 *   el panel de administracion web en el puerto 8761.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
