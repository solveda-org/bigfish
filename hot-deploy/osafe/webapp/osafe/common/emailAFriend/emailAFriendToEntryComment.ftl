<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="content"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.CommentCaption}</label>
      <div class="entryField">
          <textarea class="content" name="comment" id="js_content" rows="5" cols="35">${parameters.comment!""}</textarea>
          <input type="hidden" name="comment_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="comment"/>
          <div class="counter">
            <span class="js_textCounter textCounter" id="js_textCounter"></span>
          </div>
      </div>
</div>