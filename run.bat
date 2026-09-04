@echo off
cd D:\Projetos\db\desafio-votacao-fullstack
java -jar target\desafio-votacao-1.0.0.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/votacao --spring.datasource.username=votacao --spring.datasource.password=votacao123 > app.log 2>&1