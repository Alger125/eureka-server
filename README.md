# Eureka Server (eureka-server)

> **Servidor Central de Descubrimiento de Servicios (Service Discovery).** 
> Desarrollado con **Java 17**, **Spring Boot 4** y **Spring Cloud Netflix Eureka Server**.

---

## 1. Rol en el Ecosistema de Microservicios

En una arquitectura distribuida moderna, los microservicios se despliegan en contenedores cuyas direcciones IP y puertos son dinámicos. eureka-server actúa como el **Directorio Telefónico Central** o la **Agenda de Contactos**.

### ¿Cómo se relaciona con los demás servicios?
`
 
 EUREKA SERVER 
 Puerto 8761 
 
 
 
 Heartbeat / Registro Heartbeat / Registro
 
 
 SALES-SERVICE CATALOG-SERVICE 
 Puerto 8081 Puerto 8082 
 OpenFeign 
 (Llamada directa vía Eureka)
`

1. **Recepción de Heartbeats**:
 * Cada microservicio que inicia se registra enviando su nombre lógico (SALES-SERVICE, CATALOG-SERVICE), su IP y su puerto.
 * Cada 30 segundos, los microservicios envían un latido (*heartbeat*). Si un servicio muere o se apaga, Eureka lo elimina del registro para que nadie intente llamarlo.
2. **Resolución de Nombres para Comunicación Interna**:
 * Cuando sales-service necesita llamar a catalog-service, no usa http://localhost:8082, sino http://CATALOG-SERVICE. Eureka traduce ese nombre a la IP activa real con balanceo de carga automático.

---

## 2. Configuración Esencial (pplication.properties)

`properties
spring.application.name=eureka-server
server.port=8761

# Como este proyecto ES el servidor central, se deshabilita su registro como cliente
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
`

---

## 3. Dashboard Web de Monitoreo

* **URL del Panel**: http://localhost:8761
* **Métricas visibles**:
 * Estado de memoria y tiempo de actividad del sistema.
 * Tabla **Instances currently registered with Eureka**: Lista todos los microservicios vivos en tiempo real con sus estados (UP, DOWN, STARTING).

---

## 4. Ejecución en Terminal (Bajo Consumo de RAM)

`powershell
cd C:\Users\ErickJimz\IdeaProjects\eureka-server
.\mvnw.cmd clean package -DskipTests
java -Xmx256m -jar .\target\eureka-server-0.0.1-SNAPSHOT.jar
`
*Puerto configurado:* **8761**
