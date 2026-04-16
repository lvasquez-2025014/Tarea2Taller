# 🚀 KinalApp: Ecosistema de Gestión Transaccional

## 1. Visión del Proyecto
**KinalApp** es una aplicación web completa desarrollada bajo el ecosistema de **Java 21**. Combina una **API REST** robusta con una **interfaz web intuitiva** que permite gestionar toda la operación comercial desde el navegador.

Su objetivo principal es centralizar la gestión comercial de una entidad, automatizando el flujo que conecta a los usuarios internos con la administración de inventarios, la ejecución de ventas finales y un dashboard analítico en tiempo real.

La aplicación destaca por su **integridad referencial**, asegurando que cada venta esté vinculada a un cliente existente y sea procesada por un usuario autorizado en el sistema.

---

## 2. Especificaciones Técnicas
* **Lenguaje**: Java 21 (LTS).
* **Framework**: Spring Boot 4.0.2.
* **Vistas**: Thymeleaf (Motor de templates HTML).
* **Persistencia**: Spring Data JPA con motor **MySQL**.
* **Gestión de Dependencias**: Maven (POM XML).
* **Frontend**: HTML5, CSS3, JavaScript.
* **Estandarización de Datos**: Implementación de tipos **Long** en todos los identificadores (ID).

---

## 3. Guía de Configuración y Despliegue

### Requisitos de Entorno
* **Java Development Kit (JDK) 21**.
* **Servidor MySQL** (Puerto 3306).
* **Maven 3.9+**.

### Pasos de Instalación
1.  **Obtención del código**:
    ```bash
    git clone [https://github.com/lvasquez-2025014/Tarea2Taller.git](https://github.com/lvasquez-2025014/Tarea2Taller.git)
    ```
2.  **Configuración de Base de Datos**:
    En `src/main/resources/application.properties`, utiliza esta configuración:
    ```properties
    spring.application.name=kinalapp
    server.port=8500
    spring.datasource.url=jdbc:mysql://localhost:3306/dbClientes_in5am?createDatabaseIfNotExist=true
    spring.datasource.username=root
    spring.datasource.password=Ludwing299
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```
3.  **Lanzamiento**: Ejecutar desde la clase `KinalAppApplication.java`.

---

## 4. Diccionario de Endpoints (API Reference)

El sistema utiliza rutas normalizadas para cada recurso, diferenciando las operaciones mediante verbos HTTP (GET, POST, PUT, DELETE).

###  Módulo de Clientes (`/clientes`)
* `GET /clientes`: Lista todos los clientes registrados.
* `POST /clientes`: Registra un nuevo cliente.
* `GET /clientes/estado`: Filtra clientes con estado activo.
* `GET /clientes/{id}`: Busca un cliente por su ID (**Long**).
* `PUT /clientes/{id}`: Actualiza los datos de un cliente.
* `DELETE /clientes/{id}`: Baja lógica o física de un cliente.

###  Módulo de Usuarios (`/usuarios`)
* `GET /usuarios`: Lista todos los usuarios del sistema.
* `POST /usuarios`: Crea un nuevo usuario (Valida credenciales).
* `GET /usuarios/estado`: Filtra usuarios activos.
* `GET /usuarios/{id}`: Búsqueda, edición o eliminación de usuario.

###  Módulo de Productos (`/productos`)
* `GET /productos`: Catálogo completo de productos.
* `POST /productos`: Agrega un nuevo producto al inventario.
* `GET /productos/estado`: Lista productos con estado activo.
* `GET /productos/stock`: Muestra nombre de productos y su disponibilidad actual.
* `GET /productos/{id}`: Gestión individual de producto por ID.

###  Módulo de Ventas (`/ventas`)
* `GET /ventas`: Historial completo de ventas realizadas.
* `POST /ventas`: Registra una venta (Vincula Cliente y Usuario).
* `GET /ventas/activas`: Lista ventas con estado vigente.
* `GET /ventas/{id}`: Busca una venta específica.
* `DELETE /ventas/{id}`: Anulación de una transacción de venta.

###  Módulo de Detalles (`/detalleVentas`)
* `GET /detalleVentas`: Lista todos los detalles de facturación.
* `POST /detalleVentas`: Agrega un ítem a una venta (Vincula Producto y Venta).
* `GET /detalleVentas/estado`: Filtra detalles de venta activos.
* `GET /detalleVentas/{id}`: Busca un detalle específico.
* `PUT /detalleVentas/{id}`: Actualiza cantidad o producto de un detalle.
* `DELETE /detalleVentas/{id}`: Elimina un registro de detalle.

---

## 5. Aspectos Destacados de la Implementación
* **Validación de Capas**: Separación estricta entre **Entidades**, **Repositorios** y **Servicios**.
* **Manejo de Excepciones**: Uso de bloques `try-catch` para retornar códigos HTTP precisos (404, 400, 500).
* **Seguridad y Control**: El campo `estado` garantiza que la información histórica se mantenga incluso tras "eliminaciones" (Borrado Lógico).

---

## 6. Interfaz Web (Frontend)
KinalApp incluye una interfaz web completa con templates HTML, estilos CSS y scripts JavaScript.

### Dashboard Principal (`/dashboard`)
Panel de control con estadísticas en tiempo real:
* **Estadísticas Generales**: Total de productos, clientes, ventas y usuarios
* **Ingresos Totales**: Cálculo automático de todas las ventas realizadas
* **Alertas de Stock**: Notificaciones de productos con stock bajo (< 10 unidades)
* **Clientes Activos**: Contador de clientes con estado activo
* **Ventas Recientes**: Lista de las últimas 5 ventas realizadas
* **Sistema de Notificaciones**: Alertas dinámicas de stock, ventas completadas e ingresos

### Módulos de Gestión con UI
* **Login** (`/login`): Autenticación de usuarios con sesión
* **Clientes**: Listado, formulario, detalle y dashboard
* **Productos**: Catálogo, stock, formulario y dashboard
* **Usuarios**: Gestión de usuarios del sistema
* **Ventas**: Registro de ventas con detalles
* **Detalle de Ventas**: Gestión de ítems por venta

### Recursos Estáticos
* **CSS**: Hojas de estilo globales (`style.css`) y de autenticación (`autenticacion.css`)
* **JavaScript**: Scripts para interactividad del dashboard
* **Imágenes**: Logo de la aplicación y recursos visuales

---

## 7. Sistema de Autenticación
* **Login de Usuarios**: Formulario de autenticación con validación de credenciales
* **Gestión de Sesiones**: Manejo de sesiones HTTP para mantener usuario logueado
* **Control de Acceso**: Redirección automática al login si no hay sesión activa
* **Logout** (`/logout`): Cierre de sesión seguro

---

## 8. Control de Versiones (Git Workflow)
El proyecto sigue un flujo de trabajo estricto con **Conventional Commits**:

* **32 Commits individuales**: Un commit por cada archivo/cambio significativo
* **Tipo `feat`**: Nuevas funcionalidades (controllers, entities, services, templates)
* **Ramas**: `main` (producción), `dev` (desarrollo), `lvasquez-2025014` (trabajo)
* **Pull Requests**: Cada funcionalidad mergeada a través de PR hacia `dev`

### Estructura de Commits
| Categoría | Archivos | Commits |
|-----------|----------|---------|
| Controllers | 7 archivos (Home, Login, Cliente, Usuario, Producto, Venta, DetalleVenta) | 7 |
| Entities | 6 archivos (Notificacion, Usuario, Cliente, Producto, Venta, DetalleVenta) | 6 |
| Repositories | 5 archivos | 5 |
| Services Interfaces | 5 archivos | 5 |
| Services Implementaciones | 5 archivos | 5 |
| Resources | application.properties, static/, templates/ | 3 |
| Tests & Config | Tests, pom.xml, KinalappApplication | 1 |