<!DOCTYPE html>
<html lang="en">
<head></head>
<body>

<form action='' method="post">
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
    $servername = "basededades";
    $username = "root";
    $password = "sistemes";
    $db = "si";

    $conn = new mysqli($servername, $username, $password,$db);

    //Comprovem si l'usuari i contrasenya són vàlids
    $sql = "select * from user where email=\"".$email."\" and password=\"".$pass."\"";

    echo $sql;

    $result=mysqli_query($conn,$sql);

    $rowcount=mysqli_num_rows($result);

    echo "<br>";
    if($rowcount > 0){
        echo "LOGIN!!";

        session_start();
        if($row = $result->fetch_assoc()){
            $obj = new stdClass();
            $obj->user = $row["email"];
            $_SESSION["USER"] = $obj;
        }

    } else {
        echo "ERROR LOGIN";
    }

}

?>

</body>
</html>
