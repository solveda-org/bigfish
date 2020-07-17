package jobs;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);

runtimeDataId = StringUtils.trimToEmpty(parameters.runtimeDataId);
context.runtimeDataId = runtimeDataId;
if (UtilValidate.isNotEmpty(runtimeDataId))
{
	runtimeData = delegator.findByPrimaryKey("RuntimeData", [runtimeDataId : runtimeDataId]);
	context.runtimeData = runtimeData;
}


jobId = StringUtils.trimToEmpty(parameters.jobId);
context.jobId = jobId;
if (UtilValidate.isNotEmpty(jobId))
{
	schedJob = delegator.findByPrimaryKey("JobSandbox", [jobId : jobId]);
	context.schedJob = schedJob;
	detailInfoBoxHeading = FlexibleStringExpander.expandString(UtilProperties.getMessage("OSafeAdminUiLabels","RuntimeDataParametersHeading",locale), context);
	
	context.detailInfoBoxHeading  = detailInfoBoxHeading;
}