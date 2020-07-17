package user;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import com.osafe.util.OsafeAdminUtil;


//get entity for UserLogin --> securityGroupInfo
groupId = StringUtils.trimToEmpty(parameters.groupId);
context.groupId = groupId;
if (UtilValidate.isNotEmpty(groupId))
{
    securityGroupInfo = delegator.findByPrimaryKey("SecurityGroup", [groupId : groupId]);
    context.securityGroupInfo = securityGroupInfo;	
}




