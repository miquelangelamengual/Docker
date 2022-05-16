INSERT INTO `im_centre` (`idcentre`, `identificador`, `nom`, `any_academic`, `sincronitzar`)
VALUES (NULL, '07002208', 'IES Manacor', '2021', 0);


UPDATE `im_usuari`
SET `email` = 'jgalmes1@iesmanacor.cat'
WHERE gestib_cognom1 = "GALMES"
  and gestib_cognom2 = "RIERA";
