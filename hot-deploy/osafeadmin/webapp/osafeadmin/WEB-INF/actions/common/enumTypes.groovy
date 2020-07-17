import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import javolution.util.FastList;
import java.lang.*;
import org.ofbiz.entity.GenericValue;

if (UtilValidate.isNotEmpty(context.enumTypeId)) 
{
    enumTypeList = delegator.findByAnd("Enumeration", [enumTypeId : context.enumTypeId], ["sequenceId"]);
	processEnumTypes = FastList.newInstance();
    if(UtilValidate.isNotEmpty(enumTypeList))
    {
        for (GenericValue enumType :  enumTypeList) 
        {
            if(UtilValidate.isNotEmpty(enumType.sequenceId) && (enumType.sequenceId instanceof String) && (UtilValidate.isInteger(enumType.sequenceId)))
            {
                seqId = Integer.parseInt(enumType.sequenceId);
                if(seqId > 0)
                {
                    processEnumTypes.add(enumType);
                }
            }
        }
    }
    context.enumTypes = processEnumTypes;
}