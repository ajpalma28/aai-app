CREATE TABLE asociacion (
  idasociacion CHAR(28) NOT NULL,
  idorganizacion CHAR(14) NOT NULL,
  idinvestigador CHAR(13) NOT NULL,
  PRIMARY KEY (idasociacion),
  UNIQUE KEY idasociacion_UNIQUE (idasociacion)
)