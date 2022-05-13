<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
<?php
$idusuari = $_GET["user"];

//Connexió BBDD
$servername = "basededades";    
$username = "root";
$password = "sistemes";
$db = "si";

$conn = new mysqli($servername, $username, $password,$db);

$sql2 = 'select * from product where user_iduser='.$idusuari;
echo $sql2;

$result=mysqli_query($conn,$sql2);

echo '<table>';
while ($row = $result->fetch_assoc())
{
    echo '<tr>';
    echo '<td><a href="product.php?id='.$row['idproduct'].'">'.$row["name"].'</a></td>';
    echo '<td>'.$row["description"].'</td>';
    echo '<td>'.$row["price"].'€</td>';
    echo '</tr>';
}
echo '</table>';

?>
</body>
</html>
