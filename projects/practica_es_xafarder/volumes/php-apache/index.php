<!DOCTYPE html>
<html lang="en">
<head>
    <title>Portada</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
<?php

include_once('config.php');

$sql = 'select * from noticia';

$result=mysqli_query($conn,$sql);

echo '<h1>Es Xafarder</h1>';

echo '<div class="wrapper">';
while ($row = $result->fetch_assoc())
{
    echo '<div class="box">';
    echo '<h2><a href="noticia.php?id='.$row['idnoticia'].'">'.$row["titular"].'</a></h2>';
    echo '<br>';
    echo substr($row["noticia"],0,100);
    echo '</div>';
}
echo '</div>';

?>
</body>
</html>
