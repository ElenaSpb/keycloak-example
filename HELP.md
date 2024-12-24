# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.example.keycloak-app' is invalid and this project uses 'com.example.keycloak_app' instead.

# Getting Started

1. run keycloak service on port 8085: http://localhost:8085
2. create realm 'external'
3. create client 'external-client'
4. set Valid redirect URIs  http://localhost:8081/*
5. Web origins http://localhost:8081
6. Authentication flow set 'Direct access grants'

