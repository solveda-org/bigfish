<#assign requestUri =  Static["org.ofbiz.base.util.UtilHttp"].getRequestUriFromTarget(request.getRequestURL())/> 
<div id="siteInfo">
   <div id="welcome">
      <p>${uiLabelMap.WelcomeCaption} <span class="name"><#if userLoginFullName?has_content>${userLoginFullName}<#else>${userLogin.userLoginId}</#if>&nbsp;[${(adminModuleName)?if_exists}]</span></p>
      
      <#if stores?has_content && (stores.size() > 1)>
		  <form name="chooseProductStore" method="post" action="<@ofbizUrl>main</@ofbizUrl>">
          <p>${uiLabelMap.ProductStoreCaption}
		    <select id="selectProductStore" name="selectedProductStoreId" onchange="submit()">
		      <#list stores as productStore>
		        <option value='${productStore.productStoreId}'<#if productStore.productStoreId == globalContext.productStoreId> selected</#if>>${productStore.storeName!""}</option>
		      </#list>
		    </select>
		 </p>
		  </form>
      <#else>
          <p>${uiLabelMap.ProductStoreCaption} <span class="name">[${globalContext.productStoreName?if_exists}]</span></p>
      </#if>
      <p>${uiLabelMap.CatalogCaption} <span class="name">[${globalContext.prodCatalogName?if_exists}]</span></p>
    </div>
   <div id="helperImages">
		   <a class="standardBtn help" href="${ADM_HELP_URL!}${helperFileName!"index.htm"}" target="_blank" >${uiLabelMap.HelpBtn}</a>
		   <a class="standardBtn logout" href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.LogoutBtn}</a>
    </div>
</div>

