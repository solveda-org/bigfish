<#if productAtrributeMap?has_content>
	<#assign personalizationDefaultMap = Static["org.ofbiz.base.util.UtilProperties"].getResourceBundleMap("parameters_personalization", locale)>
	
	<#assign personalizationMap = sessionAttributes.personalizationMap!/>
		
	<#assign textPersonalization = productAtrributeMap.PT_TEXT_PERSONALIZE!/>
	<#if textPersonalization?has_content && textPersonalization == "TRUE">
		<li class="${request.getAttribute("attributeClass")!}">
			<div class="entryForm">
			
				<#assign textLinesNum = personalizationDefaultMap.PT_TEXT_LINES_NUM!"3"/>
				<#if productAtrributeMap.PT_TEXT_LINES_NUM?has_content>
					<#assign textLinesNum = productAtrributeMap.PT_TEXT_LINES_NUM!/>
				</#if>
				<#if textLinesNum?has_content && Static["com.osafe.util.Util"].isNumber(textLinesNum) && (textLinesNum?number &gt; 0)>
					<input type="hidden" name="textLinesNum" value="${textLinesNum}"/>
					<div id="textLinesEntry">
						<#assign textLinesNumMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("textLinesNum", textLinesNum)>
		    			<#assign personalizedTextInstructions = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels","PersonalizedTextInstructions",textLinesNumMap, locale ) />
						<p class="instructions"><span>${personalizedTextInstructions!}</span></p>
						<#list 0 .. (textLinesNum?number-1) as idx>
							<#if personalizationMap?has_content>
								<#assign textLineFromMap = personalizationMap.get("textLine_" + idx)!/>
								<#assign fontSizeFromMap = personalizationMap.get("fontSize_" + idx)!/>
							</#if>
							<div class="entry textLinePersonalized textLinePersonalized_${idx!}">
								<#assign lineNumMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("idx", idx + 1)>
		    					<#assign lineNumberText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels","LineNumberCaption",lineNumMap, locale ) />
						        <label>${lineNumberText!}</label>
						        <div class="entryField">
					                <input type="text" class="textLine js_personalizedTextLine" id="textLine_${idx!}" name="textLine_${idx!}" value="${textLineFromMap!requestParameters.get("textLine_" + idx)!""}" onkeyup="javascript:updateImageText('${idx!}');updatePersonalizationMap(this);"/>
					                
					                <#assign textSize = productAtrributeMap.PT_TEXT_SIZE!/>
									<#if textSize?has_content && textSize == "TRUE">
						                <select id="fontSize_${idx!}" name="fontSize_${idx!}" class="fontSize" onChange="javascript:updateImageTextSize('${idx!}');updatePersonalizationMap(this);">
						                	<#assign fontSizeEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "FONT_SIZE"))/>    
						                  	<#assign fontSize = fontSizeFromMap!requestParameters.get("fontSize_" + idx)!"">
						                  	<option value="">${uiLabelMap.FontSizeLabel}</option>
						                  	<#if fontSizeEnums?has_content>
						                  		<#list fontSizeEnums as fontSizeEnum>
						                      		<option value="${fontSizeEnum.enumId?if_exists}"<#if fontSizeEnum.enumId == fontSize> selected</#if>>${fontSizeEnum.description?if_exists}</option>
						                      	</#list>
						                  	</#if>
						            	</select>
					            	</#if>
					            	
						        </div>
							</div>
						</#list>
					</div>
				</#if>
			</div>
		</li>
	</#if>
</#if>
	

<#-- TODO: This DIV needs to be split out into a new DIV in the DIV Sequencer -->

<#if productAtrributeMap?has_content>
	<#assign personalizationDefaultMap = Static["org.ofbiz.base.util.UtilProperties"].getResourceBundleMap("parameters_personalization", locale)>
	<#assign personalizationMap = sessionAttributes.personalizationMap!/>
		
	<#assign textPersonalization = productAtrributeMap.PT_TEXT_PERSONALIZE!/>
	<#if textPersonalization?has_content && textPersonalization == "TRUE">
		<li class="container personalizedText pdpPersonalizedTextFontAlignment">
			<div class="entryForm">
		
				<#assign textLinesNum = personalizationDefaultMap.PT_TEXT_LINES_NUM!"3"/>
				<#if productAtrributeMap.PT_TEXT_LINES_NUM?has_content>
					<#assign textLinesNum = productAtrributeMap.PT_TEXT_LINES_NUM!/>
				</#if>
				<#if textLinesNum?has_content && Static["com.osafe.util.Util"].isNumber(textLinesNum) && (textLinesNum?number &gt; 0)>
					<div id="fontAlignmentEntry">
						<p class="instructions"><span>${uiLabelMap.ChooseFontAlignmentInstructions}</span></p>
						
						<#assign productAtrributeTextFont = productAtrributeMap.PT_TEXT_FONT!/>
						<#if productAtrributeTextFont?has_content && productAtrributeTextFont == "TRUE">
							<#if personalizationMap?has_content>
								<#assign fontEnumFromMap = personalizationMap.get("fontEnum")!/>
							</#if>
							<div class="entry fontEnumPersonalized">
						        <label>${uiLabelMap.FontCaption}</label>
						        <div class="entryField">
					                <select id="js_fontEnum" name="fontEnum" class="fontEnum" onChange="javascript:updateImageTextFont();updatePersonalizationMap(this);">
					                	<#assign fontEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "FONT_FAMILY"))/>    
					                  	<#if personalizationDefaultMap?has_content>
								  			<#assign textAreaDefaultFont = personalizationDefaultMap.PT_DEFAULT_FONT!""/>
								  		</#if>
								  		<#if productAtrributeMap.PT_DEFAULT_FONT?has_content>
											<#assign textAreaDefaultFont = productAtrributeMap.PT_DEFAULT_FONT!""/>
										</#if>
					                  	<#assign font = fontEnumFromMap!parameters.fontEnum!textAreaDefaultFont!"">
					                  	<#if fontEnums?has_content>
					                  		<#list fontEnums as fontEnum>
					                      		<option value="${fontEnum.enumId?if_exists}"<#if fontEnum.enumId == font> selected</#if>>${fontEnum.description?if_exists}</option>
					                      	</#list>
					                  	</#if>
					            	</select>
						        </div>
							</div>
						</#if>
						
						<#assign productAtrributeTextAlign = productAtrributeMap.PT_TEXT_ALIGN!/>
						<#if productAtrributeTextAlign?has_content && productAtrributeTextAlign == "TRUE">
							<#if personalizationMap?has_content>
								<#assign textAlignFromMap = personalizationMap.get("textAlign")!/>
							</#if>
							<div class="entry textAlignPersonalized">
						        <label>${uiLabelMap.FontAlignmentCaption}</label>
						        <div class="entryField">
					                <select id="js_textAlign" name="textAlign" class="textAlign" onChange="javascript:updateImageTextAlignment();updatePersonalizationMap(this);">
					                	<#assign textAlignEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "TEXT_ALIGN"))/>    
					                  	<#if personalizationDefaultMap?has_content>
								  			<#assign textAreaDefaultTextAlign = personalizationDefaultMap.PT_DEFAULT_TEXT_ALIGN!""/>
								  		</#if>
								  		<#if productAtrributeMap.PT_DEFAULT_TEXT_ALIGN?has_content>
											<#assign textAreaDefaultTextAlign = productAtrributeMap.PT_DEFAULT_TEXT_ALIGN!""/>
										</#if>
								  		<#assign textAlign = textAlignFromMap!parameters.textAlignEnum!textAreaDefaultTextAlign!"">
					                  	<#if textAlignEnums?has_content>
					                  		<#list textAlignEnums as textAlignEnum>
					                      		<option value="${textAlignEnum.enumId?if_exists}"<#if textAlignEnum.enumId == textAlign> selected</#if>>${textAlignEnum.description?if_exists}</option>
					                      	</#list>
					                  	</#if>
					            	</select>
						        </div>
							</div>
						</#if>
						
					</div>
				</#if>
		
			</div>
		</li>
	
	</#if>
</#if>