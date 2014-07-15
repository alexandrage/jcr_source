<html>
<head>
    <title>JCR Launcher :: Админ-панель</title>
</head>
<style>
.spoiler {
    width: 700px;
    border: #ccc solid 1px;
	margin-bottom: 10px;
}
.spoiler > .title {
    display: block;
    padding: 4px;
}
.spoiler > .body {
    padding: 4px;
    border-top: #ccc solid 1px;
    display: none;
}
.spoiler > :checked ~ .body {
    display: block;
}
</style>
<body>

<div class="spoiler">
    <input type="checkbox" style="display: none" id="1" />
    <label class="title" for="1">+ Блокировка / Разблокировка по HWID</label>
    <div class="body">
		<form action="jcr_admin.php?action=block_HWID" method="post">
			<table>
				<tr><td>Логин администратора<td><input name="admin_login" maxlength="50" size="30">
				<tr><td>Пароль администратора<td><input name="admin_pass" type="password" maxlength="50" size="30">
				<tr><td><br />Заполните одно из следующих полей или 2 поля сразу:
				<tr><td><br /><b>Заблокировать HWID игрока:</b>
				<tr><td>Логин игрока<td><input name="loc_login" maxlength="50" size="30">
				<tr><td><br /><b>Разблокировать HWID игрока:</b>
				<tr><td>Логин игрока<td><input name="unloc_login" maxlength="50" size="30">
				
				<tr><td colspan="2"><input type="submit" value="Заблокировать / Разблокировать">
			</table>
		</form>
	</div>
</div>

<div class="spoiler">
    <input type="checkbox" style="display: none" id="2" />
    <label class="title" for="2">+ Просмотр репорт-файла</label>
    <div class="body">
		<form action="jcr_admin.php?action=show_rfile" method="post">
			<table>
				<tr><td>Логин администратора<td><input name="admin_login" maxlength="50" size="30">
				<tr><td>Пароль администратора<td><input name="admin_pass" type="password" maxlength="50" size="30">
				
				<tr><td colspan="2"><input type="submit" value="Просмотреть репорт-файл">
			</table>
		</form>
	</div>
</div>

<div class="spoiler">
    <input type="checkbox" style="display: none" id="3" />
    <label class="title" for="3">+ Сменить статус игрока (загрузка плащей)</label>
    <div class="body">
		<form action="jcr_admin.php?action=change_status" method="post">
			<table>
				<tr><td>Логин администратора<td><input name="admin_login" maxlength="50" size="30">
				<tr><td>Пароль администратора<td><input name="admin_pass" type="password" maxlength="50" size="30">
				<tr><td><br />Заполните одно из следующих полей или 2 поля сразу:
				<tr><td><i>Для массового использования, разделяйте логины с помощью ", ".</i>
				<tr><td><br /><b>Повысить статус игрока (разрешить загружать плащи):</b>
				<tr><td>Логин игрока<td><textarea name="up_status_login" maxlength="80" size="30"></textarea>
				<tr><td><br /><b>Понизить статус игрока (запретить загружать плащи):</b>
				<tr><td>Логин игрока<td><textarea name="down_status_login" maxlength="80" size="30"></textarea>
				
				<tr><td colspan="2"><input type="submit" value="Повысить статус / Понизить статус">
			</table>
		</form>
	</div>
</div>

</body>
</html>

<?php

	define("IMPASS_CHECK", true);
	
	include("jcr_connect.php");
	include("jcr_settings.php");
	
	$action				= $_GET['action'];
	$get_admin_login	= sql_param($_POST['admin_login']);
	$get_admin_pass		= sql_param($_POST['admin_pass']);
	$loc_login			= sql_param($_POST['loc_login']);
	$unloc_login		= sql_param($_POST['unloc_login']);
	$up_status_login	= $db -> real_escape_string($_POST['up_status_login']);
	$down_status_login	= $db -> real_escape_string($_POST['down_status_login']);
	
	if ($action != null && strCmp($admin_login, $get_admin_login) != 0 && strCmp($admin_pass, $get_admin_pass) != 0)
		die ("Неверный логин или пароль администратора");
	
	if ($action == "block_HWID")
	{
		if ($loc_login == null && $unloc_login == null) die ("Поля 'Логин для блокировки' и 'Логин для разблокировки' не могут быть пустыми одновременно");
		
		if ($loc_login != null)
		{
			$query	= $db -> query("SELECT $db_colHWID FROM $db_table WHERE $db_colUser='$loc_login'") or die ($db -> error);
			
			if ($query -> num_rows == 1)
			{
				$row	= $query -> fetch_assoc();
				$bdHWID = $row[$db_colHWID];
				
				if ($bdHWID != null)
				{
					$db -> query("UPDATE $db_table SET $db_colBlHWIDs='$bdHWID' WHERE $db_colUser='$loc_login'") or die ($db -> error);
					echo ("HWID игрока '$loc_login' заблокирован<br />");
				} else
				{
					echo ("У пользователя '$loc_login' еще не установлен HWID, возможно он ни разу не проходил авторизацию<br />");
				}
			} else
			{
				echo ("Указанный логин '$loc_login' не найден<br />");
			}
		}
		
		if ($unloc_login != null)
		{
			$query	= $db -> query("SELECT $db_colBlHWIDs FROM $db_table WHERE $db_colUser='$unloc_login'") or die ($db -> error);
			
			if ($query -> num_rows == 1)
			{
				$row		= $query -> fetch_assoc();
				$blBdHWID	= $row[$db_colBlHWIDs];
				
				if ($blBdHWID != null)
				{
					$db -> query("UPDATE $db_table SET $db_colBlHWIDs=NULL WHERE $db_colUser='$unloc_login'") or die ($db -> error);
					echo ("HWID игрока '$unloc_login' разблокирован<br />");
				} else
				{
					echo ("HWID пользователя '$unloc_login' не заблокирован (его колонка пуста)<br />");
				}
			} else
			{
				echo ("Указанный логин '$unloc_login' не найден<br />");
			}
		}
	} else if ($action == "show_rfile")
	{
		$url = "files/admin/".$report_file;
		if (file_exists($url)) echo "<meta http-equiv=\"refresh\" content=\"0; url=$url\">";
		else echo "Репорт-файл не обнаружен, возможно к нему еще не обращались";
	} else if ($action == "change_status")
	{
		if ($up_status_login == null && $down_status_login == null) die ("Поля 'Логин для повышения' и 'Логин для понижения' не могут быть пустыми одновременно");
		
		if ($up_status_login != null)
		{
			$logins = explode(", ", $up_status_login);
			for ($i = 0; $i < count($logins); $i++)
			{
				$login = $logins[$i];
				$query	= $db -> query("SELECT $db_colUserStat FROM $db_table WHERE $db_colUser='$login'") or die ($db -> error);
				
				if ($query -> num_rows == 1)
				{
					$row = $query -> fetch_assoc();
					$bdUserStatus = $row[$db_colUserStat];
					
					if ($bdUserStatus != "1")
					{
						$new_status = 1;
						$db -> query("UPDATE $db_table SET $db_colUserStat='$new_status' WHERE $db_colUser='$login'") or die ($db -> error);
						echo ("Статус пользователя '$login' был повышен до '$new_status'<br />");
					} else
					{
						echo ("Указанный пользователь '$login' уже достиг максимального статуса '$bdUserStatus'<br />");
					}
				} else
				{
					echo ("Указанный логин '$login' не найден<br />");
				}
			}
		}
		
		if ($down_status_login != null)
		{
			$logins = explode(", ", $down_status_login);
			for ($i = 0; $i < count($logins); $i++)
			{
				$login = $logins[$i];
				$query	= $db -> query("SELECT $db_colUserStat FROM $db_table WHERE $db_colUser='$login'") or die ($db -> error);
				
				if ($query -> num_rows == 1)
				{
					$row = $query -> fetch_assoc();
					$bdUserStatus = $row[$db_colUserStat];
					
					if ($bdUserStatus != "0")
					{
						$new_status = 0;
						$db -> query("UPDATE $db_table SET $db_colUserStat='$new_status' WHERE $db_colUser='$login'") or die ($db -> error);
						echo ("Статус пользователя '$login' был понижен до '$new_status'<br />");
					} else
					{
						echo ("Указанный пользователь '$login' уже достиг минимального статуса '$bdUserStatus'<br />");
					}
				} else
				{
					echo ("Указанный логин '$login' не найден<br />");
				}
			}
		}
	}
	
	function sql_param($string) 
	{
		global $db;
		(string) $string = $string;
		$string = PREG_REPLACE("/[^\w- ]|INSERT|DELETE|UPDATE|UNION|SET|SELECT|TRUNCATE|DROP|TABLE/i", "", $string);
		$string = TRIM($string);
		$db -> real_escape_string($string);
		return $string;
	}
?>