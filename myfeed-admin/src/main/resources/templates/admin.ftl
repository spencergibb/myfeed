<!DOCTYPE html>

<html lang="en">
<head>
    <title>Myfeed Admin</title>
</head>
<body>
<h2>Myfeed Admin</h2>
<ul>
    <li><a href="hystrix">Hystrix</a> </li>
<#if routerUrl?has_content>
    <li><a href="hystrix/monitor?stream=${routerUrl}/hystrix.stream">hystrix www</a> </li>
</#if>
<#if turbineUrl?has_content>
    <li><a href="hystrix/monitor?stream=${turbineUrl}">hystrix turbine</a> </li>
</#if>
    <li><a href="users">Users</a> </li>
    <li><a href="${discoveryUrl}">Discovery</a> </li>
</ul>
</body>

</html>