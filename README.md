# CrediYa System - Microservicios Dockerizados

Sistema de microservicios para préstamos con Authentication y Orders, containerizado con Docker.

## 📋 Servicios

- **Authentication (Pragma)** - Puerto 8090
- **Orders (CrediYa)** - Puerto 8091
- **PostgreSQL Auth** - Puerto 5433
- **PostgreSQL Orders** - Puerto 5434

---

## 🚀 Instalación y Configuración

### 1. Instalar Docker y Docker Compose

```bash
# Instalar Docker
sudo apt update
sudo apt install docker.io

# Instalar Docker Compose v2
sudo curl -L "https://github.com/docker/compose/releases/download/v2.25.0/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER
sudo systemctl restart docker
newgrp docker
```

### 2. Verificar instalación

```bash
docker --version
docker-compose --version
```

---

## 🔧 Compilar Proyectos

```bash
# Compilar Authentication
cd pragma
./gradlew bootJar

# Compilar Orders
cd ../crediya  
./gradlew bootJar

# Volver a la raíz
cd ..
```

---

## 🐳 Ejecutar Sistema

### Levantar todos los servicios

```bash
docker-compose up -d
```

### Verificar estado

```bash
docker-compose ps
```

### Ver logs

```bash
# Logs de todos los servicios
docker-compose logs -f

# Logs de un servicio específico
docker-compose logs -f pragma
docker-compose logs -f crediya
```

---

## 🌐 URLs y Endpoints

### Health Checks
- **Auth Health**: http://localhost:8090/actuator/health
- **Orders Health**: http://localhost:8091/actuator/health

### API Documentation
- **Auth Swagger**: http://localhost:8090/swagger-ui.html
- **Orders Swagger**: http://localhost:8091/swagger-ui.html

### API Endpoints
- **Login**: `POST http://localhost:8090/api/v1/auth/login`
- **Users**: `GET http://localhost:8090/api/v1/users/all`
- **Orders**: `POST http://localhost:8091/api/v1/order`
- **Status**: `GET http://localhost:8091/api/v1/status/all`

---

## 🗃️ Acceso a Base de Datos

### Conectar via Terminal

```bash
# PostgreSQL Authentication
docker exec -it crediya-postgres-auth psql -U postgres -d authorization

# PostgreSQL Orders
docker exec -it crediya-postgres-orders psql -U postgres -d crediya
```

### Comandos útiles PostgreSQL

```sql
\dt                           -- Listar tablas
\d tabla                      -- Ver estructura de tabla
\x                           -- Formato expandido (más legible)
SELECT * FROM users;         -- Ver datos de usuarios
SELECT * FROM orders;        -- Ver datos de órdenes
\q                           -- Salir
```

### Conexión externa (DBeaver, pgAdmin)

**Authentication DB:**
- Host: `localhost`
- Port: `5433`
- Database: `authorization`
- User: `postgres`
- Password: `postgres`

**Orders DB:**
- Host: `localhost`
- Port: `5434`
- Database: `crediya`
- User: `postgres`
- Password: `postgres`

---

## 🔄 Gestión de Servicios

### Reiniciar servicios

```bash
docker-compose restart
```

### Detener servicios

```bash
# Detener (mantiene datos)
docker-compose down

# Detener y eliminar volumes (⚠️ BORRA DATOS)
docker-compose down -v
```

### Reconstruir imágenes

```bash
# Después de cambios en código
docker-compose up -d --build
```

---

## 🧪 Testing

### Verificar conectividad entre servicios

```bash
# Probar que Orders puede llamar a Auth
curl -X GET http://localhost:8091/actuator/health
curl -X GET http://localhost:8090/actuator/health
```

### Ejemplo de flujo completo

```bash
# 1. Login para obtener token
curl -X POST http://localhost:8090/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123"}'

# 2. Usar token en Orders (reemplazar YOUR_TOKEN)
curl -X POST http://localhost:8091/api/v1/order \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount":50000,"termMonths":12}'
```

---

## 🐛 Solución de Problemas

### Ver logs detallados

```bash
docker-compose logs postgres-auth | grep ERROR
docker-compose logs pragma | grep -i liquibase
```

### Recrear servicios

```bash
docker-compose down
docker-compose up -d --force-recreate
```

### Verificar volumes

```bash
docker volume ls
docker volume inspect crediya-system_auth_db_data
```

---

## 📁 Estructura del Proyecto

```
crediya-system/
├── docker-compose.yml          # Orquestación de servicios
├── .env                        # Variables de entorno
├── README.md                   # Esta documentación
├── pragma/                     # Microservicio Authentication
│   ├── Dockerfile
│   └── applications/.../application-docker.yaml
└── crediya/                    # Microservicio Orders
    ├── Dockerfile
    └── applications/.../application-docker.yaml
```