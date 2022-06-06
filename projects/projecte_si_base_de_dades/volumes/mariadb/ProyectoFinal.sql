create database proyecto;
use proyecto;

create table Ciudad(
id_Ciudad int primary key auto_increment,
nombre_Ciudad varchar(30)
);

create table Idioma(
id_idioma int primary key,
nombre_Idioma varchar(30)
);

create table Pais(
id_Pais int primary key,
nombre_Pais varchar(30),
id_Idioma int,

foreign key (id_Idioma) references Idioma(id_idioma));

create table Cine(
id_Cine int primary key auto_increment,
nombre varchar(30),
direccion varchar(30),
telefono varchar(30),
email varchar(30),
maps varchar(100),
id_Ciudad int,
foreign key(id_Ciudad) references Ciudad(id_Ciudad)
);

create table Sala(
id int primary key auto_increment,
id_sala int unique,
id_Cine int unique,

foreign key(id_Cine) references Cine(id_Cine)
);

create table Asiento(
id_Asiento int primary key auto_increment,
num_asiento int unique,
num_fila int unique,
id int,

foreign key(id) references Sala(id)
);

create table Genero(
id_Genero int primary key,
nombre_Genero varchar(30),
id_idioma int,

foreign key(id_Idioma) references Idioma(id_Idioma)
);

create table Pelicula(
id_Pelicula int primary key,
titulo varchar(30),
fecha_estreno date,
duracion varchar(10),
valoracion varchar(30),
id_Pais int,
id_Idioma int,
id_Genero int,

foreign key(id_Pais) references Pais(id_Pais),
foreign key(id_Idioma) references Idioma(id_Idioma),
foreign key(id_Genero) references Genero(id_Genero)
);

create table Media(
tipo_Archivo enum ('jpg','mp4') primary key,
ruta_path varchar(30),
id_Pelicula int,

foreign key(id_Pelicula) references Pelicula(id_Pelicula)
);

create table Cartelera(
id_Cartelera int primary key,
id int unique,
id_Pelicula int unique,
fecha_ini date unique,
precio varchar(10),

foreign key(id) references Sala(id),
foreign key(id_Pelicula) references Pelicula(id_Pelicula)

);

create table Persona(
DNI varchar(9) primary key,
id_Persona int unique,
nombre varchar(10),
apellido1 varchar(10),
apellido2 varchar(10)
);

create table Actor(
id_Persona int primary key,
id_Actor int unique,
biografia varchar(30),
id_Actor2 int,
id_Pelicula int,

foreign key(id_Persona) references Persona(id_Persona),
foreign key (id_Actor2) references Actor(id_Actor),
foreign key(id_Pelicula) references Pelicula(id_Pelicula)
);

create table Director(
id_Persona int primary key,
id_Director int unique,
biografia varchar(30),
id_Pelicula int,

foreign key(id_Persona) references Persona(id_Persona),
foreign key(id_Pelicula) references Pelicula(id_Pelicula)
);

create table metodo_Pago(
id_Metodo_Pago int primary key
);

create table Paypal(
id_Metodo_Pago int primary key,
usuario varchar(10),

foreign key(id_Metodo_Pago) references metodo_Pago(id_Metodo_Pago)
);

create table tarjeta_Credito(
id_Metodo_Pago int primary key,
num_Tarjeta int unique,
cvv int(3),
fecha_Caducidad date,

foreign key(id_Metodo_Pago) references metodo_Pago(id_Metodo_Pago)
);

create table Cliente(
DNI int primary key,
nombre varchar(10),
apellido1 varchar(10),
apellido2 varchar(10),
email varchar(30)
);

create table Entrada(
id_Entrada int primary key,
precio int
);

create table Compra(
DNI int primary key,
id_Metodo_Pago int unique,
id_Entrada int,
fecha_Compra date,
id_Cartelera int,
id_Asiento int,

foreign key(DNI) references Cliente(DNI),
foreign key(id_Metodo_Pago) references metodo_Pago(id_Metodo_Pago),
foreign key(id_Entrada) references Entrada(id_Entrada),
foreign key(id_Cartelera) references Cartelera(id_Cartelera),
foreign key(id_Asiento) references Asiento (id_Asiento)
);