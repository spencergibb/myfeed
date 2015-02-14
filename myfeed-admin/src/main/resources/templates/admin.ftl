<!DOCTYPE html>

<html lang="en">

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
</ul>
</body>

</html>