# SIMÓN DICE - PMDM 🦑🫧🪼

![gif](https://i.pinimg.com/originals/a1/f8/be/a1f8be54a08a324c83e747a8fa5ed660.gif)

## DESCRIPCIÓN DEL PROYECTO 👀

- ESTE PROYECTO ES UNA IMPLEMENTACIÓN DEL JUEGO CLÁSICO "SIMÓN DICE" PARA ANDROID, DESARROLLADO EN KOTLIN CON ARQUITECTURA MVVM. EL JUEGO CONSISTE EN MEMORIZAR Y REPETIR SECUENCIAS DE COLORES QUE SE VAN HACIENDO CADA VEZ MÁS LARGAS Y COMPLEJAS.

> **Antes de empezar a codificar el programa he realizado un diagrama de flujo y estado para comprender de manera profunda la lógica que va a seguir🤖...**

---

- **DIAGRAMA DE ESTADO 🎀**

```mermaid

---
config:
  theme: redux
  look: neo
---
stateDiagram-v2
    [*] --> Idle : Pantalla de inicio
    state Idle : Aplicación abierta / Pantalla de inicio
    
    Idle --> Jugando : Usuario pulsa START
    state Jugando : Estado principal del juego

    Jugando --> MostrandoSecuencia : Generar y añadir un color a la secuencia
    state MostrandoSecuencia : Iluminar secuencia generada

    MostrandoSecuencia --> EsperandoEntrada : Secuencia iluminada completamente
    state EsperandoEntrada : Esperando pulsaciones del usuario

    EsperandoEntrada --> VerificandoEntrada : Usuario pulsa un botón
    state VerificandoEntrada : Comprobando pulsación

    VerificandoEntrada --> EsperandoEntrada : Pulsación correcta (secuencia NO completada)
    VerificandoEntrada --> RondaSuperada : Pulsación correcta (secuencia completada)
    VerificandoEntrada --> GameOver : Pulsación incorrecta

    state RondaSuperada : El usuario completó la secuencia correctamente
    RondaSuperada --> Jugando : Aumentar ronda y generar nueva secuencia

    state GameOver : Mostrar mensaje de pérdida
    GameOver --> Idle : Reiniciar juego

```

---

- **DIAGRAMA DE FLUJO 🎳**

```mermaid
flowchart TD
  A["Inicio - Pantalla principal<br/>(Simon Dice)"] --> B["Botón START pulsado"]
  B --> C["Inicializar juego:<br/>ronda = 1, puntuación = 0, secuencia = []"]
  C --> D["Generar/añadir color aleatorio a la secuencia"]
  D --> E["Mostrar secuencia al usuario<br/>(iluminar botones secuencialmente)"]
  E --> F["Habilitar entrada del usuario"]
  F --> G["Usuario pulsa un botón"]

  G --> H{"¿Pulsa el botón<br/>correcto según secuencia?"}
  H -->|"Sí"| I["Avanzar índice de comprobación"]
  I --> J{"¿Ha completado<br/>el usuario la secuencia?"}
  J -->|"No"| F
  J -->|"Sí"| K["Aumentar puntuación<br/>mostrar 'Rondas' y 'Puntuación'"]
  K --> L["Incrementar ronda"]
  L --> D

  H -->|"No"| M["¡Has perdido!"]
  M --> N["Mostrar mensaje de pérdida<br/>rondas completadas y puntuación"]
  N --> O["Volver a la pantalla de inicio<br/>(Reiniciar juego)"]
  O --> A

  A ~~~ D

```

---

## CARACTERÍSTICAS PRINCIPALES DEL PROYECTO 🪽

- **INTERFAZ MODERNA DESARROLLADA CON JETPACK COMPOSE**
- **ARQUITECTURA MVVM PARA SEPARACIÓN CLARA DE RESPONSABILIDADES**
- **GESTIÓN DE ESTADO REACTIVA CON FLOWS**
- **TESTING COMPLETO CON CORRUTINAS**
- **ANIMACIONES Y FEEDBACK VISUAL**

---

## ESTRUCTURA DEL PROYECTO 🏗️

<img width="368" height="441" alt="image" src="https://github.com/user-attachments/assets/b2c9cf47-0fd1-49ac-8eec-cefce9c29516" />

### MODEL (MODELO DE DATOS Y DOMINIO) 🦕
- **ColorSimon: ENUM QUE REPRESENTA LOS COLORES DEL JUEGO**
- **MotorJuegoSimon: CLASE QUE CONTIENE TODA LA LÓGICA DEL JUEGO**
- **GESTIÓN DE SECUENCIAS, PUNTUACIÓN Y ESTADOS DEL JUEGO**

---

### VIEW (INTERFAZ DE USUARIO) 🤹
- **SimonDiceScreen: PANTALLA PRINCIPAL CON JETPACK COMPOSE**
- **INTERFAZ REACTIVA QUE OBSERVA LOS CAMBIOS DE ESTADO**
- **BOTONES DE COLORES Y ANIMACIONES**

---

### VIEWMODEL (GESTIÓN DE ESTADO) 🦫
- **ModeloVistaSimon: GESTIONA EL ESTADO DE LA UI Y COORDINA CON EL DOMINIO**
- **USO DE StateFlow PARA ESTADO REACTIVO**
- **MANEJO DE CORRUTINAS PARA ANIMACIONES Y LÓGICA TEMPORAL**




