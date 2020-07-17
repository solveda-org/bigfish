import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

shoppingLists = delegator.findByAndCache("ShoppingList", [partyId : userLogin.partyId, shoppingListTypeId : "SLT_AUTO_REODR"],UtilMisc.toList("isActive DESC"));
context.shoppingLists = shoppingLists;

