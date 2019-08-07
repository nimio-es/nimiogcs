[![SourceLevel](https://app.sourcelevel.io/github/saulo-alvarado/nimiogcs.svg)](https://app.sourcelevel.io/github/saulo-alvarado/nimiogcs)

# ¿Qué es NIMIO GCS?

Es un prototipo totalmente funcional de una aplicación para la Gestión de la Configuración del Software desarrollada a principios de 2016 para Bankinter Global Services y que finalmente no llegó a ponerse en producción. El objeto era poner orden y facilitar la construcción/evolución de Software en un entorno de desarrollo con las siguientes particularidades:

* Código centralizado con Subversion
* Troncal equivale a código en producción (o última RELEASE)
* Cualquier desarrollo se hace siempre y exclusivamente sobre ramas funcionales (evolutivas y correctivas)
* Reintegración de código en troncal de forma automática como parte del ciclo de publicación
    * Nadie tiene permisos para escribir en troncal
    * Los desarrolladores deben llevar coherencia de los cambios que llegan a troncal integrando los cambios en sus ramas
* Cada artefacto tiene un ciclo de vida pseudoindependiente y genera versiones distintas (infierno de las versiones)
* JEE 6, gestión de dependencias usando Maven y artefactos repositados en Artifactory
    * Los desarrolladores no debían cambiar la sección _build_ de pom.xml (pero podían)
    * Restricción en las librerías externas que se podían usar
    * Las librerías externas solo podían ir con ámbito "provided" y estar en el _classpath_ del servidor en tiempo de ejecución

Por tanto, el reto era lidiar con:

* M artefactos de software (librerías, aplicaciones, etc.)
* D dependencias entre ellos (misma librería podía ser usada en distintas aplicaciones) y mantener la coherencia de versiones
* N jefes de proyecto y empresas externas trabajando (evolucionando/corrigiendo) paralelamente sobre los mismos artefactos
* R ramas funcionales en curso para cada producto

## Problemas habituales

* Infierno de versiones con las librerías y las aplicaciones
* La preparación del entorno de desarrollo era compleja porque se debía montar _a trozos_
* Desconocimiento de los cambios en curso y del impacto de los mismos, principalmente por parte de los jefes de proyecto
* Frecuente colisión de cambios entre proyectos
* Cambios en el pom.xml que rompían el ciclo de construcción
* Uso de librerías externas no autorizado y no detectado hasta que el desarrollo llevaba ya mucho tiempo. 

# Limitaciones técnicas de la solución

La solución que se fuera a proponer debía encajar con las siguientes restricciones:

* Subversion como repositorio de código
* Java 6 y JEE 6 (opcionalmente Spring a modo de piloto)
* WebSphere 8.5.5 
* Oracle DB 11
* Uso de librerías externas sumamente restrictivo. No se admiten librerías de utilidades genéricias para manejo de Strings y similares.
* Uso del canal de publicación autorizado (herramientas propias de la casa)

# Características de la propuesta

## Funcionales

* Definición (vía alta en registro) de las librerías externas autorizadas
* Facilitar la construcción del entorno de desarrollo
* Definición previa de las dependencias
* Mejorar el conocimiento del impacto de un proyecto sobre los artefactos implicados
* Mejorar el conocimiento del impacto de los cambios sobre artefactos no implicados en el proyecto pero sí con dependencias de los afectados
* Control de la calidad del código como parte del ciclo de vida
* Flexibilidad a la hora de elegir la representación del entorno de trabajo que pueda ser con Maven, Grant o cualquier otra cosa
* Extensible

## Técnicas

* Java 6 y Spring 4
* Intento de incorporar algunas ideas de programación funcional y los Streams de Java 8
* Oracle DB
* Necesidad de usar bastantes librerías externas inicialmente no aprobadas

## Sobre _Deployer_

El cliente ya contaba con un canal de publicación automatizada en los entornos que debía usarse para parte de los artefactos. Para ello ofrecía una interfaz de servicio web encargada de la comunicación. El producto del cliente posee otro nombre y aquí se ha dejado _deployer_ a modo de ejemplo de lo que se buscaba. El producto final es bastante más completo.

# Sobre la versión que se encuentra aquí

La versión que actualmente se encuentra repositada aquí es una versión reducida de la que finalmente se entregó al cliente y que no se encuentra integrada usando las librerías de arquitectura de base provistas por la empresa.
