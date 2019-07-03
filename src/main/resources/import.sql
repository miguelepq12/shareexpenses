/* Creamos los roles*/
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

/* Creamos algunos usuarios*/
INSERT INTO users (create_at,email,pass,profile_img,username) VALUES (NOW(),'miguelepq@gmail.com','$2a$10$O9wxmH/AeyZZzIS09Wp8YOEMvFnbRVJ8B4dmAMVSGloR62lj.yqXG','img_profile.jpg','miguelepq12');

/*Asignamos rol a usuario*/
INSERT INTO users_roles (users_id,roles_id) VALUES (1,2);

/*Populate table Label*/
INSERT INTO labels (create_at,name,color,user_id) VALUES(NOW(),'Comida','#f44336',1);
INSERT INTO labels (create_at,name,color,user_id) VALUES(NOW(),'Entretenimiento','#9c27b0',1);

/*Populate table PaymentMethod*/
INSERT INTO payment_methods (create_at,name,user_id) VALUES(NOW(),'Efectivo',1);
INSERT INTO payment_methods (create_at,name,user_id) VALUES(NOW(),'Tarjeta de debito',1);

/*Populate table Event*/
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),10000,'img_default.png','Salida a comer pizza',1,1,1);
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),15000,'img_default.png','Salida al cine',2,1,1);
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),20000,'img_default.png','Salida al cine Viernes',2,2,1);
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),5000,'img_default.png','Salida al paintball',2,2,1);
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),6000,'img_default.png','Hamburguesada en El gordo',1,1,1);
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),30000,'img_default.png','Pizzada viernes',1,2,1);
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),50000,'img_default.png','Comida para la casa',1,2,1);
INSERT INTO events (create_at,amount,img,name,label_id,payment_method_id,user_id) VALUES(NOW(),1000,'img_default.png','Jugar bolos',2,2,1);

/*Populate table Member*/
INSERT INTO members (create_at,amount,name,event_id,payment_method_id) VALUES(NOW(),5000,'Miguel Piña',1,1);
INSERT INTO members (create_at,amount,name,event_id,payment_method_id) VALUES(NOW(),5000,'Jose Daniel',1,2);

INSERT INTO members (create_at,amount,name,event_id,payment_method_id) VALUES(NOW(),5000,'Miguel P',2,1);
INSERT INTO members (create_at,amount,name,event_id,payment_method_id) VALUES(NOW(),5000,'Juan C',2,1);
INSERT INTO members (create_at,amount,name,event_id,payment_method_id) VALUES(NOW(),3000,'Jose alfredo',2,1);