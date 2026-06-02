    # 📋 ESPECIFICACIÓN DEL BACKEND - ALTAVISTA SYSTEM

    ## 🏗️ Arquitectura General

    **Framework:** Spring Boot 3.2.0  
    **Servidor:** Port 8081 (predeterminado, configurable con `PORT` env var)  
    **Paradigma:** Reactivo (WebFlux con Reactor)  
    **Base de Datos:** MongoDB (NoSQL - Documento)  
    **Java:** Versión 17  

    ---

    ## 🗄️ BASE DE DATOS

    ### Detalle Conexión MongoDB
    ```
    URI: ${SPRING_DATA_MONGODB_URI}
    Tipo: MongoDB (Documento JSON)
    Puerto: Configurable via variable de entorno
    ```

    ### COLECCIONES Y ESTRUCTURA

    #### 1️⃣ **CUSTOMERS** (Colección de Clientes)
    ```
    Colección: customers
    Tipo: Documento
    ```

    **Campos:**
    | Campo | Tipo | Requerido | Descripción |
    |-------|------|-----------|-------------|
    | `_id` (customer_id) | String (ObjectId) | ✅ | ID único generado por MongoDB |
    | `first_name` | String | ✅ | Nombre del cliente |
    | `last_name` | String | ✅ | Apellido del cliente |
    | `phone` | String | ❌ | Teléfono de contacto |
    | `email` | String | ❌ | Email único (valida no duplicados) |
    | `created_at` | DateTime | ✅ | Fecha de creación (auto-generada) |
    | `updated_at` | DateTime | ✅ | Fecha última actualización (auto-generada) |
    | `deleted_at` | DateTime | ❌ | Fecha de eliminación lógica |
    | `restored_at` | DateTime | ❌ | Fecha de restauración |
    | `preferences` | String | ❌ | Preferencias del cliente (JSON) |
    | `is_active` | Boolean | ✅ | Estado activo (default: true) |
    | `client_type` | String | ✅ | Tipo de cliente (V/R/N) |

    **Ejemplo JSON:**
    ```json
    {
    "_id": "507f1f77bcf86cd799439011",
    "first_name": "Juan",
    "last_name": "Pérez",
    "phone": "+51987654321",
    "email": "juan@example.com",
    "created_at": "2024-04-13T10:30:00",
    "updated_at": "2024-04-13T10:30:00",
    "preferences": "{}",
    "is_active": true,
    "client_type": "V"
    }
    ```

    ---

    #### 2️⃣ **PRODUCTS** (Colección de Productos)
    ```
    Colección: products
    Tipo: Documento
    ```

    **Campos:**
    | Campo | Tipo | Requerido | Descripción |
    |-------|------|-----------|-------------|
    | `_id` (id) | String (ObjectId) | ✅ | ID único del producto |
    | `name` | String | ✅ | Nombre del producto |
    | `description` | String | ❌ | Descripción detallada |
    | `price` | Decimal | ✅ | Precio del producto (> 0) |
    | `category` | String | ❌ | Categoría (3 caracteres) |
    | `is_available` | Boolean | ✅ | Disponibilidad (default: true) |
    | `image_url` | String | ❌ | URL de imagen |
    | `launch_date` | DateTime | ❌ | Fecha de lanzamiento |
    | `prep_time` | String | ❌ | Tiempo de preparación |
    | `is_featured` | Boolean | ✅ | Es destacado (default: false) |
    | `nutritional_info` | String | ❌ | Información nutricional (JSON) |
    | `created_at` | DateTime | ✅ | Fecha de creación |
    | `updated_at` | DateTime | ✅ | Fecha última actualización |
    | `deleted_at` | DateTime | ❌ | Fecha de eliminación lógica |
    | `restored_at` | DateTime | ❌ | Fecha de restauración |

    **Ejemplo JSON:**
    ```json
    {
    "_id": "507f1f77bcf86cd799439012",
    "name": "Lomito Saltado",
    "description": "Delicioso plato de lomo con papas y cebolla",
    "price": 25.50,
    "category": "PLA",
    "is_available": true,
    "image_url": "https://example.com/lomito.jpg",
    "prep_time": "00:15:00",
    "is_featured": true,
    "nutritional_info": "{}",
    "created_at": "2024-04-13T10:30:00",
    "updated_at": "2024-04-13T10:30:00"
    }
    ```

    ---

    #### 3️⃣ **TABLE_SPOTS** (Colección de Mesas)
    ```
    Colección: table_spots
    Tipo: Documento
    ```

    **Campos:**
    | Campo | Tipo | Requerido | Descripción |
    |-------|------|-----------|-------------|
    | `_id` (table_id) | String (ObjectId) | ✅ | ID único de la mesa |
    | `table_number` | Integer | ✅ | Número de mesa (único) |
    | `location` | String | ✅ | Ubicación en el restaurante |
    | `capacity` | Integer | ✅ | Capacidad de personas (> 0) |
    | `is_available` | Boolean | ✅ | Disponibilidad (default: true) |
    | `notes` | String | ❌ | Notas adicionales |
    | `status` | String | ✅ | Estado (default: "Disponible") |
    | `last_clean` | Date | ❌ | Última fecha de limpieza |
    | `cleaning_time` | Time | ❌ | Hora de limpieza |
    | `layout_details` | String | ❌ | Detalles de distribución |

    **Ejemplo JSON:**
    ```json
    {
    "_id": "507f1f77bcf86cd799439013",
    "table_number": 5,
    "location": "Terraza",
    "capacity": 4,
    "is_available": true,
    "status": "Disponible",
    "last_clean": "2024-04-13",
    "notes": "Cerca a la ventana"
    }
    ```

    ---

    #### 4️⃣ **RESERVATIONS** (Colección de Reservas)
    ```
    Colección: reservations
    Tipo: Documento (Transaccional)
    ```

    **Campos:**
    | Campo | Tipo | Requerido | Descripción |
    |-------|------|-----------|-------------|
    | `_id` (reservation_id) | String (ObjectId) | ✅ | ID único de la reserva |
    | `reservation_date` | Date | ✅ | Fecha de la reserva |
    | `reservation_time` | Time | ✅ | Hora de la reserva |
    | `guests_count` | Integer | ✅ | Cantidad de comensales (> 0) |
    | `status` | String | ✅ | Estado (default: "Pendiente") |
    | `customer_id` | Reference | ✅ | FK a Customers (DBRef) |
    | `table_id` | Reference | ✅ | FK a TableSpots (DBRef) |
    | `is_active` | Boolean | ✅ | Estado activo (default: true) |
    | `created_at` | DateTime | ✅ | Fecha de creación |
    | `updated_at` | DateTime | ✅ | Fecha última actualización |
    | `deleted_at` | DateTime | ❌ | Fecha de eliminación lógica |
    | `restored_at` | DateTime | ❌ | Fecha de restauración |

    **Ejemplo JSON:**
    ```json
    {
    "_id": "507f1f77bcf86cd799439014",
    "reservation_date": "2024-04-15",
    "reservation_time": "19:30:00",
    "guests_count": 4,
    "status": "Pendiente",
    "customer_id": { "$ref": "customers", "$id": "507f1f77bcf86cd799439011" },
    "table_id": { "$ref": "table_spots", "$id": "507f1f77bcf86cd799439013" },
    "is_active": true,
    "created_at": "2024-04-13T10:30:00",
    "updated_at": "2024-04-13T10:30:00"
    }
    ```

    ---

    #### 5️⃣ **ORDERS** (Colección de Órdenes)
    ```
    Colección: orders
    Tipo: Documento (Transaccional)
    ```

    **Campos:**
    | Campo | Tipo | Requerido | Descripción |
    |-------|------|-----------|-------------|
    | `_id` (order_id) | String (ObjectId) | ✅ | ID único de la orden |
    | `order_date` | DateTime | ✅ | Fecha y hora de la orden (auto-generada) |
    | `total_amount` | Decimal | ✅ | Monto total (default: 0.00, >= 0) |
    | `customer_id` | Reference | ❌ | FK a Customers (DBRef) |
    | `details` | Array | ❌ | Arreglo de OrderDetails (embebido) |

    **Ejemplo JSON:**
    ```json
    {
    "_id": "507f1f77bcf86cd799439015",
    "order_date": "2024-04-13T12:45:00",
    "total_amount": 51.00,
    "customer_id": { "$ref": "customers", "$id": "507f1f77bcf86cd799439011" },
    "details": [
        {
        "detail_id": "507f1f77bcf86cd799439016",
        "quantity": 2,
        "price_at_purchase": 25.50,
        "product_id": { "$ref": "products", "$id": "507f1f77bcf86cd799439012" }
        }
    ]
    }
    ```

    ---

    #### 6️⃣ **ORDER_DETAILS** (Colección de Detalles de Órdenes)
    ```
    Colección: order_details
    Tipo: Documento (Transaccional)
    ```

    **Campos:**
    | Campo | Tipo | Requerido | Descripción |
    |-------|------|-----------|-------------|
    | `_id` (detail_id) | String (ObjectId) | ✅ | ID único del detalle |
    | `quantity` | Integer | ✅ | Cantidad ordenada |
    | `price_at_purchase` | Decimal | ✅ | Precio al momento de compra |
    | `order_id` | Reference | ✅ | FK a Orders (DBRef) |
    | `product_id` | Reference | ✅ | FK a Products (DBRef) |

    **Ejemplo JSON:**
    ```json
    {
    "_id": "507f1f77bcf86cd799439016",
    "quantity": 2,
    "price_at_purchase": 25.50,
    "order_id": { "$ref": "orders", "$id": "507f1f77bcf86cd799439015" },
    "product_id": { "$ref": "products", "$id": "507f1f77bcf86cd799439012" }
    }
    ```

    ---

    #### 7️⃣ **ADMIN_USERS** (Colección de Usuarios Administrador)
    ```
    Colección: admin_users
    Tipo: Documento
    ```

    **Campos:**
    | Campo | Tipo | Requerido | Descripción |
    |-------|------|-----------|-------------|
    | `_id` (user_id) | String (ObjectId) | ✅ | ID único del usuario |
    | `username` | String | ✅ | Nombre de usuario (único) |
    | `password` | String | ✅ | Contraseña (encriptada en BD) |
    | `is_active` | Boolean | ✅ | Estado activo (default: true) |
    | `created_at` | DateTime | ✅ | Fecha de creación |

    **Ejemplo JSON:**
    ```json
    {
    "_id": "507f1f77bcf86cd799439017",
    "username": "admin",
    "password": "$2b$12$...",
    "is_active": true,
    "created_at": "2024-04-13T10:30:00"
    }
    ```

    ---

    ## 🔌 ENDPOINTS API REST

    ### Base URL
    ```
    http://localhost:8081/api
    ```

    ---

    ### 👥 CUSTOMERS - `/api/customers`

    | Método | Endpoint | Descripción | Parámetros | Retorna |
    |--------|----------|-------------|-----------|---------|
    | **GET** | `/` | Listar con filtros | `search`, `type`, `active` (opcionales) | `Flux<Customer>` |
    | **GET** | `/activos` | Listar clientes activos | - | `Flux<Customer>` |
    | **GET** | `/inactivos` | Listar clientes inactivos | - | `Flux<Customer>` |
    | **GET** | `/{id}` | Buscar por ID | `id` (path) | `Mono<Customer>` |
    | **POST** | `/` | Crear cliente | Body: JSON Customer | `Mono<Customer>` |
    | **PUT** | `/{id}` | Editar cliente | `id` (path), Body: JSON | `Mono<Customer>` |

    **Ejemplo POST/PUT:**
    ```json
    {
    "first_name": "Juan",
    "last_name": "Pérez",
    "phone": "+51987654321",
    "email": "juan@example.com",
    "client_type": "V"
    }
    ```

    ---

    ### 🍽️ PRODUCTS - `/api/products`

    | Método | Endpoint | Descripción | Parámetros | Retorna |
    |--------|----------|-------------|-----------|---------|
    | **GET** | `/` | Listar todos productos | - | `Flux<Product>` |
    | **GET** | `/activos` | Listar productos disponibles | - | `Flux<Product>` |
    | **GET** | `/inactivos` | Listar productos no disponibles | - | `Flux<Product>` |
    | **GET** | `/{id}` | Buscar por ID | `id` (path) | `Mono<Product>` |
    | **POST** | `/` | Crear producto | Body: JSON Product | `Mono<Product>` |
    | **PUT** | `/{id}` | Editar producto | `id` (path), Body: JSON | `Mono<Product>` |

    **Ejemplo POST/PUT:**
    ```json
    {
    "name": "Lomito Saltado",
    "description": "Delicioso plato de lomo",
    "price": 25.50,
    "category": "PLA",
    "is_available": true
    }
    ```

    ---

    ### 🪑 TABLE SPOTS - `/api/table-spots`

    | Método | Endpoint | Descripción | Parámetros | Retorna |
    |--------|----------|-------------|-----------|---------|
    | **GET** | `/` | Listar todas las mesas | - | `Flux<TableSpot>` |
    | **GET** | `/disponibles` | Listar mesas disponibles | - | `Flux<TableSpot>` |
    | **GET** | `/ocupadas` | Listar mesas ocupadas | - | `Flux<TableSpot>` |
    | **GET** | `/{id}` | Buscar por ID | `id` (path) | `Mono<TableSpot>` |
    | **POST** | `/` | Crear mesa | Body: JSON TableSpot | `Mono<TableSpot>` |
    | **PUT** | `/{id}` | Editar mesa | `id` (path), Body: JSON | `Mono<TableSpot>` |

    **Ejemplo POST/PUT:**
    ```json
    {
    "table_number": 5,
    "location": "Terraza",
    "capacity": 4,
    "status": "Disponible"
    }
    ```

    ---

    ### 📅 RESERVATIONS - `/api/reservations`

    | Método | Endpoint | Descripción | Parámetros | Retorna |
    |--------|----------|-------------|-----------|---------|
    | **GET** | `/` | Listar todas las reservas | - | `Flux<Reservation>` |
    | **GET** | `/activas` | Listar reservas activas | - | `Flux<Reservation>` |
    | **GET** | `/{id}` | Buscar por ID | `id` (path) | `Mono<Reservation>` |
    | **POST** | `/` | Crear reserva | Body: JSON Reservation | `Mono<Reservation>` |
    | **PUT** | `/{id}` | Editar reserva | `id` (path), Body: JSON | `Mono<Reservation>` |

    **Ejemplo POST/PUT:**
    ```json
    {
    "reservation_date": "2024-04-15",
    "reservation_time": "19:30:00",
    "guests_count": 4,
    "customer_id": "507f1f77bcf86cd799439011",
    "table_id": "507f1f77bcf86cd799439013",
    "status": "Pendiente"
    }
    ```

    ---

    ### 🛒 ORDERS - `/api/orders`

    | Método | Endpoint | Descripción | Parámetros | Retorna |
    |--------|----------|-------------|-----------|---------|
    | **GET** | `/` | Listar todas las órdenes | - | `Flux<Order>` |
    | **POST** | `/` | Crear orden con detalles | Body: JSON Order | `Mono<Order>` |

    **Ejemplo POST:**
    ```json
    {
    "customer_id": "507f1f77bcf86cd799439011",
    "total_amount": 51.00,
    "details": [
        {
        "quantity": 2,
        "price_at_purchase": 25.50,
        "product_id": "507f1f77bcf86cd799439012"
        }
    ]
    }
    ```

    ---

    ### 🔐 ADMIN USERS - `/api/admin-users`

    | Método | Endpoint | Descripción | Parámetros | Retorna |
    |--------|----------|-------------|-----------|---------|
    | **GET** | `/` | Listar usuarios admin | - | `Flux<AdminUser>` |
    | **GET** | `/{id}` | Buscar por ID | `id` (path) | `Mono<AdminUser>` |
    | **POST** | `/` | Crear usuario | Body: JSON AdminUser | `Mono<AdminUser>` |

    **Ejemplo POST:**
    ```json
    {
    "username": "admin",
    "password": "securepassword123"
    }
    ```

    ---

    ## 🏠 DOCUMENTACIÓN SWAGGER

    **URL:** `http://localhost:8081/swagger-ui.html`  
    **JSON Schema:** `http://localhost:8081/api-docs`

    ---

    ## 📦 RELACIONES Y REFERENCIAS

    ### Relaciones de Datos:

    ```
    Customers (1) ──────→ (N) Reservations
    Customers (1) ──────→ (N) Orders
    Products  (1) ──────→ (N) OrderDetails
    TableSpots (1) ──────→ (N) Reservations
    Orders    (1) ──────→ (N) OrderDetails
    ```

    ---

    ## 📝 NOTAS IMPORTANTES

    ### Características Reactivas:
    - ✅ Todos los endpoints retornan `Mono<T>` o `Flux<T>` (no bloqueantes)
    - ✅ Basado en Project Reactor
    - ✅ Procesamiento asincrónico

    ### Auditoría de Datos:
    - ✅ Todas las entidades principales tienen: `created_at`, `updated_at`, `deleted_at`, `restored_at`
    - ✅ Soporta eliminación lógica (soft delete)

    ### Validaciones:
    - ✅ Emails únicos en Customers
    - ✅ Números de mesa únicos en TableSpots
    - ✅ Precios > 0 en Products
    - ✅ Cantidad de comensales > 0 en Reservations

    ### Convenciones JSON:
    - ✅ CamelCase en Java → snake_case en JSON (gracias a @JsonProperty)
    - ✅ Decimales para precios (BigDecimal)
    - ✅ Fechas ISO 8601 (LocalDateTime, LocalDate, LocalTime)

    ---

    ## 🚀 PARA CONECTARSE DESDE EL FRONTEND

    1. **URL Base:** `http://localhost:8081`
    2. **Usar header `Content-Type: application/json`**
    3. **Los IDs son Strings (MongoDB ObjectId)**
    4. **Las respuestas son reactivas (Stream de datos)**
    5. **CORS configurado en WebConfig.java**

    ---

    **Última actualización:** Abril 13, 2024  
    **Versión API:** 1.0
