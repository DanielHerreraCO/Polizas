create database polizas;

use polizas;

CREATE TABLE ESTADO (
    idestado     BIGINT PRIMARY KEY,
    codigo        VARCHAR(50) NOT NULL,
    nombre        VARCHAR(50) NOT NULL,
    descripcion   VARCHAR(50),
    origen        VARCHAR(50) NOT NULL,
    indicador     boolean DEFAULT 1 NOT NULL
);

CREATE TABLE POLIZA ( 
	idPoliza BIGINT PRIMARY KEY, 
    tipoPoliza VARCHAR(20) NOT NULL, 
    idestado BIGINT NOT NULL, 
    fechaInicio DATE NOT NULL, 
    fechaFin DATE NOT NULL, 
    canonMensual DECIMAL(15,2) NOT NULL, 
    valorPrima DECIMAL(15,2) NOT NULL,
    porcentajeIPC DECIMAL(5,2) NOT NULL,
    arrendatario VARCHAR(150) NOT NULL, 
    arrendador VARCHAR(150) NOT NULL, 
    CONSTRAINT FK_POLIZA_ESTADO FOREIGN KEY (idestado) 
    REFERENCES ESTADO(idestado) 
);

CREATE TABLE Riesgo (   
	idRiesgo BIGINT PRIMARY KEY,
    descripcion VARCHAR(20) NOT NULL,
    idestado BIGINT NOT NULL,
    valorAsegurado DECIMAL(15,2) NOT NULL,
    direccionInmueble VARCHAR(150) NOT NULL,
    CONSTRAINT FK_RIESGO_ESTADO FOREIGN KEY (idestado)
    REFERENCES ESTADO(idestado)  
    
);


CREATE TABLE PolizaRiesgo ( 
	idPolizaRiesgo BIGINT PRIMARY KEY, 
    idPoliza BIGINT NOT NULL,
    idestado BIGINT NOT NULL, 
	idRiesgo BIGINT NOT NULL,
    fechaAsignacionRiesgo  DATE NOT NULL, 
    
    CONSTRAINT FK_POLIZA_RIESGO_POLIZA FOREIGN KEY (idPoliza) REFERENCES POLIZA(idPoliza), 
    CONSTRAINT FK_POLIZA_RIESGO_ESTADO FOREIGN KEY (idestado) REFERENCES ESTADO(idEstado), 
    CONSTRAINT FK_POLIZA_RIESGO_RIESGO FOREIGN KEY (idRiesgo) REFERENCES RIESGO(idRiesgo) 
);
ALTER TABLE PolizaRiesgo
MODIFY COLUMN idPolizaRiesgo BIGINT AUTO_INCREMENT;

CREATE TABLE Notificacion (
    idNotificacion     BIGINT PRIMARY KEY,
    idestado BIGINT NOT NULL, 
    mensaje        VARCHAR(250) NOT NULL,
    fechaEnvio        DATE NOT NULL, 
    Direccion   VARCHAR(250),
    tipo        VARCHAR(50) NOT NULL,
    CONSTRAINT FK_Notificacion_ESTADO FOREIGN KEY (idestado) 
    REFERENCES ESTADO(idestado) 
);


-- ESTADO
INSERT INTO ESTADO (
    idestado,
    codigo,
    nombre,
    descripcion,
    origen,
    indicador
) VALUES (
    1,
    'ACTIVA',
    'Poliza Activa',
    'Estado activo de la poliza',
    'POLIZA',
    true
);

-- POLIZA
INSERT INTO POLIZA (
    idPoliza,
    tipoPoliza,
    idestado,
    fechaInicio,
    fechaFin,
    valorMensual,
    arrendatario,
    arrendador
) VALUES (
    1001,
    'COLECTIVA',
    1,
    '2026-01-01',
    '2026-12-31',
    2500000.00,
    'Carlos Perez',
    'Inmobiliaria Bolivar'
);

-- RIESGO
INSERT INTO RIESGO (
    idRiesgo,
    descripcion,
    idestado,
    valorAsegurado,
    direccionInmueble
) VALUES (
    5001,
    'Apartamento',
    1,
    120000000.00,
    'Cra 15 # 120-45 Bogota'
);


-- POLIZA_RIESGO
INSERT INTO POLIZARIESGO (
    idPolizaRiesgo,
    idPoliza,
    idestado,
    idRiesgo,
    fechaAsignacionRiesgo
) VALUES (
    9001,
    1001,
    1,
    5001,
    '2026-01-01'
);

-- NOTIFICACION
INSERT INTO NOTIFICACION (
    idNotificacion,
    idestado,
    mensaje,
    fechaEnvio,
    Direccion,
    tipo
) VALUES (
    3001,
    1,
    'Poliza creada correctamente',
    '2026-01-01',
    'cliente@correo.com',
    'EMAIL'
);
INSERT INTO ESTADO (idestado, codigo, nombre, descripcion, origen, indicador) VALUES
(2,  'CANCELADA',    'Poliza Cancelada',     'Estado cancelado',           'POLIZA', 1),
(3,  'RENOVADA',     'Poliza Renovada',      'Estado renovado',            'POLIZA', 1),
(4,  'SUSPENDIDA',   'Poliza Suspendida',    'Estado suspendido',          'POLIZA', 1),
(5,  'VENCIDA',      'Poliza Vencida',       'Poliza expirada',            'POLIZA', 1),

(6,  'ACTIVO',       'Riesgo Activo',        'Riesgo activo',              'RIESGO', 1),
(7,  'INACTIVO',     'Riesgo Inactivo',      'Riesgo inactivo',           'RIESGO', 1),
(8,  'PENDIENTE',    'Riesgo Pendiente',     'En validación',             'RIESGO', 1),
(9,  'CANCELADO',    'Riesgo Cancelado',     'Riesgo cancelado',          'RIESGO', 1),
(10, 'OBSERVADO',    'Riesgo Observado',     'Requiere revisión',         'RIESGO', 1),

(11, 'ENVIADO',      'Notificación Enviada', 'Mensaje enviado',            'NOTIF', 1),
(12, 'LEIDO',        'Notificación Leída',   'Mensaje leído',              'NOTIF', 1),
(13, 'ERROR',        'Error Notificación',   'Fallo en envío',             'NOTIF', 1),
(14, 'REINTENTO',    'Reintento Notificación','Reenvío programado',         'NOTIF', 1),
(15, 'ARCHIVADO',    'Notificación Archivada','Histórico guardado',         'NOTIF', 1);