SELECT *
FROM `im_usuari`
WHERE CONCAT(gestib_nom, ' ', gestib_cognom1, ' ', gestib_cognom2) != gsuite_full_name and gestib_professor=1;
