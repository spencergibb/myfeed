<!DOCTYPE html>

<html lang="en">

<body>
<h2>Myfeed Admin: Users</h2>

<ul>
<#list users as user>
    <li>${user.content.username}: ${user.content.name} ${user.content.userId!""}</li>
</#list>
</ul>
</body>

</html>