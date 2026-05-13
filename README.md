# Sistema de Gestión de Pólizas - Grupo Bolívar

Sistema backend desarrollado en Spring Boot para gestionar pólizas de seguros, riesgos asociados y sus relaciones. Permite crear, renovar, cancelar pólizas y administrar los riesgos que éstas cubren, con validaciones de negocio robustas y una arquitectura escalable.

---

## 🛠 Tecnologías Usadas

- **Java 17** - Lenguaje principal
- **Spring Boot 4.0.6** - Framework principal
- **Spring Data JPA** - Acceso a datos y ORM
- **Hibernate** - Mapeo objeto-relacional
- **MySQL 8.x** - Base de datos relacional
- **Lombok** - Reducción de código boilerplate
- **Spring Security** - Seguridad y autenticación
- **Spring Validation** - Validación de datos
- **Spring Actuator** - Monitoreo y métricas
- **SpringDoc OpenAPI 3.0.2** - Documentación API (Swagger)
- **Maven** - Gestor de dependencias y build

---

## 📐 Arquitectura del Proyecto

El proyecto sigue el patrón **arquitectura en capas** de tres niveles:

```
com.grupo_bolivar.polizas/
├── controller/        # REST Controllers - Endpoints HTTP
├── service/          # Interfaces de servicios - Lógica de negocio
│   └── impl/         # Implementaciones de servicios
├── repository/       # Interfaces JPA - Acceso a datos
├── entity/           # Modelos JPA - Entidades del dominio
└── PolizasApplication.java  # Punto de entrada
```

**Flujo de una solicitud:**
```
HTTP Request → Controller → Service → Repository → Database
     ↓
   Response ← Service (retorna datos) ← Repository (consulta DB)
```

Cada capa tiene responsabilidades claramente definidas:
- **Controller**: Recibe solicitudes HTTP y las delega al servicio
- **Service**: Implementa toda la lógica de negocio y validaciones
- **Repository**: Comunica con la base de datos mediante JPA
- **Entity**: Define la estructura de datos y relaciones

---

## 📊 Modelo de Datos

### Entidades Principales

#### 1. **Poliza**
Representa una póliza de seguros contratada. Es el agregado raíz del dominio.

```java
- idPoliza (PK)           // Identificador único auto-generado
- idestado                // Estado actual (1=Activa, 2=Cancelada, 3=Renovada)
- tipoPoliza              // INDIVIDUAL o COLECTIVA
- fechaInicio             // Fecha de vigencia inicial
- fechaFin                // Fecha de vigencia final
- canon_mensual           // Costo mensual de la póliza
- porcentaje_ipc          // Porcentaje de ajuste por IPC
- valor_prima             // Valor total de prima
- arrendatario            // Parte contratante (tomador)
- arrendador              // Asegurador
- polizaRiesgos           // Riesgos asociados (relación 1:N)
```

#### 2. **Riesgo**
Representa un bien, propiedad o exposición asegurada. Cada riesgo debe tener una dirección y un valor asegurado.

```java
- idRiesgo (PK)           // Identificador único auto-generado
- idestado (FK)           // Estado del riesgo (referencia a Estado)
- descripcion             // Descripción del bien asegurado
- valorAsegurado          // Valor monetario del bien
- direccionInmueble       // Ubicación física del inmueble
- polizaRiesgos           // Relaciones con pólizas (1:N inverso)
```

#### 3. **Estado**
Catálogo que define los posibles estados de pólizas y riesgos.

```java
- idestado (PK)           // Identificador único
- codigo                  // Código corto (ej: "ACT", "CAN")
- nombre                  // Nombre descriptivo (ej: "Activa", "Cancelada")
- descripcion             // Descripción detallada
- origen                  // Origen del estado (ej: "SISTEMA")
- indicador               // Booleano para activar/desactivar
```

Estados utilizados:
- **1 = Activa** - Póliza/riesgo en vigencia
- **2 = Cancelada** - Póliza/riesgo cancelado
- **3 = Renovada** - Póliza/riesgo renovado

#### 4. **PolizaRiesgo**
Tabla de asociación (Many-to-Many) que vincula pólizas con riesgos. Es la tabla intermedia que permite que una póliza cubra múltiples riesgos.

```java
- idPolizaRiesgo (PK)          // Identificador único
- idPoliza (FK)                // Referencia a Poliza
- idRiesgo (FK)                // Referencia a Riesgo
- idestado (FK)                // Estado de la relación
- fechaAsignacionRiesgo        // Cuándo se asignó el riesgo a la póliza
```

### Relaciones Entre Entidades

```
┌─────────────────────────────────────────────┐
│                  ESTADO                     │
│  - idestado (PK)                            │
│  - codigo, nombre, descripcion              │
│  - origen, indicador                        │
└──────────────┬────────────────────┬─────────┘
               │ (1:N)              │ (1:N)
               │                    │
        ┌──────▼──────┐       ┌─────▼──────────┐
        │   RIESGO     │       │  POLIZARIESGO  │
        ├──────────────┤       ├────────────────┤
        │ - idRiesgo   │◄──┐   │ - idPolizaRi.. │
        │ - idestado   │   └───┤ - idRiesgo     │
        │ - descripción│       │ - idestado     │
        │ - ...        │       │ - fechaAsign.. │
        └──────────────┘       └────┬───────────┘
                                    │ (N:1)
                                    │
                              ┌─────▼───────────┐
                              │    POLIZA       │
                              ├─────────────────┤
                              │ - idPoliza (PK) │
                              │ - idestado      │
                              │ - tipoPoliza    │
                              │ - fechaInicio   │
                              │ - fechaFin      │
                              │ - canon_mensual │
                              │ - valor_prima   │
                              │ - ...           │
                              └─────────────────┘
```

**Relaciones implementadas:**
- **Poliza → PolizaRiesgo** (1:N) - Una póliza puede tener múltiples riesgos
- **Riesgo → PolizaRiesgo** (1:N) - Un riesgo puede estar en múltiples pólizas
- **Riesgo → Estado** (N:1) - Cada riesgo tiene un estado
- **PolizaRiesgo → Estado** (N:1) - Cada relación tiene un estado

---

## ⚙️ Reglas de Negocio Implementadas

### 1. **Una póliza individual solo puede tener 1 riesgo**
   - Cuando se intenta agregar un segundo riesgo a una póliza de tipo INDIVIDUAL, el sistema lanza excepción
   - Las pólizas COLECTIVA pueden tener múltiples riesgos
   ```
   Si tipoPoliza == "INDIVIDUAL" AND polizaRiesgos.size() > 0
   → Error: "Una póliza individual solo puede tener 1 riesgo"
   ```

### 2. **Validación de tipo de póliza**
   - Solo se permiten dos tipos: INDIVIDUAL y COLECTIVA
   - Cualquier otro tipo de póliza genera error
   ```
   Si tipoPoliza NOT IN ("INDIVIDUAL", "COLECTIVA")
   → Error: "Tipo de póliza no permitido"
   ```

### 3. **No se puede renovar una póliza cancelada**
   - Una póliza en estado Cancelado (idestado = 2) no puede ser renovada
   - El sistema retorna un mensaje de error indicando esta restricción
   ```
   Si póliza.idestado == 2
   → Error: "No se puede renovar una póliza cancelada"
   ```

### 4. **Cancelar una póliza cancela todos sus riesgos (cascada)**
   - Cuando se cancela una póliza (idestado = 2), automáticamente todos los registros 
     en PolizaRiesgo asociados cambian su estado a cancelado
   - Este es un cambio de estado en cascada para mantener la integridad de datos
   ```
   Póliza.cancelar() → Actualiza póliza.idestado = 2
                    → Busca todos PolizaRiesgo.where(poliza)
                    → Actualiza cada relación.idestado = 2
   ```

### 5. **Renovar una póliza actualiza el estado de todos sus riesgos**
   - Cuando se renueva una póliza (idestado = 3), todos los riesgos 
     asociados también pasan al estado Renovado
   ```
   Póliza.renovar() → Actualiza póliza.idestado = 3
                   → Busca todos PolizaRiesgo.where(poliza)
                   → Actualiza cada relación.idestado = 3
   ```

### 6. **Validación de existencia**
   - Se valida que tanto la póliza como el riesgo existan en BD 
     antes de realizar operaciones
   - Si no existe, se lanza RuntimeException: "Poliza/Riesgo no existe"

### 7. **Seguridad del Core Mock**
   - El endpoint `/core-mock/evento` requiere una API KEY en el header `x-api-key`
   - API KEY actual: `123456` (configurado en código)
   - Sin API KEY válida, retorna error: "API KEY inválida"

---

## 🔌 Endpoints REST Disponibles

### **Gestión de Pólizas**

#### **1. Listar todas las pólizas (con filtros opcionales)**
```
GET /polizas
```
Obtiene el listado de todas las pólizas. Permite filtrar por tipo y/o estado.

**Parámetros Query (opcionales):**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `tipo` | String | Tipo de póliza: INDIVIDUAL o COLECTIVA |
| `estado` | String | Estado de la póliza: 1 (Activa), 2 (Cancelada), 3 (Renovada) |

**Ejemplos Request:**
```
GET /polizas
GET /polizas?tipo=INDIVIDUAL
GET /polizas?estado=1
GET /polizas?tipo=COLECTIVA&estado=1
```

**Response (200 OK):**
```json
[
  {
    "idPoliza": 1,
    "idestado": 1,
    "tipoPoliza": "INDIVIDUAL",
    "fechaInicio": "2024-01-15",
    "fechaFin": "2025-01-15",
    "canon_mensual": 150000,
    "porcentaje_ipc": 3.5,
    "valor_prima": 1800000,
    "arrendatario": "Juan Pérez",
    "arrendador": "Grupo Bolívar",
    "polizaRiesgos": [
      {
        "idPolizaRiesgo": 1,
        "riesgo": {
          "idRiesgo": 10,
          "descripcion": "Casa en Bogotá",
          "valorAsegurado": 250000000,
          "direccionInmueble": "Cra 7 # 45-32, Bogotá"
        },
        "fechaAsignacionRiesgo": "2024-01-15"
      }
    ]
  }
]
```

---

#### **2. Obtener riesgos de una póliza específica**
```
GET /polizas/{id}/riesgos
```
Retorna todos los riesgos asociados a una póliza.

**Parámetros Path:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | Long | ID de la póliza |

**Ejemplo Request:**
```
GET /polizas/1/riesgos
```

**Response (200 OK):**
```json
[
  {
    "idRiesgo": 10,
    "estado": {
      "idestado": 1,
      "codigo": "ACT",
      "nombre": "Activa"
    },
    "descripcion": "Casa en Bogotá",
    "valorAsegurado": 250000000,
    "direccionInmueble": "Cra 7 # 45-32, Bogotá"
  },
  {
    "idRiesgo": 11,
    "estado": {
      "idestado": 1,
      "codigo": "ACT",
      "nombre": "Activa"
    },
    "descripcion": "Apartamento en Medellín",
    "valorAsegurado": 180000000,
    "direccionInmueble": "Av. 33 # 8A-45, Medellín"
  }
]
```

**Error (404):**
```json
Si la póliza no existe, retorna un error o lista vacía
```

---

#### **3. Agregar un riesgo a una póliza**
```
POST /polizas/{id}/riesgos
```
Asocia un riesgo existente a una póliza. Valida las reglas de negocio:
- Una póliza INDIVIDUAL solo puede tener 1 riesgo
- El riesgo debe existir en BD

**Parámetros Path:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | Long | ID de la póliza |

**Body Request:**
```json
{
  "idRiesgo": 10
}
```

**Response (200 OK):**
```json
{
  "idRiesgo": 10,
  "estado": {
    "idestado": 1,
    "codigo": "ACT",
    "nombre": "Activa"
  },
  "descripcion": "Casa en Bogotá",
  "valorAsegurado": 250000000,
  "direccionInmueble": "Cra 7 # 45-32, Bogotá"
}
```

**Error (400):**
```json
// Si es póliza INDIVIDUAL con riesgos
"Una póliza individual solo puede tener 1 riesgo"

// Si el tipo de póliza no es válido
"Tipo de póliza no permitido"

// Si el riesgo no existe
"Riesgo no existe"

// Si la póliza no existe
"Poliza no existe"
```

---

#### **4. Renovar una póliza**
```
POST /polizas/{id}/renovar
```
Cambia el estado de la póliza a "Renovada" (idestado = 3) y actualiza 
el estado de todos sus riesgos asociados. No se puede renovar si está cancelada.

**Parámetros Path:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | Long | ID de la póliza a renovar |

**Ejemplo Request:**
```
POST /polizas/1/renovar
```

**Response (200 OK):**
```json
{
  "idPoliza": 1,
  "idestado": 3,
  "tipoPoliza": "INDIVIDUAL",
  "fechaInicio": "2024-01-15",
  "fechaFin": "2025-01-15",
  "canon_mensual": 150000,
  "porcentaje_ipc": 3.5,
  "valor_prima": 1800000,
  "arrendatario": "Juan Pérez",
  "arrendador": "Grupo Bolívar",
  "polizaRiesgos": [
    {
      "idPolizaRiesgo": 1,
      "riesgo": {...},
      "estado": {
        "idestado": 3,
        "nombre": "Renovada"
      },
      "fechaAsignacionRiesgo": "2024-01-15"
    }
  ]
}
```

**Error (400):**
```json
// Si la póliza está cancelada
"No se puede renovar una póliza cancelada"

// Si la póliza no existe
"Poliza no existe"
```

---

#### **5. Cancelar una póliza**
```
POST /polizas/{id}/cancelar
```
Cambia el estado de la póliza a "Cancelada" (idestado = 2) y automáticamente 
cancela todos los riesgos y relaciones asociadas en cascada.

**Parámetros Path:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | Long | ID de la póliza a cancelar |

**Ejemplo Request:**
```
POST /polizas/1/cancelar
```

**Response (200 OK):**
```json
{
  "idPoliza": 1,
  "idestado": 2,
  "tipoPoliza": "INDIVIDUAL",
  "fechaInicio": "2024-01-15",
  "fechaFin": "2025-01-15",
  "canon_mensual": 150000,
  "porcentaje_ipc": 3.5,
  "valor_prima": 1800000,
  "arrendatario": "Juan Pérez",
  "arrendador": "Grupo Bolívar",
  "polizaRiesgos": [
    {
      "idPolizaRiesgo": 1,
      "riesgo": {...},
      "estado": {
        "idestado": 2,
        "nombre": "Cancelada"
      },
      "fechaAsignacionRiesgo": "2024-01-15"
    }
  ]
}
```

**Error (400):**
```json
// Si la póliza no existe
"Poliza no existe"

// Si el estado cancelado no existe
"Estado no existe"
```

---

### **Gestión de Riesgos**

#### **6. Cancelar un riesgo**
```
POST /riesgos/{id}/cancelar
```
Cambia el estado de la asociación PolizaRiesgo (relación) a cancelado.

**Parámetros Path:**
| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | Long | ID de la relación PolizaRiesgo a cancelar |

**Ejemplo Request:**
```
POST /riesgos/1/cancelar
```

**Response (200 OK):**
```json
{
  "idRiesgo": 10,
  "estado": {
    "idestado": 0,
    "nombre": "Cancelada"
  },
  "descripcion": "Casa en Bogotá",
  "valorAsegurado": 250000000,
  "direccionInmueble": "Cra 7 # 45-32, Bogotá"
}
```

---

### **Core Mock (Integración Externa)**

#### **7. Enviar evento al Core Mock**
```
POST /core-mock/evento
```
Endpoint mock que simula el envío de eventos al sistema Core. Requiere autenticación 
por API KEY. Utilizado para comunicación inter-sistemas.

**Headers (requerido):**
| Header | Tipo | Descripción |
|--------|------|-------------|
| `x-api-key` | String | API KEY para autenticación (Valor: `123456`) |

**Body Request:**
```json
{
  "evento": "POLIZA_CANCELADA",
  "polizaId": 1
}
```

**Response (200 OK):**
```
EVENTO RECIBIDO EN CORE MOCK
```

**Error (401):**
```
API KEY inválida
```

**Eventos soportados (ejemplos):**
- POLIZA_CANCELADA
- POLIZA_RENOVADA
- RIESGO_AGREGADO
- RIESGO_CANCELADO

---

## 📝 Configuración Relevante

### Base de Datos (`application.properties`)

```properties
# Aplicación
spring.application.name=sales
server.port=8080

# DataSource MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/polizas?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}           # Por defecto: root
spring.datasource.password=${DB_PASSWORD:mysql}          # Por defecto: mysql
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Logging
logging.level.root=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=INFO

# Cache (Caffeine)
spring.cache.type=caffeine
spring.cache.cache-names=exampleCache
spring.cache.caffeine.spec=maximumSize=100,expireAfterAccess=10m

# Sesiones
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true

# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui
```

### Seguridad (`SecurityConfig`)

- **CSRF deshabilitado**: Para facilitar testing y desarrollo
- **Todas las solicitudes permitidas**: Sin autenticación requerida (excepto Core Mock)
- **API KEY requerida** en `/core-mock/evento`: Header `x-api-key: 123456`

### Validaciones

- **Spring Validation** integrado para validar entidades
- **Validaciones de negocio** en servicios (reglas descritas arriba)
- **Control de excepciones** mediante RuntimeException

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- **Java 17** instalado
- **Maven 3.6+** instalado
- **MySQL 8.0+** ejecutándose localmente

### 1. Clonar y configurar base de datos

```bash
# Crear base de datos
CREATE DATABASE polizas;

# O ejecutar script SQL (si existe)
mysql -u root -p polizas < schema.sql
```

### 2. Configurar variables de entorno (opcional)

```bash
# Windows (PowerShell)
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "mysql"

# Linux/Mac
export DB_USERNAME=root
export DB_PASSWORD=mysql
```

### 3. Compilar y ejecutar

```bash
# Compilar
mvn clean package

# Ejecutar
mvn spring-boot:run
```

O directamente con JAR:
```bash
java -jar target/polizas-0.0.1-SNAPSHOT.jar
```

### 4. Verificar que está corriendo

```bash
# La aplicación estará en:
http://localhost:8080

# Swagger UI:
http://localhost:8080/swagger-ui

# API Docs:
http://localhost:8080/api-docs
```

---

## 📚 Ejemplo de Uso Completo

### Flujo de negocio típico:

```bash
# 1. Crear estado base de datos (si no existe)
INSERT INTO ESTADO (codigo, nombre, descripcion, origen, indicador) 
VALUES ('ACT', 'Activa', 'Póliza activa', 'SISTEMA', true),
       ('CAN', 'Cancelada', 'Póliza cancelada', 'SISTEMA', true),
       ('REN', 'Renovada', 'Póliza renovada', 'SISTEMA', true);

# 2. Listar pólizas activas
GET /polizas?estado=1

# 3. Obtener riesgos de una póliza
GET /polizas/1/riesgos

# 4. Agregar un nuevo riesgo a la póliza
POST /polizas/1/riesgos
{
  "idRiesgo": 10
}

# 5. Renovar la póliza
POST /polizas/1/renovar

# 6. Notificar al Core
POST /core-mock/evento
Headers: x-api-key: 123456
{
  "evento": "POLIZA_RENOVADA",
  "polizaId": 1
}

# 7. Cancelar la póliza (cancela todos sus riesgos)
POST /polizas/1/cancelar
```

---

## 🔒 Consideraciones de Seguridad

1. **API KEY en Core Mock**: Camiar `123456` por un valor seguro en producción
2. **HTTPS obligatorio**: En producción, habilitar SSL/TLS
3. **Autenticación OAuth/JWT**: Considerar implementar para endpoints principales
4. **Validación de entrada**: Todos los campos deben validarse
5. **Audit logging**: Registrar cambios en pólizas y riesgos
6. **Rate limiting**: Implementar límite de solicitudes por IP

---

## 📦 Estructura de Carpetas

```
src/main/
├── java/com/grupo_bolivar/polizas/
│   ├── PolizasApplication.java          # Punto de entrada
│   ├── controller/
│   │   ├── PolizaController.java        # REST endpoints de pólizas
│   │   ├── RiesgoController.java        # REST endpoints de riesgos
│   │   ├── CoreMockController.java      # Mock de integraciones
│   │   └── SecurityConfig.java          # Configuración de seguridad
│   ├── service/
│   │   ├── PolizaService.java           # Interfaz de servicios
│   │   ├── RiesgoService.java
│   │   └── impl/
│   │       ├── PolizaServiceImpl.java    # Lógica de negocio - Pólizas
│   │       └── RiesgoServiceImpl.java    # Lógica de negocio - Riesgos
│   ├── repository/
│   │   ├── PolizaRepository.java        # Acceso a datos - Pólizas
│   │   ├── RiesgoRepository.java        # Acceso a datos - Riesgos
│   │   ├── PolizaRiesgoRepository.java  # Acceso a datos - Relaciones
│   │   └── EstadoRepository.java        # Acceso a datos - Estados
│   └── entity/
│       ├── Poliza.java                  # Entidad JPA - Póliza
│       ├── Riesgo.java                  # Entidad JPA - Riesgo
│       ├── PolizaRiesgo.java            # Entidad JPA - Relación
│       ├── Estado.java                  # Entidad JPA - Estado
│       ├── EventoRequest.java           # DTO de eventos
│       └── ApiResponse.java             # DTO genérico de respuesta
└── resources/
    └── application.properties           # Configuración de BD y servidor
```

---

## 🎯 Próximas Mejoras Sugeridas

- ✅ Implementar DTOs completos para request/response
- ✅ Agregar validaciones con `@Valid` y anotaciones
- ✅ Crear excepciones personalizadas
- ✅ Implementar manejo de errores global con `@ControllerAdvice`
- ✅ Agregar paginación y sorting en listados
- ✅ Crear mappers (MapStruct) para transformaciones
- ✅ Agregar trazabilidad (audit) a cambios
- ✅ Implementar tests unitarios y de integración
- ✅ Considerar usar querydsl o specifications para filtros complejos

---

## 📞 Contacto y Soporte

Para preguntas o sugerencias sobre el código, contactar a Daniel Santiago Herrera Martinez
📞3124703878.

**Versión**: 0.0.1-SNAPSHOT  
**Última actualización**: Mayo 2026
