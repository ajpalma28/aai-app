CREATE TABLE usuario (
  idusuario CHAR(13) NOT NULL,
  dni CHAR(9) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  fnacimiento DATE NOT NULL,
  PRIMARY KEY (idusuario),
  UNIQUE KEY idusuario_UNIQUE (idusuario),
  UNIQUE KEY dni_UNIQUE (dni)
)