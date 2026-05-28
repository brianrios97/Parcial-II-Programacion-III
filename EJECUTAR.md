# Cómo ejecutar el proyecto correctamente

## ⚠️ NO ejecutes con el botón ▶ de IntelliJ directamente

El proyecto usa Gradle + Lombok + JPA. Para que todo funcione correctamente,
**siempre ejecutá desde la terminal** con:

```powershell
.\gradlew.bat run --console=plain
```

## Desde IntelliJ — usando el panel de Gradle

1. En IntelliJ, abrí el panel **Gradle** (ícono del elefante en la barra derecha)
2. Expandí: `TPVIII → Tasks → application`
3. Doble click en **run**

Esto ejecuta Gradle internamente y el menú funcionará correctamente.

---

## Por qué falla el botón ▶ de IntelliJ

Cuando IntelliJ ejecuta `Main.java` directamente (sin Gradle), ocurren dos problemas:

1. **Lombok** necesita procesamiento de anotaciones activo. IntelliJ lo requiere configurado
   explícitamente en `Settings → Build → Compiler → Annotation Processors → Enable`.
2. **JPA/Hibernate** busca `META-INF/persistence.xml` en el classpath. Si IntelliJ
   no configuró las `resources` como source root correctamente, no lo encuentra.

La forma más simple y garantizada es **usar siempre Gradle run**.
