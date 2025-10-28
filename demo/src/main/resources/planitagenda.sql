CREATE DATABASE planitagenda;
USE planitagenda;

CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE servico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    duracao INT NOT NULL,
    preco DECIMAL(10,2) NOT NULL
);

CREATE TABLE funcionario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(100) NOT NULL,
);

CREATE TABLE agendamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_funcionario INT NOT NULL,
    servico VARCHAR(100) NOT NULL,
    data DATE,
    hora TIME,
    observacoes TEXT,
    status VARCHAR(20) DEFAULT 'ativo',
    FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    FOREIGN KEY (id_funcionario) REFERENCES funcionario(id)
);

CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    senha VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL
);

INSERT INTO usuario (username, senha, tipo) VALUES ('admin', '1234', 'Administrador');

SELECT * FROM usuario;
SELECT * FROM cliente;
SELECT * FROM servico;
SELECT * FROM funcionario;
SELECT * FROM agendamento;

