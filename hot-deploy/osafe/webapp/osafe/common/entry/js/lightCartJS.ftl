<script type="text/javascript">
    function submitLightBoxCheckoutForm(form, mode, value) 
    {
        if (mode == "VDN") {
            <#-- validate and then done action -->
            if (validateCart()) {
                form.action="<@ofbizUrl>${doneAction!""}</@ofbizUrl>";
            	form.submit();
            }
        }
    }
</script>
