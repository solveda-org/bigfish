<script type="text/javascript">
    function submitForm(form) {
        form.loginUserName.value = document.loginform.USERNAME.value;
    }
</script>
<div class="displayBoxList">
${screens.render("component://osafe/widget/EcommerceDivScreens.xml#signInDivSequence")}
</div>