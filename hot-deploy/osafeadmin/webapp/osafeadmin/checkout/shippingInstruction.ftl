<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoValue withOutCaption">
            <textarea class="smallArea" name="shipping_instructions">${parameters.shipping_instructions!shippingInstructions!""}</textarea>
        </div>
        <div class="infoIcon">
            <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "ShippingInstructionInfo", locale)/>
            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
        </div>
    </div>
</div>