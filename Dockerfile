# Usa una imagen base ligera con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Crea un directorio en el contenedor para la app
WORKDIR /app

# Copia el JAR generado a la imagen (ajusta el nombre del .jar)
COPY target/negocio-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto que Render usará (Render establece la variable de entorno PORT automáticamente)
EXPOSE 8080

# Comando para correr la app
CMD ["java", "-jar", "app.jar"]
