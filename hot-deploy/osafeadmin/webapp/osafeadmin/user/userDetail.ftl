<#if mode?has_content>
	<#if userInfo?exists && userInfo?has_content>
		<#assign passwordHint = userInfo.passwordHint!"" />
		<#assign isSystem = userInfo.isSystem!"N" />
		<#assign enabled = userInfo.enabled!"Y" />
		<#assign hasLoggedOut = userInfo.hasLoggedOut!"Y" />
		<#assign requirePasswordChange = userInfo.requirePasswordChange!"N" />
		<#assign disabledDateTime = userInfo.disabledDateTime!"" />
		<#assign successiveFailedLogins = userInfo.successiveFailedLogins!"" />	
	</#if>
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.UserLoginIdCaption}</label>
	      </div>
	      <div class="infoValue">
	      		<#if mode="add">
	        		<input name="userLoginId" type="text" id="userLoginId" value="${parameters.userLoginId!""}"/>
	        	<#else>
	        		${parameters.userLoginId!""}
	        		<input name="userLoginId" type="hidden" id="userLoginId" value="${parameters.userLoginId!""}"/>
	        	</#if>
	      </div>
	    </div>
	  </div>
	  <#if mode !="add">
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.CurrentPasswordCaption}</label>
	      </div>
	      <div class="infoValue">
	        	<input name="currentPassword" type="password" id="currentPassword" value="${parameters.currentPassword!""}"/>
	        	<a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UserPasswordHelpInfo!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	      </div>
	    </div>
	  </div>
	  </#if> 
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.NewPasswordCaption}</label>
	      </div>
	      <div class="infoValue">
	        	<input name="newPassword" type="password" id="newPassword" value="${parameters.newPassword!""}"/>
	        	<#if mode="edit"><a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UserPasswordHelpInfo!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a></#if>
	      </div>
	    </div>
	  </div>
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.ConfirmPasswordCaption}</label>
	      </div>
	      <div class="infoValue">
	        	<input name="confirmPassword" type="password" id="confirmPassword" value="${parameters.confirmPassword!""}"/>
	        	<#if mode="edit"><a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UserPasswordHelpInfo!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a></#if>
	      </div>
	    </div>
	  </div>  
	  <div class="infoRow row">
		  <div class="infoEntry long">
		    <div class="infoCaption">
		      <label>${uiLabelMap.PasswordHintCaption}</label>
		    </div>
		    <div class="infoValue">
		      <textarea name="passwordHint" class="shortArea" cols="50" rows="5">${parameters.passwordHint!passwordHint!""}</textarea>
		    </div>
		  </div>
	  </div> 
	  <div class="infoRow row">
		  <div class="infoEntry long">
		    <div class="infoCaption">
		      <label>${uiLabelMap.IsSystemCaption}</label>
		    </div>
		    <div class="infoValue">
		      <div class="entry checkbox short">
                     <input class="checkBoxEntry" type="radio" name="isSystem"  value="Y" <#if isSystem?exists && isSystem?string == "Y">checked</#if>  disabled/>${uiLabelMap.YesLabel}
                     <input class="checkBoxEntry" type="radio" name="isSystem" value="N" <#if isSystem?exists && isSystem?string == "N">checked</#if> <#if mode = 'add'>checked</#if>  disabled/>${uiLabelMap.NoLabel}
              </div>
		    </div>
		  </div>
	  </div>  
	  <#if mode !="add">
	  <div class="infoRow row">
		  <div class="infoEntry long">
		    <div class="infoCaption">
		      <label>${uiLabelMap.HasLoggedOutCaption}</label>
		    </div>
		    <div class="infoValue">
		      <div class="entry checkbox short">
                     <input class="checkBoxEntry" type="radio" name="hasLoggedOut"  value="Y" <#if hasLoggedOut?exists && hasLoggedOut?string == "Y">checked</#if> disabled/>${uiLabelMap.YesLabel}
                     <input class="checkBoxEntry" type="radio" name="hasLoggedOut" value="N" <#if  hasLoggedOut?exists && hasLoggedOut?string == "N">checked</#if> disabled/>${uiLabelMap.NoLabel}
              </div>
		    </div>
		  </div>
	  </div>
	  </#if> 
	  <#if mode="edit">
	  	<#assign successFailedLogins = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("security.properties", "max.failed.logins")>
	  	<#assign successFailedLoginMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("successFailedLoginsNumber", successFailedLogins)>
      	<#assign successFailedLoginToolTipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SuccessFailedLoginsHelpInfo",successFailedLoginMap, locale ) />
	  </#if>
	  <div class="infoRow row">
		  <div class="infoEntry long">
		    <div class="infoCaption">
		      <label>${uiLabelMap.EnabledCaption}</label>
		    </div>
		    <div class="infoValue">
		      <div class="entry checkbox short">
                     <input class="<#if mode != 'add' >USER_ENABLED_CHECKBOX_ </#if>checkBoxEntry" type="radio" name="enabled"  value="Y" <#if mode = 'add'> checked disabled <#else><#if parameters.enabled?exists && parameters.enabled?string == "Y">checked<#elseif enabled?exists && enabled?string == "Y">checked</#if></#if>/>${uiLabelMap.YesLabel}
                     <input class="<#if mode != 'add' >USER_ENABLED_CHECKBOX_ </#if>checkBoxEntry" type="radio" name="enabled" value="N" <#if  parameters.enabled?exists && parameters.enabled?string == "N">checked="checked" <#elseif enabled?exists && enabled?string == "N">checked</#if> <#if mode = 'add'>disabled</#if>/>${uiLabelMap.NoLabel}
                     <#if mode = 'add'><input type='hidden' name='enabled' value='Y'/></#if>
              </div>
              <#if mode="edit"><a href="javascript:void(0);" onMouseover="showTooltip(event,'${successFailedLoginToolTipText!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a></#if>
		    </div>
		  </div>
		  
	  </div>  
	  <#if mode !="add">
		  <div class="infoRow row">
			  <div class="infoEntry">
		      <div class="infoCaption">
		        	<label>${uiLabelMap.DisabledDateTimeCaption}</label>
		      </div>
		      <div class="infoValue">
		        	<input <#if mode != 'add' >id="USER_DISABLED_DATE" </#if>class="<#if mode = 'add'>textEntry<#else>dateEntry</#if>" type="text" id="DISABLED_DATE" name="DISABLED_DATE" value="${parameters.DISABLED_DATE!disDate!""}" <#if mode = 'add'>disabled</#if>/>
		      </div>
		    </div>
		  </div>
	  </#if>
	  <#if mode !="add">
		  <#assign successFailedMinutesLogins = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("security.properties", "login.disable.minutes")>
		  <#assign successFailedMinutesMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("numberOfminutes", successFailedMinutesLogins)>
	      <#assign successFailedMinutesToolTipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SuccessFailedNumberMinutesHelpInfo",successFailedMinutesMap, locale ) />
	
		  <div class="infoRow">
		    <div class="infoEntry">
		    	  <div class="infoCaption">
			        	<label>${uiLabelMap.DisabledTimeCaption}</label>
			      </div>
			      <div class="infoValue">
			        <div class="entryInput"> 
		                  <!-- DISABLED hour -->
		                  <select <#if mode != 'add' >id="USER_DISABLED_HOUR" </#if> name="DISABLED_HOUR" <#if mode = 'add'>disabled</#if>>
		                  <#assign disHour = requestParameters.DISABLED_HOUR!disHour!"">
		                  <#if disHour?has_content && (disHour?length gt 0)>
		                      <option value="${disHour?if_exists}">${disHour?if_exists}</option>
		                  </#if>
		                  <option value="">${uiLabelMap.CommonHH}</option>
		                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ddHours")}
		                  </select>
		
		                  <!-- DISABLED minute -->
		                  <select <#if mode != 'add' >id="USER_DISABLED_MINUTE" </#if> name="DISABLED_MINUTE" <#if mode = 'add'>disabled</#if>>
		                  <#assign disMinute = requestParameters.DISABLED_MINUTE!disMinute!"">
		                  <#if disMinute?has_content && (disMinute?length gt -1)>
		                      <option value="${disMinute?if_exists}">${disMinute?if_exists}</option>
		                  </#if>
		                  <option value="">${uiLabelMap.CommonMM}</option>
		                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ddMinuts")}
		                  </select>
		
		                  <!-- DISABLED AMPM -->
		        		  <#assign selectedAMPM = parameters.DISABLED_AMPM!disTimeAMPM!""/>
						  <select <#if mode != 'add' >id="USER_DISABLED_AMPM" </#if>name="DISABLED_AMPM" <#if mode = 'add'>disabled</#if>>
	                        <option value="">${uiLabelMap.CommonAMPM}</option>
							<option value='1'<#if selectedAMPM == "1">selected=selected</#if>>${uiLabelMap.CommonAM}</option>
							<option value='2'<#if selectedAMPM == "2">selected=selected</#if>>${uiLabelMap.CommonPM}</option>
					      </select>
					      
					      <a href="javascript:void(0);" onMouseover="showTooltip(event,'${successFailedMinutesToolTipText!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
			        </div>
			      </div>
			 </div>
		  </div>
	  </#if>
	  <div class="infoRow row">
		  <div class="infoEntry long">
		    <div class="infoCaption">
		      <label>${uiLabelMap.RequirePasswordChangeCaption}</label>
		    </div>
		    <div class="infoValue">
		      <div class="entry checkbox short">
                     <input class="checkBoxEntry" type="radio" name="requirePasswordChange"  value="Y" <#if parameters.requirePasswordChange?exists && parameters.requirePasswordChange?string == "Y">checked="checked"<#elseif requirePasswordChange?exists && requirePasswordChange?string == "Y">checked="checked"</#if> <#if mode = 'add'>disabled</#if>/>${uiLabelMap.YesLabel}
                     <input class="checkBoxEntry" type="radio" name="requirePasswordChange" value="N" <#if parameters.requirePasswordChange?exists && parameters.requirePasswordChange?string == "N">checked="checked" <#elseif requirePasswordChange?exists && requirePasswordChange?string == "N">checked="checked"</#if> <#if mode = 'add'>disabled checked</#if>/>${uiLabelMap.NoLabel}
              </div>
              <#if mode="edit"><a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.UserRequirePasswordResetHelpInfo!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a></#if>
		    </div>
		  </div>
	  </div>
	  <#if mode !="add">
	  	<#assign maxFailedLogins = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("security.properties", "max.failed.logins")>
		  <#assign maxFailedLoginsMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("maxFailedLogins", maxFailedLogins)>
	      <#assign successiveFailedLoginsToolTipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","MaxFailedLoginsMapHelpInfo",maxFailedLoginsMap, locale ) />
		  <div class="infoRow">
		    <div class="infoEntry">
		      <div class="infoCaption">
		        	<label>${uiLabelMap.SuccFailedLoginsCaption}</label>
		      </div>
		      <div class="infoValue">
		        	<input class="small" name="successiveFailedLogins" type="text" id="successiveFailedLogins" value="${parameters.successiveFailedLogins!successiveFailedLogins!""}" disabled/>
		        	<a href="javascript:void(0);" onMouseover="showTooltip(event,'${successiveFailedLoginsToolTipText!}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
		      </div>
		    </div>
		  </div>
	  </#if> 
 
<#else>
  	 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
