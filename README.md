# List Manager

Sistema de gerenciamento de listas desenvolvido para portfólio.

## Tecnologias

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven

### Infraestrutura

- Docker
- Docker Compose

### Frontend

- React
- TypeScript
- Vite

## Arquitetura

O backend utiliza uma organização baseada em features:

```text
feature/
├── auth/
├── item/
├── list/
└── user/

shared/
├── config/
├── exception/
├── response/
└── security/