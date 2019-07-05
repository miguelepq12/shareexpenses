/* Creamos los roles*/
INSERT INTO roles (id,name) VALUES (1,'ADMIN');
INSERT INTO roles (id,name) VALUES (2,'USER');

/* Creamos algunos usuarios*/
INSERT INTO users (id,create_at,email,pass,profile_img,username) VALUES (1,NOW(),'miguelepq@gmail.com','$2a$10$O9wxmH/AeyZZzIS09Wp8YOEMvFnbRVJ8B4dmAMVSGloR62lj.yqXG','img_profile.jpg','miguelepq12');

/*Asignamos rol a usuario*/
INSERT INTO users_roles (users_id,roles_id) VALUES (1,2);

/*Populate table Label*/
INSERT INTO labels (id,create_at,name,color,user_id) VALUES(1,NOW(),'Comida','#f44336',1);
INSERT INTO labels (id,create_at,name,color,user_id) VALUES(2,NOW(),'Entretenimiento','#9c27b0',1);

/*Populate table PaymentMethod*/
INSERT INTO payment_methods (id,create_at,name,user_id) VALUES(1,NOW(),'Efectivo',1);
INSERT INTO payment_methods (id,create_at,name,user_id) VALUES(2,NOW(),'Tarjeta de debito',1);

/*Populate table Event*/
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(1,NOW(),10000,'img_default.png','Salida a comer pizza',1,1,1);
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(2,NOW(),15000,'img_default.png','Salida al cine',2,1,1);
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(3,NOW(),20000,'img_default.png','Salida al cine Viernes',2,2,1);
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(4,NOW(),5000,'img_default.png','Salida al paintball',2,2,1);
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(5,NOW(),6000,'img_default.png','Hamburguesada en El gordo',1,1,1);
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(6,NOW(),30000,'img_default.png','Pizzada viernes',1,2,1);
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(7,NOW(),50000,'img_default.png','Comida para la casa',1,2,1);
INSERT INTO events (id,create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(8,NOW(),1000,'img_default.png','Jugar bolos',2,2,1);

/*Populate table Member*/
INSERT INTO members (id,create_at,amount,name,event_id,payment_method_id) VALUES(1,NOW(),5000,'Miguel Piña',1,1);
INSERT INTO members (id,create_at,amount,name,event_id,payment_method_id) VALUES(2,NOW(),5000,'Jose Daniel',1,2);

INSERT INTO members (id,create_at,amount,name,event_id,payment_method_id) VALUES(3,NOW(),5000,'Miguel P',2,1);
INSERT INTO members (id,create_at,amount,name,event_id,payment_method_id) VALUES(4,NOW(),5000,'Juan C',2,1);
INSERT INTO members (id,create_at,amount,name,event_id,payment_method_id) VALUES(5,NOW(),3000,'Jose alfredo',2,1);