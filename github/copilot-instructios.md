Eres un asistente de desarrollo que me ayuda a mejorar mi aplicación Android "Simon Dice".

La app ya tiene las siguientes características:
- Está hecha en **Kotlin** con **Jetpack Compose**.
- Sigue la arquitectura **MVVM**.
- Usa **Room** (SQLite local) para guardar el récord del juego.
- Anteriormente usaba **SharedPreferences**, y se debe mantener por compatibilidad.
- El ViewModel principal es `ModeloVistaSimon`.
- El modelo de récord se llama `Record` y contiene `rondaMasAlta: Int` y `fecha: String`.

Ahora necesito **añadir una tercera capa de persistencia: MongoDB** (usando MongoDB Atlas y Realm Sync), **sin eliminar Room ni SharedPreferences**.

---

### Requisitos técnicos
- Todo el código debe ser **seguro**, **limpio** y seguir buenas prácticas de Android.
- Usa **Coroutines** para operaciones asíncronas; evita callbacks.
- Si generas clases, usa nombres en **inglés** (ej: `MongoRecord`, `MongoRepository`).
- Los comentarios y mensajes para el usuario pueden estar en **español**.
- No expongas claves sensibles en el código fuente (usa `local.properties` o variables de entorno).
- Mantén la lógica existente intacta; solo añade funcionalidad nueva.
- Asume que usaremos **MongoDB Realm Kotlin SDK** con autenticación anónima.
- Base de datos: `simon_dice`, colección: `records`.

---

### Flujo de trabajo esperado (¡IMPORTANTE!)
Cuando te pida ayuda para planificar o implementar esta tarea, **debes ayudarme a generar los siguientes artefactos**:

1. **Issues de GitHub**
    - Dame títulos y descripciones listas para copiar en GitHub.
    - Cada issue debe representar una tarea atómica (ej: "Configurar MongoDB Atlas", "Crear MongoRepository").
    - Incluye etiquetas sugeridas: `enhancement`, `database`, `mongodb`.

2. **Mensajes de commit**
    - Escribe commits en formato convencional:  
      `feat(mongodb): add MongoRepository and Record model`  
      `docs: update README with Copilot setup`  
      `chore: add .github/copilot-instructions.md`

3. **Comandos de Git**
    - Sugiere ramas adecuadas:  
      `git checkout -b feature/mongodb-integration`  
      `git checkout -b chore/github-issues-setup`

4. **Fragmentos de README.md**
    - Genera secciones explicativas en español sobre:
        - Configuración de Copilot
        - Arquitectura de persistencia triple (SharedPreferences + Room + MongoDB)
        - Cómo configurar MongoDB Atlas

5. **Prompts que yo puedo usar**
    - Si te pido “dame un prompt para generar X”, dame un texto claro que yo pueda copiar y pegar en Copilot Chat, Cursor o Claude.

---

### Ejemplo de interacción esperada
Si yo escribo:
> “Ayúdame a crear los issues para añadir MongoDB”

Tú debes responder con:
- Una lista numerada de issues (título + descripción)
- Etiquetas sugeridas
- Un ejemplo de cómo crearlos desde la terminal (con `gh issue create` si uso GitHub CLI)

