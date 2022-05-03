<?php
//Connexió BBDD
$servername = "basededades";
$username = "root";
$password = "sistemes";
$db = "si";

$conn = new mysqli($servername, $username, $password,$db);

$idproducte = $_GET["id"];

$sql2 = "select * from product where idproduct='$idproducte'";
$result=mysqli_query($conn,$sql2);

echo $sql2;

if ($row = $result->fetch_assoc()){
    echo '<ul>';
    echo '<li>'.$row["name"].'</li>';
    echo '<li>'.$row["description"].'</li>';
    echo '<li>'.$row["price"].'€</li>';
    echo '</ul>';
}
?>
