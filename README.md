# Ponceapp (Live Yeessenger)

Es la *nueva* forma para chatear!


* [Instalación Base de Datos y Server](#instalacion-base-de-datos-y-server)
* [API RESTful](#api-restful)
* [Ejemplos de Request y Response](#ejemplos-de-request-y-response)
* [Configurar IP](#configurar-ip)

## Instalación Base de Datos y Server

Instalar ```MySQL``` mediante WAMP, LAMP, MAMP, XAMPP o aparte. Luego, crear una base de datos llamada ```ponceapp``` y ejecutar el script ```ponceappDB.sql``` para crear las tablas y registros iniciales.

Después es necesario instalar ```Node.js```, ubicarse dentro de la carpeta ```Server``` e instalar las dependecias con el comando:
```sh
$ npm install
```
Cambiar los parámetros (como el usuario y contraseña) de la conexión a la base de datos en el archivo ```Server/connection.js```. Finalmente correr el server con:
```sh
$ npm app.js
```

Para probar que el Server funciona basta con abrir la dirección ```http://localhost:3000/```.

## API RESTful

| Endpoint              | Tipo   | Descripción                                        |
| -----------           | ------ | -----------                                        |
| [```/login/```](#post-login)                | POST    | Iniciar sesión                         |
| [```/user/```](#get-user)                | GET    | Obtiene todos los usuarios                         |
| [```/user/```](#post-user)                | POST   | Crea un nuevo usuario                              |
| [```/user/:id/```](#get-userid)             | GET    | Información del usuario ```:id```                  |
| [```/user/:id/friends/```](#put-useridfriends)     | GET    | Obtiene los amigos del usuario ```:id```           |
| [```/user/:id/nickname/```](#put-useridnickname)    | PUT    | Modifica el nickname del usuario ```:id```         |
| [```/user/:id/email/```](#put-useridemail)       | PUT    | Modifica el email del usuario ```:id```            |
| [```/user/:id/password/```](#put-useridpassword)    | PUT    | Modifica la contraseña del usuario ```:id```       |
| [```/user/:id/image/```](#put-useridimage)       | PUT    | Modifica la ruta de la imagen del usuario ```:id```|
| [```/user/:id/subnick/```](#put-useridsubnick)		 		| PUT    | Modifica el subnick del usuario ```:id```          |
| [```/user/:id/nickname_style/```](#put-useridnickname_style)		| PUT    | Modifica el estilo del nickname del usuario ```:id```          |
| [```/user/:id/msg_style/```](#put-useridmsg_style)     | PUT    | Modifica el estilo de los mensajes del usuario ```:id```          |
| [```/user/:id/state/```](#put-useridstate)       | PUT    | Modifica el estado del usuario ```:id```           |


## Ejemplos de Request y Response

### POST ```/login```

Ejemplo: ```http://dominio/login/```

Body:

    {
        "email": "indo@indo.com",
        "password": "clst",
    }
    
Respuesta:

    {
      "id": 3,
      "nickname": "indomicu",
      "subnick": "holi",
      "email": "indo@indo.cl",
      "nickname_style": null,
      "msg_style": "{bold: false, italic: false, color: '#223344'}",
      "avatar": "foto.png",
      "state": "offline"
    }

### GET ```/user```

Ejemplo: ```http://dominio/user/```

Respuesta:

    [
        {
            "id": 1,
            "nickname": "basuritaxx",
            "subnick": null,
            "email": "holi@ad.cl",
            "nickname_style": null,
            "msg_style": "{bold: false, italic: false, color: '#000000'}",
            "avatar": null,
            "state": "away"
        },
        {
            "id": 2,
            "nickname": "eyesan",
            "subnick": null,
            "email": "eyesan@eye.cl",
            "nickname_style": null,
            "msg_style": "{bold: true, italic: false, color: '#000000'}",
            "avatar": null,
            "state": "offline"
        },
        {
            "id": 3,
            "nickname": "indomicu",
            "subnick": "CLST <3",
            "email": "indo@indo.cl",
            "nickname_style": null,
            "msg_style": "{bold: false, italic: false, color: '#223344'}",
            "avatar": "foto.png",
            "state": "offline"
        }
    ]
### POST ```/user```

Ejemplo: ```http://dominio/user/```

Body:

    {
        "nickname": "chamakito",
        "email": "xx_poncioh_xx@hotmail.com",
        "password": "1234",
        "avatar": "rikolino.png"
    }
    
Respuesta:

    {
      "status": 0,
      "message": "Usuario ingresado con éxito"
    }

### GET ```/user/[id]```
Ejemplo: ```http://dominio/user/1/```

Respuesta:

    [
      {
        "id": 1,
        "nickname": "basuritaxx",
        "subnick": null,
        "email": "escoria_humana@ad.cl",
        "nickname_style": null,
        "msg_style": "{bold: false, italic: false, color: '#000000'}",
        "avatar": null,
        "state": "away"
      }
    ]

### GET ```/user/[id]/friends```
Ejemplo: ```http://dominio/user/1/friends/```

Respuesta:

    [
      {
        "id": 2,
        "nickname": "eyesan",
        "subnick": null,
        "email": "eye@eye.cl",
        "nickname_style": null,
        "msg_style": "{bold: true, italic: false, color: '#000000'}",
        "avatar": null,
        "state": "offline"
      },
      {
        "id": 3,
        "nickname": "indomicu",
        "subnick": "CLST <3",
        "email": "indo@indo.cl",
        "nickname_style": null,
        "msg_style": "{bold: false, italic: false, color: '#223344'}",
        "avatar": "duck.png",
        "state": "offline"
      }
    ]
    
### PUT ```/user/[id]/nickname```
Ejemplo: ```http://dominio/user/1/nickname/```

Body:

    {
        "nickname": "el_chamakitoxx_"
    }

Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
### PUT ```/user/[id]/email```
Ejemplo: ```http://dominio/user/1/email/```

Body:

    {
        "email": "escoria_humana2@ad.cl"
    }
    
Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
    
### PUT ```/user/[id]/password```
Ejemplo: ```http://dominio/user/1/password/```

Body:

    {
        "password": "nueva_pass"
    }

Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
    
### PUT ```/user/[id]/image```
Ejemplo: ```http://dominio/user/1/image/```

Body:

    {
        "avatar": "nueva_foto.png"
    }

Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
    
### PUT ```/user/[id]/subnick```
Ejemplo: ```http://dominio/user/1/subnick/```

Body:

    {
        "subnick": "alguien va al costa hoy?"
    }

Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
    
### PUT ```/user/[id]/nickname_style```
Ejemplo: ```http://dominio/user/1/nickname_style/```

Body:

    {
        "nickname_style": "{bold: true, italic: true, color: '#228ac62'}"
    }

Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
    
### PUT ```/user/[id]/msg_style```
Ejemplo: ```http://dominio/user/1/msg_style/```

Body:

    {
        "msg_style": "{bold: true, italic: true, color: '#228ac62'}"
    }


Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
    
### PUT ```/user/[id]/state```
Ejemplo: ```http://dominio/user/1/state/```

Body:

    {
        "state": "online"
    }

Respuesta:

    {
      "status": 0,
      "message": "Usuario modificado con éxito"
    }
    


## Configurar IP
Dentro del paquete ```Conexion``` de la aplicación están la IPs del server. Cambiar por la suya.