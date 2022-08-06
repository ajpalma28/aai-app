CREATE TABLE sesion (
  idsesion CHAR(37) NOT NULL,
  idorganizacion CHAR(14) NOT NULL,
  idinvestigador CHAR(13) NOT NULL,
  idusuario CHAR(13) NOT NULL,
  fecha DATE NOT NULL,
  resumen TEXT NOT NULL,
  PRIMARY KEY (idsesion),
  UNIQUE KEY idsesion_UNIQUE (idsesion)
)