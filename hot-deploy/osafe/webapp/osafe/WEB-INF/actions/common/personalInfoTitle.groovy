import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import javolution.util.FastList;
import java.lang.*;
import org.ofbiz.entity.GenericValue;

personTitleList = delegator.findByAndCache("Enumeration", [enumTypeId : "PERSONAL_TITLE"], ["sequenceId"]);
if(UtilValidate.isNotEmpty(personTitleList))
{
    personTitles = FastList.newInstance();
    for (GenericValue personTitle :  personTitleList) 
    {
        if(UtilValidate.isNotEmpty(personTitle.sequenceId) && (personTitle.sequenceId instanceof String) && (UtilValidate.isInteger(personTitle.sequenceId)))
        {
            seqId = Integer.parseInt(personTitle.sequenceId);
            if(seqId > 0)
            {
                personTitles.add(personTitle);
            }
        }
    }
}
context.personTitleList = personTitles;