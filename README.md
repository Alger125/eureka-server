# Eureka Server (eureka-server)

> **Servidor Central de Descubrimiento y Registro de Servicios (Service Discovery Registry).**  
> Desarrollado con **Java 17**, **Spring Boot 4** y **Spring Cloud Netflix Eureka Server**.

---

## 1. Introduccion y Justificacion Arquitectonica

En una arquitectura monolitica tradicional, todos los componentes residen en un mismo proceso en memoria, por lo que las invocaciones son llamadas a metodos directos.

En una **arquitectura de microservicios**, el sistema se fragmenta en multiples servicios independientes distribuidos a traves de la red. Esto introduce un desafio critico: **El problema de la localizacion dinamica de servicios**.

### El Problema: ¿Por que no usar URLs fijas (Hardcoded)?
Si dentro de `sales-service` configuraramos llamadas fijas como:
```
http://192.168.1.50:8082/api/games/1
```
Nos enfrentariamos a fallas severas en entornos de produccion:
1. **IPs Dinamicas**: En entornos con Docker, Kubernetes o nubes como AWS, los contenedores se destruyen y recrean constantemente con direcciones IP distintas.
2. **Escalado Horizontal**: Si levantamos 3 instancias de `catalog-service` para soportar alto trafico, una IP fija solo enviaria peticiones a una unica instancia, ignorando a las demas.
3. **Punto Unico de Falla**: Si la maquina con esa IP se apaga, el servicio consumidor se rompe por completo.

### La Solucion: Patron Service Registry (Directorio Central)
`eureka-server` resuelve este problema actuando como la **agenda telefonica central** del sistema:
* Los microservicios no conocen las IPs de sus companeros.
* Solo conocen el **nombre logico** del servicio al que desean llamar (por ejemplo: `CATALOG-SERVICE`).
* Eureka se encarga de resolver ese nombre a la IP y puerto activos en tiempo real.

---

## 2. Diagrama de Interaccion en la Arquitectura

```
                                  +-----------------------------+
                                  |        EUREKA SERVER        |
                                  |      Puerto: 8761           |
                                  |   (Service Registry Hub)    |
                                  +--------------+--------------+
                                                 |
                          +----------------------+----------------------+
                          | 1. Heartbeat / Registro                     | 1. Heartbeat / Registro
                          |    "SALES-SERVICE en 8081"                  |    "CATALOG-SERVICE en 8082"
                          v                                             v
            +---------------------------+                 +---------------------------+
            |       SALES-SERVICE       |                 |      CATALOG-SERVICE      |
            |        Puerto 8081        |                 |        Puerto 8082        |
            |     Base de Datos H2      |                 |    Base de Datos Mongo    |
            |   (Transacciones SQL)     |                 |     (Catalogo NoSQL)      |
            +-------------+-------------+                 +-------------+-------------+
                          |                                             ^
                          | 2. Consulta direccion y envia peticion      |
                          |    GET http://CATALOG-SERVICE/api/games/{id}|
                          +---------------------------------------------+
```

---

## 3. Ciclo de Vida y Mecanismos de Operacion

### A. Registro Inicial (Service Registration)
Cuando un microservicio (como `sales-service` o `catalog-service`) se inicia:
1. Lee la propiedad `eureka.client.service-url.defaultZone` de su archivo `application.properties`.
2. Emite una peticion HTTP POST a `http://localhost:8761/eureka/apps/{APP_NAME}`.
3. Envia sus metadatos: Nombre de aplicacion, IP del host, puerto activo y estado inicial (`UP`).

### B. Mantenimiento del Registro y Latidos (Heartbeats)
* Cada cliente registrado envia una peticion de renovacion (*heartbeat*) cada **30 segundos** por defecto.
* Si Eureka no recibe una renovacion durante **90 segundos**, asume que la instancia sufrio una caida abrupta y la remueve del directorio disponible para evitar enrutarle trafico fallido.

### C. Modo de Autoconservacion (Self-Preservation Mode)
Si se produce un corte temporal de red entre los servidores y Eureka deja de recibir latidos de muchos servicios de golpe, Eureka activa su mecanismo de proteccion:
* **No elimina inmediatamente las instancias**: Entiende que el problema puede ser de red y no de caida del servicio.
* Muestra una advertencia visual en el panel web:  
  *`EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT.`*

---

## 4. Configuracion del Servidor (`application.properties`)

```properties
# Nombre logico del servidor en el contexto de Spring
spring.application.name=eureka-server

# Puerto estandar de la industria para Netflix Eureka Server
server.port=8761

# Deshabilita el auto-registro: Eureka es el servidor, no necesita registrarse a si mismo
eureka.client.register-with-eureka=false

# Deshabilita la obtencion del registro: Al ser el nodo maestro, ya posee la informacion localmente
eureka.client.fetch-registry=false

# Configuracion de bitacora para diagnostico en consola
logging.level.com.netflix.eureka=INFO
logging.level.org.springframework.cloud=INFO
```

---

## 5. Anatomia de la Clase Principal (`EurekaServerApplication.java`)

```java
package com.jonathan.gamestore.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

### Explicacion detallada de Anotaciones:
* `@SpringBootApplication`:
  * Habilita el escaneo automatico de componentes (`@ComponentScan`).
  * Inicializa el contenedor de inyeccion de dependencias de Spring.
  * Arranca el servidor web embebido (Tomcat) para recibir trafico HTTP.
* `@EnableEurekaServer`:
  * Anotacion de Spring Cloud Netflix que transforma la aplicacion en un registro de servicios.
  * Despliega internamente los servlets y controladores REST que atienden a los clientes.
  * Habilita el panel de administracion web interactivo.

---

## 6. Panel de Monitoreo Web (Dashboard)

Una vez iniciado el servidor, el panel visual esta disponible en:  
**URL:** [http://localhost:8761](http://localhost:8761)

### Secciones principales del Dashboard:
1. **System Status**: Muestra metricas de memoria RAM utilizada, numero de nucleos y entorno.
2. **DS Replicas**: Nodos Eureka duplicados para alta disponibilidad (en desarrollo local aparece vacio).
3. **Instances currently registered with Eureka**:
   * **Application**: Nombre logico en mayusculas (`SALES-SERVICE`, `CATALOG-SERVICE`).
   * **AMIs**: Identificadores de instancia.
   * **Availability Zones**: Zonas de disponibilidad.
   * **Status**: Estado actual de salud (ej: `UP (1) - 192.168.1.10:sales-service:8081`).

---

## 7. Instrucciones de Compilacion y Ejecucion

### Opcion A: Modo Desarrollo (Maven Wrapper)
```powershell
cd C:\Users\ErickJimz\IdeaProjects\eureka-server
.\mvnw.cmd spring-boot:run
```

### Opcion B: Ejecucion Optimizada con Limite de Memoria RAM (8 GB Setup)
Para mantener un consumo de memoria ligero en la estacion de trabajo:
```powershell
cd C:\Users\ErickJimz\IdeaProjects\eureka-server
.\mvnw.cmd clean package -DskipTests
java -Xmx256m -jar .\target\eureka-server-0.0.1-SNAPSHOT.jar
```
* **Puerto de escucha:** `8761`
* **Consumo de memoria estimado:** ~180MB - 250MB RAM.

---

## 8. Verificacion Rapida de Operatividad

Para comprobar que el servidor esta respondiendo correctamente desde la terminal:

```powershell
# En PowerShell:
Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Headers @{"Accept"="application/json"} | ConvertTo-Json -Depth 4
```
Si el servidor esta activo, retornara la lista de aplicaciones registradas en formato JSON.
