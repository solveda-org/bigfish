<form method="post" action="/errorPage" id="errorform"  name="errorform"></form>
<script type="text/javascript">
   <#assign currentRequest = request.getPathInfo() >
   <#if !currentRequest?contains("errorPage") >
       document.errorform.submit();
   </#if>
</script>