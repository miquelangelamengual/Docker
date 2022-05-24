<!DOCTYPE html>
<html lang="en">
<head>
    <title>Notícia</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
<?php

include_once('./config.php');

$id = '';
$titular = '';
$data = '';
$contingut = '';

if (isset($_GET["id"])) {
    $id = $_GET["id"];
    $sql = "select * from noticia where idnoticia=$id";

    $result = mysqli_query($conn, $sql);

    if ($row = $result->fetch_assoc()) {
        $titular = $row["titular"];
        $data = new DateTime($row["data"]);
        $contingut = $row["noticia"];
    }

    $sql2 = "select * from comentari where noticia_idnoticia=$id";

    $result2 = mysqli_query($conn, $sql2);

}

?>

<h1><?php echo $titular; ?></h1>
<main>
    <section>
        <p><?php echo $data->format('d/m/Y'); ?></p>
        <p><?php echo $contingut; ?></p>
    </section>
    <section>
        <h3>Comentaris de la notícia:</h3>
        <ul>
            <?php
            while ($row = $result2->fetch_assoc()) {
                echo '<li>';
                echo '<span>Usuari:'.$row["usuari"].'</span>';
                echo '<p>'.$row["comentari"].'</p>';
                echo '</li>';
            }
            ?>
        </ul>
        <form action='' method="post">
            <label>Usuari</label>
            <input type="text" name="usuari"/>
            <label>Comentari</label>
            <textarea name="comentari"></textarea>
            <input type="submit" value="Enviar"/>
        </form>
    </section>
</main>

<?php

if ($_POST) {
    //Paràmetres formulari
    $usuari = $_POST["usuari"];
    $comentari = $_POST["comentari"];

    //Comprovem si l'usuari i contrasenya són vàlids
    $sql = 'insert into comentari(usuari,comentari,noticia_idnoticia) VALUES ("'.$usuari.'","'.$comentari.'","'.$id.'")';

    echo $sql;
    $result = mysqli_query($conn, $sql);

    echo '<h2>Comentari insertat amb èxit</h2>';

    echo '<script>';
    echo 'setTimeout(function(){ window.location="noticia.php?id='.$id.'" },3000);';
    echo '</script>';

}

?>
</body>
</html>
