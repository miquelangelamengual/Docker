<!DOCTYPE html>
<html lang="en">
<head>
    <title>Notícia</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
<?php

include_once('../config.php');

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
}

?>
<form action='' method="post">
    <input type="text" name="titular" value="<?php echo $titular; ?>"/>
    <input type="date" name="data" value="<?php echo $data->format('Y-m-d'); ?>"/>
    <textarea name="contingut"><?php echo $contingut; ?></textarea>
    <input type="submit" value="Enviar"/>
</form>
<button onclick="window.location='index.php'">Cancelar</button>
<?php

if ($_POST) {
    //Paràmetres formulari
    $titular = $_POST["titular"];
    $data = $_POST["data"];
    $contingut = $_POST["contingut"];

    //Comprovem si l'usuari i contrasenya són vàlids
    $sql = 'insert into noticia(titular,data,noticia) VALUES ("'.$titular.'","'.$data.'","'.$contingut.'")';

    $result = mysqli_query($conn, $sql);

    echo '<h1>Noticia insertada amb èxit</h1>';
    echo '<h4>Serà redireccionat en 5 segons.</h4>';

    echo '<script>';
    echo 'setTimeout(function(){ window.location="index.php" },5000);';
    echo '</script>';

}

?>

</body>
</html>
