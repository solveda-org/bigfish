<#if bigfishParamList?has_content>  
  <#assign selectedKey = ""/>
  <#if mode=="edit">
    <#assign selectedKey = parameters.key!""/>
  </#if>
  <input type="hidden" name="key" value="${selectedKey!""}"/>
  <input type="hidden" name="parameterFileName" value="${parameters.parameterFileName!""}"/>
  <input type="hidden" name="mode" value="${mode!""}"/>
  <input type="hidden" name="showDetail" value="true"/>
  <#list bigfishParamList as bigfishParam>
    <#assign key = bigfishParam.key!"" />
    <#assign description = bigfishParam.description!"" />
    <#assign value = bigfishParam.value!"" />
    <#assign hasNext = bigfishParam_has_next/>
    <#-- if mode equals add and we reached the last List Item (which is the empty map we added in groovy), then display this-->
    <#if (selectedKey == key) || (mode == "add" && !hasNext)>
      <div class="infoRow">
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.KeyCaption}</label></div>
                <div class="infoValue">
                  <#if mode=="edit">
                    <input type="hidden" name="key_${bigfishParam_index}" value="${key!}"/>${key!""}
                  <#elseif mode=="add">
                    <input type="text" id="divSequenceKey" name="key_${bigfishParam_index}" value="${parameters.get("key_${bigfishParam_index}")!key!""}"/>
                  </#if>
               </div>
            </div>
        </div>
        <div class="infoRow">
            <div class="infoEntry long">
                <div class="infoCaption"><label>${uiLabelMap.DescriptionCaption}</label></div>
               <div class="infoValue">
                  <textarea class="smallArea" name="description_${bigfishParam_index}" cols="50" rows="1">${parameters.get("description_${bigfishParam_index}")!description!}</textarea>
               </div>
            </div>
        </div>
       <#-- ====== Value ==== -->
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.ValueCaption}</label></div>
                <div class="infoValue">
                    <input type="text" class="large" name="value_${bigfishParam_index}" value="${parameters.get("value_${bigfishParam_index}")!value!}"/>
                </div>
            </div>
        </div>
    <#else>
      <input type="hidden" name="key_${bigfishParam_index}" value="${key!}"/>
      <input type="hidden" name="description_${bigfishParam_index}" value="${description!}"/>
      <input type="hidden" name="value_${bigfishParam_index}" value="${value!}"/>
    </#if>
  </#list>
</#if>
