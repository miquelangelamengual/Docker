<!DOCTYPE html>
<html lang="en">
<head>
    <title>Llistat de notícies</title>
    <link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>
<?php

include_once('../config.php');

$sql = 'select * from noticia';

$result=mysqli_query($conn,$sql);

echo '<button><a href="noticia.php">Nova notícia</a></button>';

echo '<table id="taula">';
echo '<thead>';
echo '<tr>';
echo '<th>Titular</th>';
echo '<th>Opcions</th>';
echo '</tr>';
echo '</thead>';
echo '<tbody> ';
while ($row = $result->fetch_assoc())
{
    echo '<tr>';
    echo '<td>'.$row["titular"].'</td>';
    echo '<td><a href="noticia.php?id='.$row["idnoticia"].'"><i class="material-icons">edit</i></a></td>';
    echo '</tr>';
}
echo '</tbody> ';
echo '</table>';
?>

<script
    src="https://code.jquery.com/jquery-3.4.1.min.js"
    integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
    crossorigin="anonymous"></script>
<script src="//cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<script>
    $(document).ready( function () {
        $('#taula').DataTable();
    } );
</script>
</body>
</html>
