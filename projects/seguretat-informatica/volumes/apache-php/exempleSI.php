<!DOCTYPE html>
<html lang="en">
<head></head>
<body>
<form action='exempleSI.php' method="post">
    <input type="text" name="email" required="required"/>
    <input type="password" name="password"/>
    <input type="submit" value="Submit"/>
</form>
<?php

if($_POST){
    //Paràmetres formulari
    $email = $_POST["email"];
    $pass = $_POST["password"];

    //Connexió BBDD
    $servername = "mariadb";
    $username = "mangel";
    $password = "miquelangel";
    $db = "wordpress";

    $conn = new mysqli($servername, $username, $password,$db);

    //Comprovem si l'usuari i contrasenya són vàlids
    $sql = "select * from users where email=\"".$email."\" and password=\"".$pass."\"";

    echo $sql;

    $result=mysqli_query($conn,$sql);

    $rowcount=mysqli_num_rows($result);

    echo "<br>";
    if($rowcount > 0){
        echo "LOGIN!!";

        $sql2 = "select * from users";
        $result=mysqli_query($conn,$sql2);
        echo "<ul>";
        while ($row = $result->fetch_array())
        {
            echo '<li>'.$row[0]. ' - '.$row[1].'</li>';
        }
        echo "</ul>";
    } else {
        echo "ERROR LOGIN";
    }

}

?>

</body>
</html>
