<#-- @ftlvariable name="" type="views.PersonView" -->
<html>
<body>
<!-- calls getPerson().getFirstName() and sanitizes it -->
<h1>Hello, ${person.name?html}!</h1>
</body>
</html>