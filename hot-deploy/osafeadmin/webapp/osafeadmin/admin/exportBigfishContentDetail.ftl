  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />

      <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.EntitytoExportCaption}</label>
            </div>
             <#assign intiCb = "${initializedCB}"/>
             <div class="entry checkbox medium">
                 <input type="checkbox" class="checkBoxEntry" name="exportall" id="exportall" value="Y" onclick="javascript:setCheckboxes('${detailFormName!""}','export')" <#if parameters.exportall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentLibrary" id="exportContentLibrary" value="Y" <#if parameters.exportContentLibrary?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentLibraryLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentSiteInfo" id="exportContentSiteInfo" value="Y" <#if parameters.exportContentSiteInfo?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentSiteInfoLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentHomePage" id="exportContentHomePage" value="Y" <#if parameters.exportContentHomePage?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentHomePageLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentStaticPage" id="exportContentStaticPage" value="Y" <#if parameters.exportContentStaticPage?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentStaticPageLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentPageTop" id="exportContentPageTop" value="Y" <#if parameters.exportContentPageTop?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentPageTopLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentPDPSpot" id="exportContentPDPSpot" value="Y" <#if parameters.exportContentPDPSpot?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentPDPSpotLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentProdCat" id="exportContentProdCat" value="Y" <#if parameters.exportContentProdCat?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentProductCategorySpotsLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportPromotions" id="exportPromotions" value="Y" <#if parameters.exportPromotions?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportPromotionsLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentEmail" id="exportContentEmail" value="Y" <#if parameters.exportContentEmail?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentEmailLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportContentTxtTemplate" id="exportContentTxtTemplate" value="Y" <#if parameters.exportContentTxtTemplate?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportContentTxtTemplateLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportPageTagging" id="exportPageTagging" value="Y" <#if parameters.exportPageTagging?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportPageTaggingLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportPaymentGatewaySettings" id="exportPaymentGatewaySettings" value="Y" <#if parameters.exportPaymentGatewaySettings?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportPaymentGatewaySettingsLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportShippingCharges" id="exportShippingCharges" value="Y" <#if parameters.exportShippingCharges?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportShippingChargesLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportSalesTaxes" id="exportSalesTaxes" value="Y" <#if parameters.exportSalesTaxes?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportSalesTaxesLabel}</br>
                 <input type="checkbox" class="checkBoxEntry" name="exportStores" id="exportStores" value="Y" <#if parameters.exportStores?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportStoresLabel}</br>
             </div>
        </div>
      </div>

  <#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>