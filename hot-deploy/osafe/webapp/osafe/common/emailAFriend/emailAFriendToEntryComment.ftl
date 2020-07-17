<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="content"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.CommentCaption}</label>
      <div class="entryField">
          <textarea class="content characterLimit" name="comment" id="js_content" rows="5" cols="35" maxlength="255">${parameters.comment!""}</textarea>
          <span class="js_textCounter textCounter" id="js_textCounter"></span>
          <input type="hidden" name="comment_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="comment"/>
      </div>
</div>