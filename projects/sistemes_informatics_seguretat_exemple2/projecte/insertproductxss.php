
<!DOCTYPE html>
<html lang="en">
<head></head>
<style>
input, label, textarea{
    display: block;
}
</style>
<body>

<form action='http://172.16.61.249/insertproductxss.php' method="post">
    <label>Nom</label>
    <input type="text" name="nom" required="required">

    <label>Descripció</description>
    <textarea name="description"></textarea>

    <label>Preu</label>
    <input type="number" name="price" required>

    <label>Observacions</description>
    <textarea name="observacions"></textarea>

    <label>ID Usuari</label>
    <input type="number" name="user" required="required">
    
    <input type="submit" value="Submit"/>
</form>
</body>
</html>
<?php

if($_POST){
    //Connexió BBDD
    $servername = "basededades";
    $username = "root";
    $password = "sistemes";
    $db = "si";

    $conn = new mysqli($servername, $username, $password,$db);

    $nom = $_POST["nom"];
    $descripcio = $_POST["description"];
    $preu = $_POST["price"];
    $observacions = $_POST["observacions"];
    $user=$_POST["user"];

    $sql2 = "insert into product set name='$nom', description='$descripcio', price='$preu', observations='$observacions', user_iduser='$user'";
        
    $result=mysqli_query($conn,$sql2);

    echo "<br><br>PRODUCTE INSERTAT";
}
?>
