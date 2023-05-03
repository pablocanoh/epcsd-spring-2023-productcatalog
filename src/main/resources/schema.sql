INSERT INTO category (id, name, description, parent_id)
VALUES (1, 'Cámaras', 'Cámaras', null)
     , (2, 'Cámaras de Fotos', 'Cámaras de Fotos', 1)
     , (3, 'Cámaras de Video', 'Cámaras de Video', 1)
     , (4, 'Iluminación', 'Todo tipo de accesorios de iluminación', null)
     , (5, 'Focos', 'Focos direccionales, omnidireccionales, etc.', 4)
     , (6, 'Difusores', 'Difusores para focos', 4)
;

INSERT INTO product (name, description, daily_price, brand, model, category_id)
VALUES ('Canon 500D', 'Cámara de fotos Canon 500D', 100, 'Canon', '500D', 2)
     , ('Canon EOS R8', 'Cámara de fotos Canon EOS R8', 200, 'Canon', 'EOS R8', 2)
     , ('Canon EOS R5 C', 'Cámara de video Canon EOS R5 C', 250, 'Canon', 'EOS R5 C', 3)
     , ('Foco Phillips 120L', 'Foco LED Phillips de luz blanca 120W. Sin difusor', 80, 'Phillips', '120L', 2)
     , ('Foco Phillips 220L', 'Foco LED Phillips de luz blanca 220W. Sin difusor', 120, 'Phillips', '220L', 2)
;