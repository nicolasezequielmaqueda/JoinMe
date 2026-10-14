# JoinMe – Guía para arrancar el proyecto

Guía paso a paso para levantar JoinMe en tu ordenador con **IntelliJ IDEA** y probarlo en un emulador Android.
Tiempo aproximado la primera vez: 30–45 min (la mayor parte son descargas).

---

## 0. Qué vas a necesitar

| Herramienta | Versión | Para qué |
|---|---|---|
| IntelliJ IDEA (Ultimate o Community) | 2026.1 o superior | Editar y ejecutar el proyecto |
| Plugin **Android** de IntelliJ | el que trae IntelliJ | Configuraciones "Android App", emulador, SDK |
| JDK | **21** (p. ej. Amazon Corretto 21) | Ejecutar Gradle |
| Android SDK Platform | **Android 37** (API 37) | Compilar (`compileSdk = 37`) |
| Android SDK Build-Tools | **37.0.0** | Compilar el APK |
| Android SDK Platform-Tools | última | `adb` |
| Android Emulator | última | Emulador |
| Imagen del emulador | **Android 36** (API 36), Google APIs | Ver nota del paso 6 |
| Git | cualquiera | Clonar el repositorio |

No hay que instalar nada a mano dentro del proyecto: Gradle descarga solo las librerías
(Kotlin 2.4.20, Compose Multiplatform 1.12.1, AGP 9.1.1, Gradle 9.5.1...).

**Ordenador:** 16 GB de RAM recomendados (el emulador consume bastante) y ~10 GB libres de disco.
La virtualización tiene que estar activada (VT-x/AMD-V en la BIOS; en Linux, que exista `/dev/kvm`).

---

## 1. Clonar el repositorio

```bash
git clone https://github.com/nicolasezequielmaqueda/JoinMe.git
```

En IntelliJ: **File → Open…** y selecciona la carpeta `JoinMe` (la que contiene `settings.gradle.kts`).
Si pregunta, elige **Trust Project**.

> No toques todavía nada aunque salgan errores de Gradle: primero hay que instalar el SDK.

---

## 2. Activar el plugin de Android

Si en **Run → Edit Configurations → +** solo te aparece "Kotlin" y no "Android App", el plugin está desactivado.

1. **Settings** (`Ctrl+Alt+S`, en Mac `⌘,`) → **Plugins** → pestaña **Installed**.
2. Busca **Android** y actívalo. Si aparecen "Android Design Tools" o "Jetpack Compose", actívalos también.
3. (Opcional) En **Marketplace** instala **Kotlin Multiplatform** (de JetBrains).
4. Reinicia IntelliJ.

---

## 3. Instalar el Android SDK

### Opción A – Desde IntelliJ (recomendada)

1. **Settings → Languages & Frameworks → Android SDK**.
2. **Android SDK Location**: deja la ruta por defecto o pon una tuya
   (Linux: `~/Android/Sdk`, Mac: `~/Library/Android/sdk`, Windows: `C:\Users\<tú>\AppData\Local\Android\Sdk`).
   Si no existe, pulsa **Edit** y el asistente lo descarga.
3. Pestaña **SDK Platforms**: marca **Android 37**.
4. Pestaña **SDK Tools** (marca *Show Package Details* si hace falta) y marca:
   - Android SDK Build-Tools → **37.0.0**
   - **Android SDK Platform-Tools**
   - **Android Emulator**
   - **Android SDK Command-line Tools (latest)**
5. **Apply** y acepta las licencias.

### Opción B – Por terminal (Linux, si la opción A falla)

```bash
# 1. Descargar las command-line tools
mkdir -p ~/Android/Sdk/cmdline-tools && cd /tmp
curl -LO https://dl.google.com/android/repository/commandlinetools-linux-16111833_latest.zip
unzip commandlinetools-linux-*_latest.zip
mv cmdline-tools ~/Android/Sdk/cmdline-tools/latest

# 2. Aceptar licencias e instalar los paquetes
export ANDROID_HOME=~/Android/Sdk
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --install \
  "platform-tools" "platforms/android-37.0" "build-tools/37.0.0" "emulator" \
  "system-images/android-36/google_apis/x86_64"
```

> Las versiones nuevas de `sdkmanager` usan `/` en los nombres de paquete (`platforms/android-37.0`),
> las antiguas usan `;` (`platforms;android-37`).

---

## 4. Decirle al proyecto dónde está el SDK

En la raíz del proyecto debe existir el fichero **`local.properties`** con la ruta del SDK.
IntelliJ suele crearlo solo al sincronizar; si no, créalo tú:

```properties
# Linux
sdk.dir=/home/<tu-usuario>/Android/Sdk
# Mac
# sdk.dir=/Users/<tu-usuario>/Library/Android/sdk
# Windows (barras dobles)
# sdk.dir=C\:\\Users\\<tu-usuario>\\AppData\\Local\\Android\\Sdk
```

Este fichero está en `.gitignore`: cada uno tiene el suyo y **no se sube** al repositorio.

---

## 5. JDK de Gradle y sincronizar

1. **Settings → Build, Execution, Deployment → Build Tools → Gradle**.
2. **Gradle JVM** → elige un **JDK 21** (si no tienes, *Download JDK…* → versión 21, p. ej. Amazon Corretto).
3. Pulsa el icono del elefante **Sync Gradle Changes** (o *Reload All Gradle Projects* en la ventana Gradle).

La primera sincronización tarda unos minutos. Al terminar no debe haber errores rojos en la ventana *Build*.

En Linux/Mac, si al usar la terminal sale `Permission denied` con `./gradlew`:

```bash
chmod +x gradlew
```

---

## 6. Crear el emulador

1. **View → Tool Windows → Device Manager** → **+** → **Create Virtual Device**.
2. Teléfono: **Pixel 8** (o cualquier Pixel).
3. Imagen del sistema: **Android 36 (API 36) – Google APIs – x86_64**
   (en Mac con chip Apple: la versión **arm64-v8a**).
4. Nombre: por ejemplo `JoinMe_Pixel` → **Finish**.

> ⚠️ **No uses la imagen de Android 37 para el emulador.** Con la versión actual (octubre 2026) se queda
> en pantalla negra: el sistema Android falla al arrancar (`Invalid feature flag ... aapm_feature_disable_insecure_wifi_autojoin`).
> La app compila contra Android 37 pero funciona perfectamente en un emulador Android 36.

---

## 7. Crear la configuración de ejecución y lanzar la app

1. **Run → Edit Configurations…** → **+** → **Android App**.
2. Rellena:
   - **Name:** `androidApp`
   - **Module:** `JoinMe.androidApp.main`
   - **Launch:** `Default Activity`
3. **OK**.
4. Arriba, en el selector de dispositivos, elige `JoinMe_Pixel`.
5. Pulsa **▶ Run** (`Shift+F10`).

La primera vez el emulador tarda 1–2 minutos en arrancar. Después se abre JoinMe solo.

---

## 8. Qué probar (flujo principal)

| Paso | Qué debería pasar |
|---|---|
| Abrir la app | Pantalla **Activities** con la fecha de hoy y varias actividades de ejemplo |
| Pulsar **Table Tennis** | Detalle con `1 of 2 people`, estado `OPEN` y botón **Join activity** |
| Pulsar **Join activity** | Pasa a `2 of 2`, estados `FULL` + `JOINED`, mensaje "You joined this activity!" y el botón cambia a **Leave activity** |
| Pestaña **My Activities** | Table Tennis aparece en **Joined** |
| **+ New** en Activities | Formulario: elige tipo, fecha, hora y lugar → se activa **Create activity** |
| Crear la actividad | Se abre su detalle y aparece en **My Activities → Created by you** |
| Abrir **Board Games** (creada por ti) | Botón **Cancel activity** con confirmación |

> Los datos son de ejemplo y se guardan **solo en memoria** (M1): al cerrar la app del todo se reinician.
> La persistencia con Room y Supabase llega en M2.

**Accesibilidad:** en el emulador, *Settings → Display → Font size* al máximo y repetir el flujo: todo debe seguir visible y usable.

---

## 9. Ejecutar los tests

Desde IntelliJ: ventana **Gradle** → `JoinMe → shared → Tasks → verification → testAndroidHostTest` (doble clic).

O desde la terminal del proyecto:

```bash
./gradlew :shared:testAndroidHostTest
```

Debe terminar con **BUILD SUCCESSFUL** (14 tests: `ActivityRulesTest` y `ActivityValidatorTest`).

---

## 10. Plan B: lanzar sin la configuración de IntelliJ

```bash
~/Android/Sdk/emulator/emulator -avd JoinMe_Pixel &          # arrancar el emulador
./gradlew :androidApp:installDebug                           # compilar e instalar
~/Android/Sdk/platform-tools/adb shell am start -n com.joinme/.MainActivity   # abrir la app
```

También puedes usar un **móvil real**: activa *Opciones de desarrollador → Depuración USB*, conéctalo por USB
y aparecerá en el selector de dispositivos de IntelliJ.

---

## 11. iOS (solo con Mac)

1. Instala **Xcode** desde la App Store.
2. Abre `iosApp/iosApp.xcodeproj` con Xcode.
3. En `iosApp/Configuration/Config.xcconfig` pon tu `TEAM_ID` (solo hace falta para un iPhone real; el simulador no lo necesita).
4. Elige un simulador de iPhone y pulsa **Run**. Xcode compila primero el código compartido de Kotlin.

En Linux y Windows no se puede compilar iOS: es normal ver el aviso de que las tareas de iOS están desactivadas.

---

## 12. Problemas frecuentes

| Problema | Solución |
|---|---|
| `SDK location not found` | Falta `local.properties` o la ruta está mal (paso 4) |
| En *Edit Configurations* solo sale "Kotlin" | Activar el plugin Android (paso 2) |
| `Unsupported class file major version` / errores raros de Gradle | Gradle JVM no es JDK 21 (paso 5) |
| `./gradlew: Permission denied` | `chmod +x gradlew` |
| Emulador en pantalla negra que nunca arranca | Estás usando la imagen Android 37: crea el emulador con Android 36 (paso 6) |
| Aviso "Suggested minimum system RAM…" | Es solo un aviso. Cierra programas pesados si va lento |
| El emulador no arranca en Linux | Comprueba que existe `/dev/kvm` y que tu usuario puede usarlo |
| Cambios en el código que no se ven | Vuelve a pulsar **Run**; si sigue igual, *Build → Clean Project* y Run |

---

## 13. Estructura del código (para orientarte)

```
shared/src/commonMain/kotlin/com/joinme/
├── App.kt                 Punto de entrada de la UI (Android e iOS)
├── model/                 Datos: Activity, ActivityType, ActivityStatus, ResidenceLocation...
├── domain/                Reglas: ActivityRules (join/leave/cancel/estado), ActivityValidator
├── data/                  ActivityRepository + FakeActivityRepository (datos de ejemplo en memoria)
├── util/                  Fechas y horas
└── ui/
    ├── theme/             Colores, tipografía (Inter), tema claro/oscuro
    ├── icons/             Todos los iconos (Phosphor) en un único fichero
    ├── components/        Piezas reutilizables: tarjetas, botones, pills, secciones...
    ├── navigation/        Rutas, navegación y barra de pestañas
    └── screens/           Una carpeta por pantalla, cada una con su ViewModel
shared/src/commonTest/     Tests de las reglas de negocio
androidApp/                MainActivity (solo arranca App())
iosApp/                    Proyecto Xcode (solo arranca App())
```

Flujo de datos: **Pantalla (Compose) → ViewModel → Repository → (M2: Room / Supabase)**.
