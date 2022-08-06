CREATE TABLE investigador (
  idinvestigador CHAR(13) NOT NULL,
  dni CHAR(9) NOT NULL,
  apellidos VARCHAR(100) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  fnacimiento DATE NOT NULL,
  contrasena VARCHAR(30) NOT NULL,
  notificaciones ENUM('true','false') NOT NULL,
  terminoscondiciones ENUM('true','false') NOT NULL,
  PRIMARY KEY (idinvestigador),
  UNIQUE KEY idinvestigador_UNIQUE (idinvestigador),
  UNIQUE KEY dni_UNIQUE (dni)
)